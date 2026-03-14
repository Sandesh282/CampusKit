package com.example.campuskit.data.calendar

import androidx.compose.ui.graphics.Color
import com.example.campuskit.ui.theme.CalendarGreen
import com.example.campuskit.ui.theme.CalendarLavender
import com.example.campuskit.ui.theme.CalendarPeach
import com.example.campuskit.ui.theme.CalendarPink
import com.example.campuskit.ui.theme.CalendarSkyBlue
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

/**
 * Domain model representing a calendar event.
 *
 * Used by the Calendar feature's grid and timeline views.
 * Backed by [CalendarEventEntity] in Room.
 *
 * @param id unique identifier
 * @param title event title
 * @param date the date of the event
 * @param startTime start time
 * @param endTime end time
 * @param color display color for the event block
 */
/**
 * Domain model representing a calendar event.
 *
 * Used by the Calendar feature grid and timeline views.
 * Backed by [CalendarEventEntity] in Room.
 */
data class CalendarEvent(
    val id: String,
    val title: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val color: Color,
    val assignee: String = "",
    val attendeeCount: Int = 0,
    val location: String = "",
    val description: String = "",
)

/**
 * Represents course attendance data for the calendar grid overlay.
 *
 * @param name course name
 * @param color display color for attendance dots
 * @param datesAttended set of dates the student attended this course
 */
data class CourseInfo(
    val name: String,
    val color: Color,
    val datesAttended: Set<LocalDate> = emptySet(),
)

/** Provides static mock calendar events and course data for first-launch demo. */
object MockCalendarData {

    private val today: LocalDate = LocalDate.now()

    fun getEvents(): List<CalendarEvent> = listOf(
        CalendarEvent(
            id = "c1",
            title = "Research Plan",
            date = today,
            startTime = LocalTime.of(9, 20),
            endTime = LocalTime.of(10, 45),
            color = CalendarPink,
            assignee = "Wade Warren",
        ),
        CalendarEvent(
            id = "c2",
            title = "Team Meeting",
            date = today,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 0),
            color = CalendarPeach,
            attendeeCount = 8,
        ),
        CalendarEvent(
            id = "c3",
            title = "Design Review on Dashboard",
            date = today,
            startTime = LocalTime.of(12, 20),
            endTime = LocalTime.of(14, 30),
            color = CalendarSkyBlue,
            assignee = "Leslie Alexander",
        ),
        CalendarEvent(
            id = "c4",
            title = "Sprint Planning",
            date = today.plusDays(1),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            color = CalendarPink,
            attendeeCount = 5,
        ),
        CalendarEvent(
            id = "c5",
            title = "Code Review Session",
            date = today.plusDays(1),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 30),
            color = CalendarSkyBlue,
            assignee = "Alex Johnson",
        ),
        CalendarEvent(
            id = "c6",
            title = "UI Workshop",
            date = today.plusDays(2),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(11, 0),
            color = CalendarPeach,
            attendeeCount = 12,
        ),
        CalendarEvent(
            id = "c7",
            title = "Project Demo",
            date = today.minusDays(1),
            startTime = LocalTime.of(15, 0),
            endTime = LocalTime.of(16, 0),
            color = CalendarPink,
            assignee = "Sarah Chen",
        ),
        CalendarEvent(
            id = "c8",
            title = "Standup",
            date = today.minusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(9, 30),
            color = CalendarSkyBlue,
            attendeeCount = 6,
        ),
    )

    private fun YearMonth.safeDay(day: Int): LocalDate =
        atDay(day.coerceAtMost(lengthOfMonth()))

    fun getCourses(): List<CourseInfo> {
        val m = YearMonth.from(today)
        return listOf(
            CourseInfo(
                name = "UI UX Design",
                color = CalendarGreen,
                datesAttended = listOf(1, 2, 3, 7, 8, 13, 14, 16, 20, 21, 22, 24, 25, 27, 29, 30)
                    .filter { it <= m.lengthOfMonth() }
                    .map { m.safeDay(it) }
                    .toSet(),
            ),
            CourseInfo(
                name = "Graphic Design",
                color = CalendarLavender,
                datesAttended = listOf(5, 11, 18, 19)
                    .filter { it <= m.lengthOfMonth() }
                    .map { m.safeDay(it) }
                    .toSet(),
            ),
        )
    }
}
