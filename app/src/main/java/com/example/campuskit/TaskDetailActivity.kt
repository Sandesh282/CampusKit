package com.example.campuskit

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campuskit.data.TaskRepository
import com.example.campuskit.databinding.ActivityTaskDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TaskDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var taskRepository: TaskRepository
    private var taskId: Long = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        taskRepository = TaskRepository.getInstance(this)
        taskId = intent.getLongExtra("task_id", -1)
        
        if (taskId == -1L) {
            Toast.makeText(this, "Invalid task", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        loadTask()
        
        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            intent.putExtra("task_id", taskId)
            startActivity(intent)
        }
        
        binding.btnDelete.setOnClickListener {
            showDeleteDialog()
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadTask()
    }
    
    private fun loadTask() {
        val task = taskRepository.getTaskById(taskId)
        if (task == null) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        binding.taskTitle.text = task.title
        binding.taskDescription.text = task.description
        
        val priorityText = when (task.priority) {
            0 -> "LOW"
            1 -> "MEDIUM"
            2 -> "HIGH"
            else -> "UNKNOWN"
        }
        binding.taskPriority.text = priorityText
        
        if (task.hasReminder && task.reminderTime != null) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            binding.reminderInfo.text = "Reminder: ${dateFormat.format(task.reminderTime)}"
            binding.reminderInfo.visibility = android.view.View.VISIBLE
        } else {
            binding.reminderInfo.visibility = android.view.View.GONE
        }
    }
    
    private fun showDeleteDialog() {
        val task = taskRepository.getTaskById(taskId)
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task?.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                taskRepository.deleteTask(taskId)
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
