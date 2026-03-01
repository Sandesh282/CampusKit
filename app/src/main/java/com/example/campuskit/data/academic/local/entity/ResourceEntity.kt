package com.example.campuskit.data.academic.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resources")
data class ResourceEntity(
    @PrimaryKey val id: String,
    val subjectCode: String,
    val title: String,
    val type: String, // "PYP" or "NOTES"
    val url: String,
    val year: Int?,
    val examType: String?,
    val author: String?
)
