package com.example.campuskit.data.calendar

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for calendar events, backed by Room.
 *
 * On first launch, seeds the database with [MockCalendarData] so the
 * user sees example events immediately.
 */
@Singleton
class CalendarRepository @Inject constructor(
    private val calendarEventDao: CalendarEventDao,
) {

    /** Returns all calendar events as domain models. */
    fun getAllEvents(): Flow<List<CalendarEvent>> = calendarEventDao.getAllEvents().map { entities ->
        entities.map { it.toDomain() }
    }

    /** Returns events for a specific [date] as domain models. */
    fun getEventsForDate(date: LocalDate): Flow<List<CalendarEvent>> =
        calendarEventDao.getEventsForDate(date.toString()).map { entities ->
            entities.map { it.toDomain() }
        }

    /** Persists a new calendar event. */
    suspend fun addEvent(event: CalendarEvent) {
        calendarEventDao.insert(event.toEntity())
    }

    /** Deletes a calendar event. */
    suspend fun deleteEvent(event: CalendarEvent) {
        calendarEventDao.delete(event.toEntity())
    }

    /** Seeds mock events into the database if it's empty (first launch). */
    suspend fun seedIfEmpty() {
        if (calendarEventDao.getCount() == 0) {
            val seedEntities = MockCalendarData.getEvents().map { it.toEntity() }
            calendarEventDao.insertAll(seedEntities)
        }
    }
}

// ── Mapping helpers ──

/** Converts a [CalendarEventEntity] from Room into a domain [CalendarEvent]. */
private fun CalendarEventEntity.toDomain() = CalendarEvent(
    id = id,
    title = title,
    date = LocalDate.parse(dateString),
    startTime = LocalTime.parse(startTimeString),
    endTime = LocalTime.parse(endTimeString),
    color = Color(colorArgb),
    assignee = assignee,
    attendeeCount = attendeeCount,
    location = location,
    description = description,
)

/** Converts a domain [CalendarEvent] into a [CalendarEventEntity] for Room. */
private fun CalendarEvent.toEntity() = CalendarEventEntity(
    id = id,
    title = title,
    dateString = date.toString(),
    startTimeString = startTime.toString(),
    endTimeString = endTime.toString(),
    colorArgb = color.hashCode(),
    assignee = assignee,
    attendeeCount = attendeeCount,
    location = location,
    description = description,
)
