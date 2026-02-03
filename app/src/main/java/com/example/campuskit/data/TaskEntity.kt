package com.example.campuskit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TaskEntity - The "Timeline" Items
 * Represents tasks that appear on the timeline view.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Study", "Social", "Health"
    val dateTimestamp: Long, // For sorting in the Timeline
    val isCompleted: Boolean = false,
    val priority: Int // 0 = Low, 1 = Medium, 2 = High
)
