package com.example.campuskit.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskRepository private constructor(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private var tasks: MutableList<Task> = mutableListOf()
    private var nextId: Long = 1
    
    init {
        loadTasks()
    }
    
    companion object {
        private const val PREFS_NAME = "task_prefs"
        private const val KEY_TASKS = "tasks"
        private const val KEY_NEXT_ID = "next_id"
        
        @Volatile
        private var INSTANCE: TaskRepository? = null
        
        fun getInstance(context: Context): TaskRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TaskRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    fun getAllTasks(): List<Task> = tasks.toList()
    
    fun getTaskById(id: Long): Task? = tasks.find { it.id == id }
    
    fun getTodayTasks(): List<Task> {
        val today = System.currentTimeMillis()
        val startOfDay = today - (today % (24 * 60 * 60 * 1000))
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000)
        
        return tasks.filter { task ->
            task.reminderTime != null && 
            task.reminderTime >= startOfDay && 
            task.reminderTime < endOfDay
        }
    }
    
    fun addTask(task: Task): Long {
        val newTask = task.copy(id = nextId++)
        tasks.add(newTask)
        saveTasks()
        return newTask.id
    }
    
    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            saveTasks()
        }
    }
    
    fun deleteTask(id: Long) {
        tasks.removeAll { it.id == id }
        saveTasks()
    }
    
    private fun saveTasks() {
        val json = gson.toJson(tasks)
        sharedPreferences.edit()
            .putString(KEY_TASKS, json)
            .putLong(KEY_NEXT_ID, nextId)
            .apply()
    }
    
    private fun loadTasks() {
        val json = sharedPreferences.getString(KEY_TASKS, null)
        if (json != null) {
            val type = object : TypeToken<List<Task>>() {}.type
            tasks = gson.fromJson(json, type) ?: mutableListOf()
        }
        // Ensure nextId is greater than the maximum existing task ID
        val maxId = tasks.maxOfOrNull { it.id } ?: 0L
        val savedNextId = sharedPreferences.getLong(KEY_NEXT_ID, 1)
        nextId = maxOf(maxId + 1, savedNextId)
    }
}
