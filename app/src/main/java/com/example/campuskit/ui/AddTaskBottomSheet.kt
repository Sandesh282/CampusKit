package com.example.campuskit.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.campuskit.R
import com.example.campuskit.data.CampusKitRepository
import com.example.campuskit.data.TaskEntity
import com.example.campuskit.databinding.BottomSheetAddTaskBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * BottomSheetDialogFragment for adding tasks
 * Per spec: "feels faster/modern than a full new Activity"
 */
class AddTaskBottomSheet : BottomSheetDialogFragment() {
    
    private var _binding: BottomSheetAddTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: CampusKitRepository
    private var selectedDateTimestamp: Long? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        repository = CampusKitRepository.getInstance(requireContext())
        
        setupCategorySpinner()
        setupPrioritySpinner()
        
        binding.selectDateButton.setOnClickListener {
            openDatePicker()
        }
        
        binding.saveButton.setOnClickListener {
            saveTask()
        }
        
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }
    
    private fun setupCategorySpinner() {
        val categories = arrayOf("Study", "Social", "Health", "Work", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter
    }
    
    private fun setupPrioritySpinner() {
        val priorities = arrayOf("Low", "Medium", "High")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.prioritySpinner.adapter = adapter
        binding.prioritySpinner.setSelection(1) // Default to Medium
    }
    
    private fun openDatePicker() {
        val now = Calendar.getInstance()
        
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                openTimePicker(year, month, day)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun openTimePicker(year: Int, month: Int, day: Int) {
        val now = Calendar.getInstance()
        
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                val calendar = Calendar.getInstance().apply {
                    set(year, month, day, hour, minute, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                selectedDateTimestamp = calendar.timeInMillis
                updateDateButtonText(calendar)
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        ).show()
    }
    
    private fun updateDateButtonText(calendar: Calendar) {
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
        binding.selectDateButton.text = dateFormat.format(calendar.time)
    }
    
    private fun saveTask() {
        val title = binding.titleInput.text.toString().trim()
        
        if (title.isEmpty()) {
            binding.titleInputLayout.error = "Title cannot be empty"
            return
        }
        
        binding.titleInputLayout.error = null
        
        val category = binding.categorySpinner.selectedItem.toString()
        val priority = binding.prioritySpinner.selectedItemPosition
        val timestamp = selectedDateTimestamp ?: System.currentTimeMillis()
        
        val newTask = TaskEntity(
            title = title,
            category = category,
            dateTimestamp = timestamp,
            isCompleted = false,
            priority = priority
        )
        
        lifecycleScope.launch {
            repository.insertTask(newTask)
            Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        const val TAG = "AddTaskBottomSheet"
    }
}
