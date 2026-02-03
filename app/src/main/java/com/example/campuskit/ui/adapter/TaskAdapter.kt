package com.example.campuskit.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.campuskit.data.TaskEntity
import com.example.campuskit.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * TaskAdapter with "Two-Line Logic" for continuous timeline effect
 */
class TaskAdapter(
    private val onTaskClick: (TaskEntity) -> Unit,
    private val onTaskLongPress: ((TaskEntity) -> Unit)? = null
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val isFirst = position == 0
        val isLast = position == itemCount - 1
        holder.bind(getItem(position), isFirst, isLast)
    }
    
    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        
        fun bind(task: TaskEntity, isFirst: Boolean, isLast: Boolean) {
            // Two-Line Logic: Show/hide connectors for first and last items
            binding.topLine.visibility = if (isFirst) View.INVISIBLE else View.VISIBLE
            binding.bottomLine.visibility = if (isLast) View.INVISIBLE else View.VISIBLE
            
            binding.taskTitle.text = task.title
            binding.taskDescription.text = "${task.category} • Task details"
            
            // Show time if available
            if (task.dateTimestamp > 0) {
                binding.taskTime.visibility = View.VISIBLE
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = task.dateTimestamp
                binding.taskTime.text = timeFormat.format(calendar.time)
            } else {
                binding.taskTime.visibility = View.GONE
            }
            
            // Status indicator color based on completion
            val statusColorRes = if (task.isCompleted) {
                com.example.campuskit.R.color.accent_sage
            } else {
                com.example.campuskit.R.color.status_pending
            }
            binding.statusIndicator.setBackgroundResource(statusColorRes)
            
            // Click listeners
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
    
    class TaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}
