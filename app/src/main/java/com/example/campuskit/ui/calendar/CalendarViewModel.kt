package com.example.campuskit.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.data.calendar.CalendarEvent
import com.example.campuskit.data.calendar.CalendarRepository
import com.example.campuskit.data.calendar.CourseInfo
import com.example.campuskit.data.calendar.MockCalendarData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

enum class CalendarMode { GRID, TIMELINE }

/**
 * ViewModel for the Calendar feature.
 *
 * Persists events in Room via [CalendarRepository] and exposes reactive
 * [StateFlow]s for the selected date, calendar mode, and filtered events.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository,
) : ViewModel() {

    private val allCourses = MockCalendarData.getCourses()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _calendarMode = MutableStateFlow(CalendarMode.GRID)
    val calendarMode: StateFlow<CalendarMode> = _calendarMode.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    /** All calendar events from Room. */
    val events: StateFlow<List<CalendarEvent>> = repository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Events filtered to the currently selected date. */
    val eventsForSelectedDate: StateFlow<List<CalendarEvent>> =
        _selectedDate.flatMapLatest { date ->
            repository.getEventsForDate(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val courses: List<CourseInfo> get() = allCourses

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _currentMonth.value = YearMonth.from(date)
    }

    fun toggleMode() {
        _calendarMode.value = when (_calendarMode.value) {
            CalendarMode.GRID -> CalendarMode.TIMELINE
            CalendarMode.TIMELINE -> CalendarMode.GRID
        }
    }

    fun navigateMonth(delta: Int) {
        _currentMonth.value = _currentMonth.value.plusMonths(delta.toLong())
    }

    /** Persists a new event to Room. */
    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.addEvent(event)
        }
    }
}
