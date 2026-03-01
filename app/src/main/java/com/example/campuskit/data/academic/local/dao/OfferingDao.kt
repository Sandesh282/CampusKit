package com.example.campuskit.data.academic.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.campuskit.data.academic.local.entity.OfferingEntity
import com.example.campuskit.data.academic.local.entity.SubjectEntity
import com.example.campuskit.data.academic.local.entity.SubjectOffering
import kotlinx.coroutines.flow.Flow

@Dao
interface OfferingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSubjects(subjects: List<SubjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOfferings(offerings: List<OfferingEntity>)

    @Transaction
    @Query("SELECT * FROM offerings WHERE program = :program AND semester = :semester")
    fun getOfferingsWithSubjectsFlow(program: String, semester: Int): Flow<List<SubjectOffering>>
    
    @Transaction
    @Query("SELECT * FROM offerings WHERE program = :program AND semester = :semester")
    suspend fun getOfferingsWithSubjects(program: String, semester: Int): List<SubjectOffering>
}
