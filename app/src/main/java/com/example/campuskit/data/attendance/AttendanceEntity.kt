package com.example.campuskit.data.attendance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val subjectId: Long = 0,
    val subjectName: String,
    val attendedClasses: Int = 0,
    val totalClasses: Int = 0,
    val minimumPercentage: Float = 75f,
)
