package com.example.campuskit.ui.calendar

import androidx.lifecycle.ViewModel
import com.example.campuskit.data.calendar.CalendarEvent
import com.example.campuskit.data.calendar.CourseInfo
import com.example.campuskit.data.calendar.MockCalendarData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth

enum class CalendarMode { GRID, TIMELINE }

class CalendarViewModel : ViewModel() {

    private val allEvents = MockCalendarData.getEvents()
    private val allCourses = MockCalendarData.getCourses()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _calendarMode = MutableStateFlow(CalendarMode.GRID)
    val calendarMode: StateFlow<CalendarMode> = _calendarMode.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _eventsForSelectedDate = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val eventsForSelectedDate: StateFlow<List<CalendarEvent>> = _eventsForSelectedDate.asStateFlow()

    val events: List<CalendarEvent> get() = allEvents
    val courses: List<CourseInfo> get() = allCourses

    init {
        updateEventsForDate(_selectedDate.value)
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _currentMonth.value = YearMonth.from(date)
        updateEventsForDate(date)
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

    private fun updateEventsForDate(date: LocalDate) {
        _eventsForSelectedDate.value = allEvents.filter { it.date == date }.sortedBy { it.startTime }
    }
}
