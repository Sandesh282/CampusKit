package com.example.campuskit.data.attendance

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a subject's attendance record.
 *
 * Each row tracks attended vs total classes for a single subject within
 * a specific [program] and [semester]. The unique index on
 * `(subjectName, program, semester)` prevents duplicate entries and
 * allows historical data to coexist across different academic terms.
 */
@Entity(
    tableName = "attendance",
    indices = [Index(value = ["subjectName", "program", "semester"], unique = true)]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val subjectId: Long = 0,
    val subjectName: String,
    val attendedClasses: Int = 0,
    val totalClasses: Int = 0,
    val minimumPercentage: Float = 75f,
    val program: String = "UNKNOWN",
    val semester: Int = 0,
)
