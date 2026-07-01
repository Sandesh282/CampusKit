package com.example.campuskit.data.assistant.vector

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single embedded text chunk stored in the local vector store.
 * Each chunk represents one piece of campus data (e.g. one subject's attendance,
 * one event, one day's mess menu) as a natural-language sentence.
 *
 * [embedding] is a 384-dim L2-normalised float vector produced by all-MiniLM-L6-v2.
 * Because vectors are unit-length, cosine similarity == dot product (no sqrt needed).
 */
@Entity(tableName = "vector_chunks")
data class VectorChunkEntity(
    @PrimaryKey val id: String,       // e.g. "attendance:COA", "event:HackFest25"
    val text: String,                 // natural language chunk passed as context to Gemini
    val embedding: FloatArray,        // 384 floats, stored as BLOB via FloatArrayConverter
    val source: String,               // "attendance" | "event" | "mess" | "guide"
    val indexedAt: Long = System.currentTimeMillis(),
) {
    // FloatArray doesn't implement equals/hashCode by default
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VectorChunkEntity) return false
        return id == other.id && text == other.text && embedding.contentEquals(other.embedding)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + embedding.contentHashCode()
        return result
    }
}
