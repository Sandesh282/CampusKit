package com.example.campuskit.data

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val priority: Int, // 0 = Low, 1 = Medium, 2 = High
    val hasReminder: Boolean,
    val reminderTime: Long? // timestamp in milliseconds
)
