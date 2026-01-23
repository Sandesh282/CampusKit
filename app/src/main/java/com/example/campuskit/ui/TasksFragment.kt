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
import com.example.campuskit.ui.adapter.TaskAdapter

class TasksFragment : Fragment() {
    
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskAdapter: TaskAdapter
    
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
        
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                // Open TaskDetailActivity on click
                val intent = Intent(requireContext(), TaskDetailActivity::class.java)
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            },
            onTaskLongPress = { task ->
                // Show delete dialog on long press
                showDeleteDialog(task)
            }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = taskAdapter
        
        loadTasks()
    }
    
    override fun onResume() {
        super.onResume()
        loadTasks()
    }
    
    private fun loadTasks() {
        val tasks = taskRepository.getAllTasks()
        taskAdapter.submitList(tasks)
    }
    
    private fun showDeleteDialog(task: com.example.campuskit.data.Task) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                taskRepository.deleteTask(task.id)
                loadTasks()
                Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
