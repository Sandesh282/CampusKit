package com.example.campuskit.data

import androidx.room.*

/**
 * DAO for Habit operations
 */
@Dao
interface HabitDao {
    
    @Query("SELECT * FROM habits ORDER BY id ASC")
    suspend fun getAllHabits(): List<HabitEntity>
    
    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Long): HabitEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long
    
    @Update
    suspend fun updateHabit(habit: HabitEntity)
    
    @Delete
    suspend fun deleteHabit(habit: HabitEntity)
    
    // Habit Log operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity): Long
    
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND dateTimestamp >= :startDate AND dateTimestamp < :endDate ORDER BY dateTimestamp ASC")
    suspend fun getHabitLogsForRange(habitId: Long, startDate: Long, endDate: Long): List<HabitLogEntity>
    
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND dateTimestamp = :date")
    suspend fun getHabitLogForDate(habitId: Long, date: Long): HabitLogEntity?
    
    @Delete
    suspend fun deleteHabitLog(log: HabitLogEntity)
}
