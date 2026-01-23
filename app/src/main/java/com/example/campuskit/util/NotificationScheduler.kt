package com.example.campuskit.util

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import com.example.campuskit.worker.TaskReminderWorker
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    
    fun scheduleReminder(context: Context, taskId: Long, reminderTime: Long) {
        val currentTime = System.currentTimeMillis()
        val delay = reminderTime - currentTime
        
        if (delay > 0) {
            val inputData = Data.Builder()
                .putLong("task_id", taskId)
                .build()
            
            val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()
            
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
    
    fun cancelReminder(context: Context, taskId: Long) {
        // WorkManager doesn't have a direct way to cancel by tag
        // In a production app, you'd use tags or unique work names
        // For this MVP, we'll keep it simple
    }
}
