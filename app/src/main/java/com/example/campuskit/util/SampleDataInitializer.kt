package com.example.campuskit.util

import android.content.Context
import com.example.campuskit.R
import com.example.campuskit.data.CampusKitRepository
import com.example.campuskit.data.HabitEntity
import com.example.campuskit.data.HabitLogEntity
import com.example.campuskit.data.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Utility to populate database with sample data for testing
 */
object SampleDataInitializer {
    
    fun initialize(context: Context) {
        val repository = CampusKitRepository.getInstance(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            // Check if data already exists
            val existingTasks = repository.getAllTasks()
            if (existingTasks.isNotEmpty()) return@launch
            
            // Create sample tasks
            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            
            repository.insertTask(TaskEntity(
                title = "Morning Study Session",
                category = "Study",
                dateTimestamp = now + (2 * 60 * 60 * 1000), // 2 hours from now
                isCompleted = false,
                priority = 2 // High
            ))
            
            repository.insertTask(TaskEntity(
                title = "Team Meeting",
                category = "Work",
                dateTimestamp = now + (4 * 60 * 60 * 1000),
                isCompleted = false,
                priority = 1 // Medium
            ))
            
            repository.insertTask(TaskEntity(
                title = "Gym Workout",
                category = "Health",
                dateTimestamp = now + (6 * 60 * 60 * 1000),
                isCompleted = false,
                priority = 0 // Low
            ))
            
            // Create sample habits
            val habit1Id = repository.insertHabit(HabitEntity(
                title = "🏃 Exercise regularly",
                frequency = "Daily",
                iconResId = R.drawable.ic_launcher_foreground
            ))
            
            val habit2Id = repository.insertHabit(HabitEntity(
                title = "📚 Read books",
                frequency = "Daily",
                iconResId = R.drawable.ic_launcher_foreground
            ))
            
            val habit3Id = repository.insertHabit(HabitEntity(
                title = "💧 Drink water",
                frequency = "Daily",
                iconResId = R.drawable.ic_launcher_foreground
            ))
            
            // Add sample habit logs (last 7 days)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            for (daysAgo in 0..6) {
                val dayTimestamp = calendar.timeInMillis - (daysAgo * 24 * 60 * 60 * 1000)
                
                // Exercise: 5 out of 7 days
                if (daysAgo <= 4) {
                    repository.checkInHabit(habit1Id, dayTimestamp)
                }
                
                // Reading: 3 out of 7 days
                if (daysAgo <= 2) {
                    repository.checkInHabit(habit2Id, dayTimestamp)
                }
                
                // Water: 6 out of 7 days
                if (daysAgo != 2) {
                    repository.checkInHabit(habit3Id, dayTimestamp)
                }
            }
        }
    }
}
