package com.example.campuskit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * HabitEntity - The "Tracker" Definitions
 * Represents habit templates that users track over time.
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val frequency: String, // "Daily", "Weekly"
    val iconResId: Int // Resource ID for icon/emoji representation
)
