package com.example.campuskit.data.attendance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Room DAO for [AttendanceEntity] CRUD operations and attendance tracking updates. */
@Dao
interface AttendanceDao {

    /** Returns all attendance records, ordered alphabetically. */
    @Query("SELECT * FROM attendance ORDER BY subjectName ASC")
    fun getAllSubjects(): Flow<List<AttendanceEntity>>

    /** Returns attendance records filtered by [program] and [semester]. */
    @Query("SELECT * FROM attendance WHERE program = :program AND semester = :semester ORDER BY subjectName ASC")
    fun getSubjectsForSemester(program: String, semester: Int): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE subjectId = :id")
    suspend fun getBySubjectId(id: Long): AttendanceEntity?

    /** Inserts a new record. Silently ignores if the unique constraint is already satisfied. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: AttendanceEntity): Long

    @Update
    suspend fun update(entity: AttendanceEntity)

    @Delete
    suspend fun delete(entity: AttendanceEntity)

    /** Increments both [AttendanceEntity.attendedClasses] and [AttendanceEntity.totalClasses] by 1. */
    @Query("UPDATE attendance SET attendedClasses = attendedClasses + 1, totalClasses = totalClasses + 1 WHERE subjectId = :subjectId")
    suspend fun markAttended(subjectId: Long)

    /** Increments only [AttendanceEntity.totalClasses] by 1 (records a bunk). */
    @Query("UPDATE attendance SET totalClasses = totalClasses + 1 WHERE subjectId = :subjectId")
    suspend fun markBunked(subjectId: Long)
}
