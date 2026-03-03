package com.example.campuskit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.data.academic.SubjectCatalog
import com.example.campuskit.data.academic.local.dao.OfferingDao
import com.example.campuskit.data.academic.prefs.AcademicPreferences
import com.example.campuskit.data.academic.prefs.AcademicPreferencesManager
import com.example.campuskit.data.attendance.AttendanceEntity
import com.example.campuskit.data.attendance.AttendanceRepository
import com.example.campuskit.domain.academic.model.Program
import com.example.campuskit.domain.academic.model.Resource
import com.example.campuskit.domain.academic.model.Subject
import com.example.campuskit.domain.academic.repository.AcademicRepository
import com.example.campuskit.domain.attendance.AttendanceStatus
import com.example.campuskit.domain.attendance.CalculateSafeBunksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HomeTag { ATTENDANCE, QUESTION_PAPERS, NOTES }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val academicRepository: AcademicRepository,
    private val academicPrefsManager: AcademicPreferencesManager,
    private val calculateSafeBunksUseCase: CalculateSafeBunksUseCase,
    private val offeringDao: OfferingDao,
) : ViewModel() {

    private val _selectedTag = MutableStateFlow(HomeTag.ATTENDANCE)
    val selectedTag: StateFlow<HomeTag> = _selectedTag.asStateFlow()

    val academicPreferences: StateFlow<AcademicPreferences> =
        academicPrefsManager.preferencesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AcademicPreferences(Program.UNKNOWN, 1),
        )

    val attendanceSubjects: StateFlow<List<AttendanceEntity>> =
        attendanceRepository.getAllSubjects().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList(),
        )

    // Academic subjects from the current semester selection
    val academicSubjects: StateFlow<List<Subject>> =
        academicPrefsManager.preferencesFlow.flatMapLatest { prefs ->
            if (prefs.program == Program.UNKNOWN) {
                flowOf(emptyList())
            } else {
                academicRepository.getSubjects(prefs.program, prefs.semester)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList(),
        )

    fun selectTag(tag: HomeTag) {
        _selectedTag.value = tag
    }

    fun updateProgram(program: Program) {
        viewModelScope.launch {
            academicPrefsManager.updateProgram(program)
        }
    }

    fun updateSemester(semester: Int) {
        viewModelScope.launch {
            academicPrefsManager.updateSemester(semester)
        }
    }

    fun updateStudentName(name: String) {
        viewModelScope.launch {
            academicPrefsManager.updateStudentName(name)
        }
    }

    /**
     * Seeds subjects from the static catalog into Room
     * so they appear instantly after semester selection.
     */
    fun seedSubjectsForSelection(program: Program, semester: Int) {
        viewModelScope.launch {
            val subjects = SubjectCatalog.getSubjects(program, semester)
            val offerings = SubjectCatalog.getOfferings(program, semester)
            if (subjects.isNotEmpty()) {
                offeringDao.upsertSubjects(subjects)
                offeringDao.upsertOfferings(offerings)
            }
        }
    }

    // Attendance actions (delegate to existing logic)
    fun addSubject(name: String) {
        viewModelScope.launch {
            attendanceRepository.insertSubject(AttendanceEntity(subjectName = name.trim()))
        }
    }

    fun deleteSubject(entity: AttendanceEntity) {
        viewModelScope.launch {
            attendanceRepository.deleteSubject(entity)
        }
    }

    fun markAttended(subjectId: Long) {
        viewModelScope.launch {
            attendanceRepository.markAttended(subjectId)
        }
    }

    fun markBunked(subjectId: Long) {
        viewModelScope.launch {
            attendanceRepository.markBunked(subjectId)
        }
    }

    fun getAttendanceStatus(attended: Int, total: Int, minPercentage: Float): AttendanceStatus {
        return calculateSafeBunksUseCase(attended, total, minPercentage)
    }

    fun syncResources() {
        viewModelScope.launch {
            val prefs = academicPreferences.value
            if (prefs.program != Program.UNKNOWN) {
                try {
                    academicRepository.syncResources(prefs.program, prefs.semester)
                } catch (_: Exception) { /* silent fail */ }
            }
        }
    }
}
