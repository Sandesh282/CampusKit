package com.example.campuskit.data.calendar

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a calendar event.
 *
 * Date and time fields are stored as ISO-format strings (e.g. "2026-03-28",
 * "09:00") for simplicity. Colour is stored as an ARGB [Int] via
 * [androidx.compose.ui.graphics.Color.toArgb].
 */
@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val dateString: String,
    val startTimeString: String,
    val endTimeString: String,
    /** ARGB integer representation of the event display color. */
    val colorArgb: Int,
    val assignee: String = "",
    val attendeeCount: Int = 0,
    val location: String = "",
    val description: String = "",
)
