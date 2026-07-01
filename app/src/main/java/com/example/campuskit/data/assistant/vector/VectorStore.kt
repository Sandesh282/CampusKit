package com.example.campuskit.data.assistant.vector

import com.example.campuskit.data.assistant.embedding.ONNXEmbeddingService
import com.example.campuskit.data.attendance.AttendanceRepository
import com.example.campuskit.data.campusguide.CampusGuideCatalog
import com.example.campuskit.data.events.EventsRepository
import com.example.campuskit.data.mess.MessMenuData
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The heart of the RAG pipeline.
 *
 * [indexAll] converts all campus data into natural-language text chunks, embeds them
 * using the on-device ONNX model, and persists the embeddings to Room.
 *
 * [search] embeds the user's query and performs a cosine similarity search
 * (dot product, since embeddings are L2-normalised) to return the top-k most
 * semantically relevant chunks.
 *
 * Cosine similarity on ~few-hundred vectors takes <5ms in Kotlin — no need for sqlite-vec or FAISS.
 */
@Singleton
class VectorStore @Inject constructor(
    private val dao: VectorChunkDao,
    private val embeddingService: ONNXEmbeddingService,
    private val attendanceRepository: AttendanceRepository,
    private val eventsRepository: EventsRepository,
) {

    companion object {
        const val SOURCE_ATTENDANCE = "attendance"
        const val SOURCE_EVENT = "event"
        const val SOURCE_MESS = "mess"
        const val SOURCE_GUIDE = "guide"
    }

    // ─── Retrieval ────────────────────────────────────────────────────────────

    /**
     * Embeds [queryEmbedding] (already computed) and returns the [topK] most
     * semantically similar chunk texts from the local vector store.
     */
    suspend fun search(queryEmbedding: FloatArray, topK: Int = 5): List<String> {
        val chunks = dao.getAll()
        if (chunks.isEmpty()) return emptyList()

        return chunks
            .map { chunk -> chunk to dotProduct(queryEmbedding, chunk.embedding) }
            .sortedByDescending { it.second }
            .take(topK)
            .map { it.first.text }
    }

    /** Dot product of two unit vectors == cosine similarity. */
    private fun dotProduct(a: FloatArray, b: FloatArray): Float {
        var sum = 0f
        for (i in a.indices) sum += a[i] * b[i]
        return sum
    }

    // ─── Indexing ─────────────────────────────────────────────────────────────

    /** Re-indexes all data sources. Safe to call multiple times (uses upsert). */
    suspend fun indexAll() {
        indexAttendance()
        indexEvents()
        indexMessMenu()
        indexCampusGuide()
    }

    private suspend fun indexAttendance() {
        val subjects = attendanceRepository.getAllSubjects().first()
        dao.deleteBySource(SOURCE_ATTENDANCE)

        val chunks = subjects.map { subject ->
            val pct = if (subject.totalClasses > 0)
                (subject.attendedClasses * 100f / subject.totalClasses).toInt() else 0
            val safeBunks = if (subject.totalClasses > 0) {
                val required = (subject.minimumPercentage / 100f * subject.totalClasses).toInt()
                maxOf(0, subject.attendedClasses - required)
            } else 0

            val text = "For ${subject.subjectName}, the student has attended " +
                "${subject.attendedClasses} out of ${subject.totalClasses} classes ($pct%). " +
                "Safe bunks remaining: $safeBunks."

            VectorChunkEntity(
                id = "$SOURCE_ATTENDANCE:${subject.subjectName}",
                text = text,
                embedding = embeddingService.embed(text),
                source = SOURCE_ATTENDANCE,
            )
        }
        dao.upsert(chunks)
    }

    private suspend fun indexEvents() {
        val events = eventsRepository.getEvents().first()
        dao.deleteBySource(SOURCE_EVENT)

        val chunks = events.map { event ->
            val text = "${event.title} is on ${event.dateTime} at ${event.venue}, " +
                "organised by ${event.organizer}." +
                if (event.description.isNotBlank()) " Details: ${event.description}" else ""

            VectorChunkEntity(
                id = "$SOURCE_EVENT:${event.id}",
                text = text,
                embedding = embeddingService.embed(text),
                source = SOURCE_EVENT,
            )
        }
        dao.upsert(chunks)
    }

    private suspend fun indexMessMenu() {
        dao.deleteBySource(SOURCE_MESS)

        val chunks = MessMenuData.getWeeklyMenu().flatMap { dayMenu ->
            val dayLabel = dayMenu.day.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            listOf(
                "breakfast" to dayMenu.breakfast,
                "lunch" to dayMenu.lunch,
                "snacks" to dayMenu.snacks,
                "dinner" to dayMenu.dinner,
            ).map { (meal, items) ->
                val text = "The $meal menu for $dayLabel includes ${items.joinToString()}."
                VectorChunkEntity(
                    id = "$SOURCE_MESS:${dayMenu.day.value}:$meal",
                    text = text,
                    embedding = embeddingService.embed(text),
                    source = SOURCE_MESS,
                )
            }
        }
        dao.upsert(chunks)
    }

    private suspend fun indexCampusGuide() {
        dao.deleteBySource(SOURCE_GUIDE)

        val chunks = CampusGuideCatalog.getAll().map { place ->
            val text = "${place.name} is a ${place.category} located ${place.distance} from campus at ${place.address}."
            VectorChunkEntity(
                id = "$SOURCE_GUIDE:${place.name}",
                text = text,
                embedding = embeddingService.embed(text),
                source = SOURCE_GUIDE,
            )
        }
        dao.upsert(chunks)
    }
}
