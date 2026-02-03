package com.example.campuskit.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.campuskit.data.Task
import com.example.campuskit.databinding.ItemTaskBinding

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskLongPress: ((Task) -> Unit)? = null
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(task: Task) {
            binding.taskTitle.text = task.title
            binding.taskDescription.text = task.description
            
            // Set color marker based on task priority/category
            val markerColorRes = when (task.priority) {
                0 -> com.example.campuskit.R.color.accent_sage      // Low priority - sage green
                1 -> com.example.campuskit.R.color.accent_blue      // Medium - blue
                2 -> com.example.campuskit.R.color.accent_coral     // High - coral
                else -> com.example.campuskit.R.color.accent_sage
            }
            binding.taskColorMarker.setBackgroundResource(markerColorRes)
            
            // Show time if task has reminder (subtle, timeline-anchored)
            if (task.hasReminder && task.reminderTime != null) {
                binding.taskTime.visibility = View.VISIBLE
                // Format time from reminderTime (assuming it's a timestamp)
                val calendar = java.util.Calendar.getInstance()
                calendar.timeInMillis = task.reminderTime!!
                val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                binding.taskTime.text = timeFormat.format(calendar.time)
            } else {
                binding.taskTime.visibility = View.GONE
            }
            
            // Status indicator color (subtle tone-based communication)
            val statusColorRes = when {
                task.hasReminder && task.reminderTime != null -> 
                    com.example.campuskit.R.color.status_in_progress
                else -> 
                    com.example.campuskit.R.color.status_pending
            }
            binding.statusIndicator.setBackgroundResource(statusColorRes)
            
            // Hide metadata layout for now (reserved for habit progress)
            binding.metadataLayout.visibility = View.GONE
            
            // Set click listeners
            binding.root.setOnClickListener {
                onTaskClick(task)
            }
            
            onTaskLongPress?.let { longPress ->
                binding.root.setOnLongClickListener {
                    longPress(task)
                    true
                }
            }
        }
    }
    
    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
