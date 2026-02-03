package com.example.campuskit.ui.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.campuskit.R
import com.example.campuskit.data.HabitEntity
import com.example.campuskit.data.HabitLogEntity
import com.example.campuskit.databinding.ItemHabitBinding

/**
 * HabitAdapter with "Last 7 Days" circle visualization
 */
class HabitAdapter(
    private val onCheckIn: (HabitEntity) -> Unit,
    private val habitLogsMap: Map<Long, List<HabitLogEntity>> = emptyMap()
) : ListAdapter<HabitEntity, HabitAdapter.HabitViewHolder>(HabitDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val logs = habitLogsMap[getItem(position).id] ?: emptyList()
        holder.bind(getItem(position), logs)
    }
    
    inner class HabitViewHolder(
        private val binding: ItemHabitBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(habit: HabitEntity, logs: List<HabitLogEntity>) {
            binding.habitTitle.text = habit.title
            binding.habitFrequency.text = habit.frequency
            
            // Create 7 circles for last 7 days
            binding.last7DaysContainer.removeAllViews()
            val circleSize = 32 // dp
            val circleMargin = 8 // dp
            val density = binding.root.context.resources.displayMetrics.density
            
            // Create a map of timestamps to completion status
            val logMap = logs.associateBy { it.dateTimestamp }
            
            for (i in 6 downTo 0) {
                val dayOffset = i
                val dayTimestamp = getNormalizedDayTimestamp(dayOffset)
                val isCompleted = logMap[dayTimestamp]?.status == true
                
                val circle = View(binding.root.context)
                val layoutParams = LinearLayout.LayoutParams(
                    (circleSize * density).toInt(),
                    (circleSize * density).toInt()
                )
                layoutParams.marginEnd = (circleMargin * density).toInt()
                circle.layoutParams = layoutParams
                
                // Create circular drawable
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.OVAL
                drawable.setColor(
                    if (isCompleted) {
                        ContextCompat.getColor(binding.root.context, R.color.accent_sage)
                    } else {
                        ContextCompat.getColor(binding.root.context, R.color.surface_variant)
                    }
                )
                circle.background = drawable
                
                binding.last7DaysContainer.addView(circle)
            }
            
            // Calculate streak
            val streak = calculateStreak(logs)
            binding.statsText.text = if (streak > 0) {
                "$streak day streak 🔥"
            } else {
                "No current streak"
            }
            
            // Check-in button
            binding.checkInButton.setOnClickListener {
                onCheckIn(habit)
            }
        }
        
        private fun getNormalizedDayTimestamp(daysAgo: Int): Long {
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
            return calendar.timeInMillis
        }
        
        private fun calculateStreak(logs: List<HabitLogEntity>): Int {
            if (logs.isEmpty()) return 0
            
            val sortedLogs = logs.sortedByDescending { it.dateTimestamp }
            var streak = 0
            
            for (i in sortedLogs.indices) {
                if (sortedLogs[i].status) {
                    streak++
                } else {
                    break
                }
            }
            
            return streak
        }
    }
    
    class HabitDiffCallback : DiffUtil.ItemCallback<HabitEntity>() {
        override fun areItemsTheSame(oldItem: HabitEntity, newItem: HabitEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: HabitEntity, newItem: HabitEntity): Boolean {
            return oldItem == newItem
        }
    }
}
