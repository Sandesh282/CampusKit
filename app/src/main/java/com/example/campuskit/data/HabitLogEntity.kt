package com.example.campuskit.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * HabitLogEntity - The "Grid" Data
 * Represents daily check-ins for habits, forming the progress visualization.
 */
@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateTimestamp: Long, // Normalized to Midnight
    val status: Boolean // True = Checked in, False = Missed
)
