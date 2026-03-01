package com.example.campuskit.data.academic.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "offerings",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class, 
            parentColumns = ["code"], 
            childColumns = ["subjectCode"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("subjectCode"), 
        Index("program", "semester")
    ]
)
data class OfferingEntity(
    @PrimaryKey val id: String,
    val subjectCode: String,
    val program: String,
    val semester: Int,
    val isElective: Boolean
)
