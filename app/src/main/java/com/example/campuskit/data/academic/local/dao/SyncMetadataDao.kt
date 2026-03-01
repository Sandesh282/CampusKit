package com.example.campuskit.data.academic.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.campuskit.data.academic.local.entity.SyncMetadataEntity

@Dao
interface SyncMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(syncMetadata: SyncMetadataEntity)

    @Query("SELECT * FROM sync_metadata WHERE semesterProgramKey = :key")
    suspend fun getLastSync(key: String): SyncMetadataEntity?
}
