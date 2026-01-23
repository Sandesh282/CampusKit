package com.example.campuskit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campuskit.data.Task
import com.example.campuskit.data.TaskRepository
import com.example.campuskit.databinding.ActivityAddEditTaskBinding
import com.example.campuskit.util.NotificationScheduler
import java.util.Calendar

class AddEditTaskActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddEditTaskBinding
    private lateinit var taskRepository: TaskRepository
    private var taskId: Long? = null
    private var reminderTimeInMillis: Long? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        taskRepository = TaskRepository.getInstance(this)
        
        // Check if editing existing task
        taskId = intent.getLongExtra("task_id", -1).takeIf { it != -1L }
        
        setupPrioritySpinner()
        setupReminderCheckbox()
        loadTaskIfEditing()
        
        binding.btnSave.setOnClickListener {
            if (validateAndSave()) {
                finish()
            }
        }
    }
    
    private fun setupPrioritySpinner() {
        val priorities = arrayOf("Low", "Medium", "High")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = adapter
    }
    
    private fun setupReminderCheckbox() {
        binding.checkBoxReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                openDatePicker()
            } else {
                reminderTimeInMillis = null
            }
        }
    }
    
    private fun openDatePicker() {
        val now = Calendar.getInstance()
        
        DatePickerDialog(
            this,
            { _, year, month, day ->
                openTimePicker(year, month, day)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun openTimePicker(year: Int, month: Int, day: Int) {
        val now = Calendar.getInstance()
        
        TimePickerDialog(
            this,
            { _, hour, minute ->
                val calendar = Calendar.getInstance().apply {
                    set(year, month, day, hour, minute, 0)
                }
                reminderTimeInMillis = calendar.timeInMillis
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        ).show()
    }
    
    private fun loadTaskIfEditing() {
        taskId?.let { id ->
            val task = taskRepository.getTaskById(id)
            task?.let {
                binding.editTextTitle.setText(it.title)
                binding.editTextDescription.setText(it.description)
                binding.spinnerPriority.setSelection(it.priority)
                
                // Set reminder state without triggering listener
                binding.checkBoxReminder.setOnCheckedChangeListener(null)
                binding.checkBoxReminder.isChecked = it.hasReminder
                reminderTimeInMillis = it.reminderTime
                binding.checkBoxReminder.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        openDatePicker()
                    } else {
                        reminderTimeInMillis = null
                    }
                }
            }
        }
    }
    
    private fun validateAndSave(): Boolean {
        val title = binding.editTextTitle.text.toString().trim()
        
        if (title.isEmpty()) {
            binding.titleInputLayout.error = "Title cannot be empty"
            return false
        }
        
        binding.titleInputLayout.error = null
        
        val description = binding.editTextDescription.text.toString().trim()
        val priority = binding.spinnerPriority.selectedItemPosition
        val hasReminder = binding.checkBoxReminder.isChecked
        
        if (hasReminder && reminderTimeInMillis == null) {
            Toast.makeText(this, "Please select a reminder date and time", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val savedTaskId = if (taskId != null) {
            // Update existing task
            val updatedTask = Task(
                id = taskId!!,
                title = title,
                description = description,
                priority = priority,
                hasReminder = hasReminder,
                reminderTime = if (hasReminder) reminderTimeInMillis else null
            )
            taskRepository.updateTask(updatedTask)
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
            taskId!!
        } else {
            // Create new task
            val newTask = Task(
                id = 0, // Will be assigned by repository
                title = title,
                description = description,
                priority = priority,
                hasReminder = hasReminder,
                reminderTime = if (hasReminder) reminderTimeInMillis else null
            )
            val newId = taskRepository.addTask(newTask)
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
            newId
        }
        
        // Schedule notification if reminder is set
        if (hasReminder && reminderTimeInMillis != null) {
            NotificationScheduler.scheduleReminder(this, savedTaskId, reminderTimeInMillis!!)
        }
        
        return true
    }
}
