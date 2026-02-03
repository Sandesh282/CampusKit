package com.example.campuskit.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuskit.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for the curved date selector in Home tab.
 * Implements pill-based date navigation with arc visual effect via translation.
 */
class DateSelectorAdapter(
    private val onDateSelected: (Date) -> Unit
) : RecyclerView.Adapter<DateSelectorAdapter.DatePillViewHolder>() {

    private val dates = mutableListOf<Date>()
    private var selectedPosition = 0
    
    private val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
    private val dayNumberFormat = SimpleDateFormat("d", Locale.getDefault())

    init {
        // Generate 14 days (7 before, 7 after today)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        
        for (i in 0 until 14) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        selectedPosition = 7 // Today is in the middle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatePillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date_pill, parent, false)
        return DatePillViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatePillViewHolder, position: Int) {
        val date = dates[position]
        val isSelected = position == selectedPosition
        
        holder.bind(date, isSelected)
        
        // Arc translation effect: selected item is slightly elevated
        // Non-selected items closer to the edge have more downward translation
        val distanceFromSelected = Math.abs(position - selectedPosition)
        val translationY = if (isSelected) {
            -8f // Slight upward lift for selected
        } else {
            distanceFromSelected * 2f // Subtle curved effect
        }
        
        holder.itemView.translationY = translationY * holder.itemView.context.resources.displayMetrics.density
        
        // Alpha fade for distant items
        val alpha = if (distanceFromSelected > 4) 0.3f else 1f
        holder.itemView.alpha = alpha
        
        holder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
            onDateSelected(date)
        }
    }

    override fun getItemCount() = dates.size

    class DatePillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pillContainer: FrameLayout = itemView.findViewById(R.id.pillContainer)
        private val dayOfWeek: TextView = itemView.findViewById(R.id.dayOfWeek)
        private val dayNumber: TextView = itemView.findViewById(R.id.dayNumber)
        
        private val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
        private val dayNumberFormat = SimpleDateFormat("d", Locale.getDefault())

        fun bind(date: Date, isSelected: Boolean) {
            dayOfWeek.text = dayOfWeekFormat.format(date).uppercase(Locale.getDefault()).take(2)
            dayNumber.text = dayNumberFormat.format(date)
            
            // Update background drawable
            pillContainer.setBackgroundResource(
                if (isSelected) R.drawable.date_pill_selected
                else R.drawable.date_pill_unselected
            )
            
            // Animate selection change
            pillContainer.animate()
                .scaleX(if (isSelected) 1.05f else 1f)
                .scaleY(if (isSelected) 1.05f else 1f)
                .setDuration(200)
                .start()
        }
    }
}
