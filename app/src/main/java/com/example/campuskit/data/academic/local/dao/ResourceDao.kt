package com.example.campuskit.data.academic.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.campuskit.data.academic.local.entity.ResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(resources: List<ResourceEntity>)

    @Query("SELECT * FROM resources WHERE subjectCode = :subjectCode")
    fun getResourcesForSubjectFlow(subjectCode: String): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE subjectCode = :subjectCode")
    suspend fun getResourcesForSubject(subjectCode: String): List<ResourceEntity>

    @Query("DELETE FROM resources WHERE subjectCode IN (SELECT subjectCode FROM offerings WHERE program = :program AND semester = :semester)")
    suspend fun deleteResourcesForSemester(program: String, semester: Int)
}
