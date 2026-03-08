package com.example.campuskit.data.attendance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendance ORDER BY subjectName ASC")
    fun getAllSubjects(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE program = :program AND semester = :semester ORDER BY subjectName ASC")
    fun getSubjectsForSemester(program: String, semester: Int): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE subjectId = :id")
    suspend fun getBySubjectId(id: Long): AttendanceEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: AttendanceEntity): Long

    @Update
    suspend fun update(entity: AttendanceEntity)

    @Delete
    suspend fun delete(entity: AttendanceEntity)

    @Query("UPDATE attendance SET attendedClasses = attendedClasses + 1, totalClasses = totalClasses + 1 WHERE subjectId = :subjectId")
    suspend fun markAttended(subjectId: Long)

    @Query("UPDATE attendance SET totalClasses = totalClasses + 1 WHERE subjectId = :subjectId")
    suspend fun markBunked(subjectId: Long)
}
