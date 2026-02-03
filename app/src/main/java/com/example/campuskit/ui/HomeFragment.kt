package com.example.campuskit.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuskit.AddEditTaskActivity
import com.example.campuskit.R
import com.example.campuskit.data.TaskRepository
import com.example.campuskit.databinding.FragmentHomeBinding
import com.example.campuskit.ui.adapter.DateSelectorAdapter
import com.example.campuskit.ui.adapter.TaskAdapter
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var dateSelectorAdapter: DateSelectorAdapter
    
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        taskRepository = TaskRepository.getInstance(requireContext())
        
        // Set month display
        binding.monthText.text = monthFormat.format(Date())

        // Setup date selector
        dateSelectorAdapter = DateSelectorAdapter { selectedDate ->
            // Date changed - trigger cross-fade animation
            loadTasksForDate(selectedDate)
        }
        
        binding.dateSelectorRecyclerView.layoutManager = 
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dateSelectorRecyclerView.adapter = dateSelectorAdapter
        
        // Scroll to today (position 7 in the adapter)
        binding.dateSelectorRecyclerView.scrollToPosition(7)

        // Setup task list
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                val intent = Intent(
                    requireContext(),
                    com.example.campuskit.TaskDetailActivity::class.java
                )
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = taskAdapter
        
        binding.fabAddTask.setOnClickListener {
            val intent = Intent(requireContext(), AddEditTaskActivity::class.java)
            startActivity(intent)
        }
        
        // Load initial tasks
        loadTasksForDate(Date())
    }
    
    override fun onResume() {
        super.onResume()
        loadTasksForDate(Date())
    }
    
    private fun loadTasksForDate(date: Date) {
        val tasks = taskRepository.getTodayTasks() // TODO: filter by selected date
        
        // Animate task card appearance with fade + slight vertical translate
        val slideUpFade = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade)
        binding.recyclerView.layoutAnimation = 
            android.view.animation.LayoutAnimationController(slideUpFade, 0.1f)
        
        taskAdapter.submitList(tasks)
        binding.recyclerView.scheduleLayoutAnimation()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
