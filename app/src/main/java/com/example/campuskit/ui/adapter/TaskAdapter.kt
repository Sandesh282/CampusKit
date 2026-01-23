package com.example.campuskit.ui.adapter

import android.view.LayoutInflater
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
            
            // Set priority text
            val priorityText = when (task.priority) {
                0 -> "LOW"
                1 -> "MEDIUM"
                2 -> "HIGH"
                else -> "UNKNOWN"
            }
            binding.priorityText.text = priorityText
            
            // Show reminder icon if reminder is set
            binding.reminderIcon.visibility = if (task.hasReminder && task.reminderTime != null) {
                ViewGroup.VISIBLE
            } else {
                ViewGroup.GONE
            }
            
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
