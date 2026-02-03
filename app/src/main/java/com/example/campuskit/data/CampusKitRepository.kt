package com.example.campuskit.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Repository - Abstraction layer over Room database
 * Provides clean API for UI layer to interact with data
 */
class CampusKitRepository private constructor(context: Context) {
    
    private val database = AppDatabase.getInstance(context)
    private val taskDao = database.taskDao()
    private val habitDao = database.habitDao()
    
    companion object {
        @Volatile
        private var INSTANCE: CampusKitRepository? = null
        
        fun getInstance(context: Context): CampusKitRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CampusKitRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // ========== Task Operations ==========
    
    suspend fun getAllTasks(): List<TaskEntity> = withContext(Dispatchers.IO) {
        taskDao.getAllTasks()
    }
    
    suspend fun getTasksForToday(): List<TaskEntity> = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000)
        
        taskDao.getTasksForDay(startOfDay, endOfDay)
    }
    
    suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }
    
    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }
    
    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }
    
    // ========== Habit Operations ==========
    
    suspend fun getAllHabits(): List<HabitEntity> = withContext(Dispatchers.IO) {
        habitDao.getAllHabits()
    }
    
    suspend fun insertHabit(habit: HabitEntity): Long = withContext(Dispatchers.IO) {
        habitDao.insertHabit(habit)
    }
    
    suspend fun updateHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }
    
    suspend fun deleteHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.deleteHabit(habit)
    }
    
    // ========== Habit Log Operations ==========
    
    /**
     * Get habit logs for the last N days (normalized to midnight)
     */
    suspend fun getHabitLogsForLastDays(habitId: Long, days: Int = 7): List<HabitLogEntity> = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val endDate = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startDate = calendar.timeInMillis
        
        habitDao.getHabitLogsForRange(habitId, startDate, endDate)
    }
    
    suspend fun checkInHabit(habitId: Long, date: Long = System.currentTimeMillis()): Long = withContext(Dispatchers.IO) {
        // Normalize date to midnight
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val normalizedDate = calendar.timeInMillis
        
        val log = HabitLogEntity(
            habitId = habitId,
            dateTimestamp = normalizedDate,
            status = true
        )
        habitDao.insertHabitLog(log)
    }
    
    suspend fun getHabitLogForDate(habitId: Long, date: Long): HabitLogEntity? = withContext(Dispatchers.IO) {
        habitDao.getHabitLogForDate(habitId, date)
    }
}
