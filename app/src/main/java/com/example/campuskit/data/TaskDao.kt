package com.example.campuskit.data

import androidx.room.*

/**
 * DAO for Task operations
 */
@Dao
interface TaskDao {
    
    @Query("SELECT * FROM tasks ORDER BY dateTimestamp ASC")
    suspend fun getAllTasks(): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE dateTimestamp >= :startOfDay AND dateTimestamp < :endOfDay ORDER BY dateTimestamp ASC")
    suspend fun getTasksForDay(startOfDay: Long, endOfDay: Long): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)
}
