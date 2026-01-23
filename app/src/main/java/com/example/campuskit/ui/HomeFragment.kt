package com.example.campuskit.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuskit.AddEditTaskActivity
import com.example.campuskit.R
import com.example.campuskit.data.TaskRepository
import com.example.campuskit.databinding.FragmentHomeBinding
import com.example.campuskit.ui.adapter.TaskAdapter

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskAdapter: TaskAdapter
    
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
        
        loadTodayTasks()
    }
    
    override fun onResume() {
        super.onResume()
        loadTodayTasks()
    }
    
    private fun loadTodayTasks() {
        val tasks = taskRepository.getTodayTasks()
        taskAdapter.submitList(tasks)
        
        binding.summaryText.text = if (tasks.isEmpty()) {
            "No tasks for today"
        } else {
            "${tasks.size} task(s) for today"
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
