package com.example.campuskit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.data.attendance.AttendanceEntity
import com.example.campuskit.data.attendance.AttendanceRepository
import com.example.campuskit.domain.attendance.AttendanceStatus
import com.example.campuskit.domain.attendance.CalculateSafeBunksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository,
    private val calculateSafeBunksUseCase: CalculateSafeBunksUseCase
) : ViewModel() {

    val subjects = repository.getAllSubjects().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList(),
    )

    fun addSubject(name: String) {
        viewModelScope.launch {
            repository.insertSubject(AttendanceEntity(subjectName = name.trim()))
        }
    }

    fun deleteSubject(entity: AttendanceEntity) {
        viewModelScope.launch {
            repository.deleteSubject(entity)
        }
    }

    fun markAttended(subjectId: Long) {
        viewModelScope.launch {
            repository.markAttended(subjectId)
        }
    }

    fun markBunked(subjectId: Long) {
        viewModelScope.launch {
            repository.markBunked(subjectId)
        }
    }

    fun getAttendanceStatus(attended: Int, total: Int, minPercentage: Float): AttendanceStatus {
        return calculateSafeBunksUseCase(attended, total, minPercentage)
    }
}
