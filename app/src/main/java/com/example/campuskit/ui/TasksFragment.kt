package com.example.campuskit.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuskit.R
import com.example.campuskit.TaskDetailActivity
import com.example.campuskit.data.TaskRepository
import com.example.campuskit.databinding.FragmentTasksBinding

class TasksFragment : Fragment() {
    
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskRepository: TaskRepository
    
    // Sample habit data
    private val sampleHabits = listOf(
        "🏃 Exercise regularly",
        "📚 Read books",
        "💧 Drink more water",
        "🧘 Meditate daily"
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        taskRepository = TaskRepository.getInstance(requireContext())
        
        // Set stats
        binding.totalHabitsCount.text = sampleHabits.size.toString()
        binding.completedTodayCount.text = "0"
        
        // Setup RecyclerView with habit cards (simplified for now)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        // Create New Habit button
        binding.createHabitButton.setOnClickListener {
            Toast.makeText(requireContext(), "Create New Habit (coming soon)", Toast.LENGTH_SHORT).show()
        }
        
        // For now, just show message in recycler view
        // In full implementation, this would use a HabitAdapter with item_habit.xml
        Toast.makeText(requireContext(), "Habits UI loaded - grid visualization requires data", Toast.LENGTH_SHORT).show()
    }
    
    override fun onResume() {
        super.onResume()
        // Reload habits data when fragment resumes
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
