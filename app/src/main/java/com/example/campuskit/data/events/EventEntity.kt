package com.example.campuskit.data.events

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a campus event.
 *
 * Mirrors the fields of the domain [Event] model, with an additional
 * [isReminded] flag to persist the user's reminder preference.
 */
@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val dateTime: String,
    val venue: String,
    val posterUrl: String,
    val organizer: String,
    val description: String = "",
    val isReminded: Boolean = false,
)
