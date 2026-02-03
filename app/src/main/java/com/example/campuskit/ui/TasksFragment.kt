package com.example.campuskit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuskit.data.CampusKitRepository
import com.example.campuskit.databinding.FragmentTasksBinding
import com.example.campuskit.ui.adapter.HabitAdapter
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {
    
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: CampusKitRepository
    private lateinit var habitAdapter: HabitAdapter
    
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
        
        repository = CampusKitRepository.getInstance(requireContext())
        
        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        // Create New Habit button
        binding.createHabitButton.setOnClickListener {
            Toast.makeText(requireContext(), "Create New Habit (coming soon)", Toast.LENGTH_SHORT).show()
        }
        
        loadHabits()
    }
    
    private fun loadHabits() {
        lifecycleScope.launch {
            val habits = repository.getAllHabits()
            
            // Fetch logs for all habits (last 7 days)
            val logsMap = mutableMapOf<Long, List<com.example.campuskit.data.HabitLogEntity>>()
            for (habit in habits) {
                logsMap[habit.id] = repository.getHabitLogsForLastDays(habit.id, 7)
            }
            
            // Update stats
            binding.totalHabitsCount.text = habits.size.toString()
            
            // Create adapter with check-in callback
            habitAdapter = HabitAdapter(
                onCheckIn = { habit ->
                    lifecycleScope.launch {
                        repository.checkInHabit(habit.id)
                        loadHabits() // Reload to update UI
                        Toast.makeText(requireContext(), "Checked in!", Toast.LENGTH_SHORT).show()
                    }
                },
                habitLogsMap = logsMap
            )
            
            binding.recyclerView.adapter = habitAdapter
            habitAdapter.submitList(habits)
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadHabits()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
