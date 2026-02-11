package com.example.campuskit.data.attendance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {

    @Query("SELECT * FROM timetable WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getByDay(day: Int): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetable ORDER BY dayOfWeek, startTime")
    fun getAll(): Flow<List<TimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TimetableEntity): Long

    @Delete
    suspend fun delete(entity: TimetableEntity)
}
