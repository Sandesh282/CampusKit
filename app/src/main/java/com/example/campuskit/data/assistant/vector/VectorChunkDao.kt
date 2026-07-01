package com.example.campuskit.data.assistant.vector

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface VectorChunkDao {

    @Query("SELECT * FROM vector_chunks")
    suspend fun getAll(): List<VectorChunkEntity>

    @Query("SELECT * FROM vector_chunks WHERE source = :source")
    suspend fun getBySource(source: String): List<VectorChunkEntity>

    @Upsert
    suspend fun upsert(chunks: List<VectorChunkEntity>)

    @Query("DELETE FROM vector_chunks WHERE source = :source")
    suspend fun deleteBySource(source: String)

    @Query("SELECT COUNT(*) FROM vector_chunks")
    suspend fun count(): Int
}
