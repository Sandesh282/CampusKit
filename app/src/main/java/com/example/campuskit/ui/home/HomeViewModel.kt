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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Content tags available on the Home screen. */
enum class HomeTag { ATTENDANCE, QUESTION_PAPERS, NOTES }

/**
 * Central ViewModel for the Home screen.
 *
 * Coordinates attendance tracking, academic resources (QP/Notes),
 * and semester selection. Attendance subjects are filtered reactively
 * based on the currently selected [Program] and semester.
 *
 * @param attendanceRepository  Room-backed attendance data source
 * @param academicRepository    Remote + local academic resources
 * @param academicPrefsManager  DataStore-backed semester/program preferences
 * @param calculateSafeBunksUseCase  Domain logic for attendance health
 * @param offeringDao           DAO for subject/offering catalog data
 */
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val academicPreferences: StateFlow<AcademicPreferences> =
        academicPrefsManager.preferencesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AcademicPreferences(Program.UNKNOWN, 1),
        )

    val attendanceSubjects: StateFlow<List<AttendanceEntity>> =
        academicPrefsManager.preferencesFlow.flatMapLatest { prefs ->
            combine(
                attendanceRepository.getSubjectsForSemester(prefs.program.name, prefs.semester),
                _searchQuery
            ) { subjects, query ->
                if (query.isBlank()) subjects
                else subjects.filter { it.subjectName.contains(query, ignoreCase = true) }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList(),
        )

    // Academic subjects from the current semester selection, filtered by search query
    val academicSubjects: StateFlow<List<Subject>> =
        combine(
            academicPrefsManager.preferencesFlow.flatMapLatest { prefs ->
                if (prefs.program == Program.UNKNOWN) {
                    flowOf(emptyList())
                } else {
                    academicRepository.getSubjects(prefs.program, prefs.semester)
                }
            },
            _searchQuery
        ) { subjects, query ->
            if (query.isBlank()) subjects
            else subjects.filter {
                it.name.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList(),
        )

    fun selectTag(tag: HomeTag) {
        _selectedTag.value = tag
        _searchQuery.value = "" // Clear search when switching tabs
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
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

                // 1. Just insert the new ones. The DB unique constraint + OnConflictStrategy.IGNORE 
                //    will ensure we don't duplicate but also don't overwrite user attendance data.
                subjects.forEach { subject ->
                    attendanceRepository.insertSubject(
                        AttendanceEntity(
                            subjectName = subject.name,
                            program = program.name,
                            semester = semester
                        )
                    )
                }
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
