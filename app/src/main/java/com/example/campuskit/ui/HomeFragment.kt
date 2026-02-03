package com.example.campuskit.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.campuskit.AddEditTaskActivity
import com.example.campuskit.R
import com.example.campuskit.data.CampusKitRepository
import com.example.campuskit.databinding.FragmentHomeBinding
import com.example.campuskit.ui.adapter.DateSelectorAdapter
import com. example.campuskit.ui.adapter.TaskAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: CampusKitRepository
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
        
        repository = CampusKitRepository.getInstance(requireContext())
        
        // Set month display
        binding.monthText.text = monthFormat.format(Date())

        // Setup date selector with LinearSnapHelper ("Boring Code" physics)
        dateSelectorAdapter = DateSelectorAdapter { selectedDate ->
            loadTasksForDate(selectedDate)
        }
        
        binding.dateSelectorRecyclerView.layoutManager = 
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dateSelectorRecyclerView.adapter = dateSelectorAdapter
        
        // THE SNAP: LinearSnapHelper for tactile day picker
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.dateSelectorRecyclerView)
        
        // Add OnScrollListener for center item scale/fade effects
        binding.dateSelectorRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                applyScrollEffects(recyclerView)
            }
        })
        
        // Scroll to today (position 7 in the adapter)
        binding.dateSelectorRecyclerView.scrollToPosition(7)

        // Setup task list
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                // TODO: Navigate to task detail
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = taskAdapter
        
        binding.fabAddTask.setOnClickListener {
            val bottomSheet = AddTaskBottomSheet()
            bottomSheet.show(parentFragmentManager, AddTaskBottomSheet.TAG)
        }
        
        // Load initial tasks
        loadTasksForDate(Date())
    }
    
    private fun applyScrollEffects(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val centerX = recyclerView.width / 2
        
        for (i in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i) ?: continue
            val childCenterX = (child.left + child.right) / 2
            val distanceFromCenter = Math.abs(centerX - childCenterX).toFloat()
            val maxDistance = recyclerView.width / 2f
            
            // Scale and alpha based on distance from center
            val scale = 1.2f - (distanceFromCenter / maxDistance) * 0.2f
            val alpha = 1f - (distanceFromCenter / maxDistance) * 0.5f
            
            child.scaleX = scale.coerceIn(1f, 1.2f)
            child.scaleY = scale.coerceIn(1f, 1.2f)
            child.alpha = alpha.coerceIn(0.5f, 1f)
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadTasksForDate(Date())
    }
    
    private fun loadTasksForDate(date: Date) {
        lifecycleScope.launch {
            val tasks = repository.getTasksForToday()
            
            // Animate task card appearance with fade + slight vertical translate
            val slideUpFade = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade)
            binding.recyclerView.layoutAnimation = 
                android.view.animation.LayoutAnimationController(slideUpFade, 0.1f)
            
            taskAdapter.submitList(tasks)
            binding.recyclerView.scheduleLayoutAnimation()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
