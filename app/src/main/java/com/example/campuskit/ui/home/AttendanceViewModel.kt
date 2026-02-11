package com.example.campuskit.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.data.AppDatabase
import com.example.campuskit.data.attendance.AttendanceEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.max

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).attendanceDao()

    val subjects = dao.getAllSubjects().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList(),
    )

    fun addSubject(name: String) {
        viewModelScope.launch {
            dao.insert(AttendanceEntity(subjectName = name.trim()))
        }
    }

    fun deleteSubject(entity: AttendanceEntity) {
        viewModelScope.launch {
            dao.delete(entity)
        }
    }

    fun markAttended(subjectId: Long) {
        viewModelScope.launch {
            dao.markAttended(subjectId)
        }
    }

    fun markBunked(subjectId: Long) {
        viewModelScope.launch {
            dao.markBunked(subjectId)
        }
    }

    companion object {
        fun calculatePercentage(attended: Int, total: Int): Float {
            if (total == 0) return 100f
            return (attended.toFloat() / total.toFloat()) * 100f
        }

        fun calculateSafeBunks(attended: Int, total: Int, minPercentage: Float): Int {
            if (total == 0) return 0
            val minFraction = minPercentage / 100f
            val safeBunks = floor((attended.toFloat() - minFraction * total.toFloat()) / minFraction).toInt()
            return max(0, safeBunks)
        }

        fun calculateClassesNeeded(attended: Int, total: Int, minPercentage: Float): Int {
            if (total == 0) return 0
            val minFraction = minPercentage / 100f
            val currentPercentage = attended.toFloat() / total.toFloat()
            if (currentPercentage >= minFraction) return 0
            var needed = 0
            var att = attended
            var tot = total
            while (att.toFloat() / tot.toFloat() < minFraction) {
                att++
                tot++
                needed++
            }
            return needed
        }

        fun getStatus(attended: Int, total: Int, minPercentage: Float): AttendanceStatus {
            val percentage = calculatePercentage(attended, total)
            val safeBunks = calculateSafeBunks(attended, total, minPercentage)
            val needed = calculateClassesNeeded(attended, total, minPercentage)

            return when {
                total == 0 -> AttendanceStatus.NoData
                percentage >= minPercentage + 10f -> AttendanceStatus.Safe(safeBunks)
                percentage >= minPercentage -> AttendanceStatus.Warning(safeBunks)
                else -> AttendanceStatus.Critical(needed)
            }
        }
    }
}

sealed class AttendanceStatus {
    data object NoData : AttendanceStatus()
    data class Safe(val canBunk: Int) : AttendanceStatus()
    data class Warning(val canBunk: Int) : AttendanceStatus()
    data class Critical(val needToAttend: Int) : AttendanceStatus()
}
