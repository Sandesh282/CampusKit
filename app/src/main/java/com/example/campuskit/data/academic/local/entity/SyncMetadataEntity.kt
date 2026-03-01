package com.example.campuskit.data.academic.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val semesterProgramKey: String, // e.g., "5-CSAI"
    val lastSyncedAt: Long
)
