package com.example.campuskit.data.assistant

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
 * Builds a grounded context string from Room/static data to inject into the Gemini prompt.
 * This is the "retrieval" part of the RAG-inspired architecture.
 *
 * Keyword routing: we inspect the query to decide which data sources are relevant,
 * then format them as natural language sentences the model can reason over.
 */
@Singleton
class AssistantContextBuilder @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val eventsRepository: EventsRepository,
) {

    /**
     * Returns a natural-language context block relevant to the [query].
     * Multiple sections can be appended if the query spans topics.
     */
    suspend fun buildContext(query: String): String {
        val q = query.lowercase()
        val builder = StringBuilder()

        // Attendance context
        if (q.containsAny("attendance", "bunk", "class", "skip", "safe", "percentage", "present", "absent")) {
            val subjects = attendanceRepository.getAllSubjects().first()
            if (subjects.isNotEmpty()) {
                builder.appendLine("ATTENDANCE RECORDS:")
                subjects.forEach { subject ->
                    val pct = if (subject.totalClasses > 0)
                        (subject.attendedClasses * 100f / subject.totalClasses).toInt()
                    else 0
                    val safeBunks = if (subject.totalClasses > 0) {
                        val required = (subject.minimumPercentage / 100f * subject.totalClasses).toInt()
                        maxOf(0, subject.attendedClasses - required)
                    } else 0
                    builder.appendLine(
                        "- ${subject.subjectName}: attended ${subject.attendedClasses} " +
                        "out of ${subject.totalClasses} classes ($pct%). " +
                        "Safe bunks remaining: $safeBunks."
                    )
                }
            } else {
                builder.appendLine("ATTENDANCE RECORDS: No attendance data recorded yet.")
            }
        }

        // Mess menu context
        if (q.containsAny("menu", "food", "eat", "mess", "breakfast", "lunch", "dinner", "snack", "today", "meal")) {
            // Check if a specific day is mentioned, else default to today
            val targetDay = detectDay(q)
            val dayMenu = MessMenuData.getWeeklyMenu().firstOrNull { it.day == targetDay }
                ?: MessMenuData.getTodayMenu()
            val dayLabel = targetDay.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

            builder.appendLine("MESS MENU FOR $dayLabel:")
            builder.appendLine("Breakfast: ${dayMenu.breakfast.joinToString()}")
            builder.appendLine("Lunch: ${dayMenu.lunch.joinToString()}")
            builder.appendLine("Snacks: ${dayMenu.snacks.joinToString()}")
            builder.appendLine("Dinner: ${dayMenu.dinner.joinToString()}")
        }

        // Events context
        if (q.containsAny("event", "fest", "happening", "seminar", "workshop", "competition", "lecture", "tournament")) {
            val events = eventsRepository.getEvents().first()
            if (events.isNotEmpty()) {
                builder.appendLine("UPCOMING CAMPUS EVENTS:")
                events.take(6).forEach { event ->
                    builder.appendLine("- ${event.title} on ${event.dateTime} at ${event.venue}, organized by ${event.organizer}.")
                    if (event.description.isNotBlank()) {
                        builder.appendLine("  Details: ${event.description}")
                    }
                }
            } else {
                builder.appendLine("UPCOMING CAMPUS EVENTS: No events found.")
            }
        }

        // Campus guide context
        if (q.containsAny("restaurant", "hotel", "food place", "shop", "mall", "near", "landmark", "where", "nearby")) {
            val places = CampusGuideCatalog.getAll()
            builder.appendLine("NEARBY PLACES:")
            places.forEach { place ->
                builder.appendLine("- ${place.name} (${place.category}): ${place.address}, ${place.distance} from campus.")
            }
        }

        return builder.toString().trim().ifBlank {
            "No specific campus data matched this query. Answer based on general college knowledge if possible."
        }
    }

    /** Detects a day of the week mentioned in the query, defaults to today. */
    private fun detectDay(query: String): DayOfWeek {
        return when {
            "monday" in query -> DayOfWeek.MONDAY
            "tuesday" in query -> DayOfWeek.TUESDAY
            "wednesday" in query -> DayOfWeek.WEDNESDAY
            "thursday" in query -> DayOfWeek.THURSDAY
            "friday" in query -> DayOfWeek.FRIDAY
            "saturday" in query -> DayOfWeek.SATURDAY
            "sunday" in query -> DayOfWeek.SUNDAY
            else -> java.time.LocalDate.now().dayOfWeek
        }
    }

    private fun String.containsAny(vararg keywords: String) =
        keywords.any { this.contains(it) }
}
