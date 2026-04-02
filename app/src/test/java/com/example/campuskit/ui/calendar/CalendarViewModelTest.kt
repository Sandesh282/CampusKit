package com.example.campuskit.ui.calendar

import app.cash.turbine.test
import com.example.campuskit.data.calendar.CalendarEvent
import com.example.campuskit.data.calendar.CalendarRepository
import com.example.campuskit.utils.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class CalendarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CalendarRepository
    private lateinit var viewModel: CalendarViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        every { repository.getAllEvents() } returns flowOf(emptyList())
        every { repository.getEventsForDate(any()) } returns flowOf(emptyList())

        viewModel = CalendarViewModel(repository)
    }

    @Test
    fun events_initializesEmpty() = runTest {
        viewModel.events.test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    @Test
    fun eventsForSelectedDate_updatesWhenDateChanges() = runTest {
        val testDate = LocalDate.of(2026, 4, 10)
        val mockEvents = listOf(
            CalendarEvent(
                id = "c1", title = "Test Event",
                date = testDate,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0),
                color = androidx.compose.ui.graphics.Color.Red,
            )
        )
        every { repository.getEventsForDate(testDate) } returns flowOf(mockEvents)

        viewModel.selectDate(testDate)

        viewModel.eventsForSelectedDate.test {
            val events = awaitItem()
            assertEquals(1, events.size)
            assertEquals("Test Event", events[0].title)
        }
    }

    @Test
    fun toggleMode_switchesBetweenGridAndTimeline() {
        assertEquals(CalendarMode.GRID, viewModel.calendarMode.value)

        viewModel.toggleMode()
        assertEquals(CalendarMode.TIMELINE, viewModel.calendarMode.value)

        viewModel.toggleMode()
        assertEquals(CalendarMode.GRID, viewModel.calendarMode.value)
    }

    @Test
    fun addEvent_delegatesToRepository() = runTest {
        val event = CalendarEvent(
            id = "c99", title = "New Event",
            date = LocalDate.now(),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            color = androidx.compose.ui.graphics.Color.Blue,
        )

        viewModel.addEvent(event)

        coVerify { repository.addEvent(event) }
    }

    @Test
    fun seedIfEmpty_calledOnInit() = runTest {
        coVerify { repository.seedIfEmpty() }
    }

    @Test
    fun selectDate_updatesSelectedDateAndMonth() {
        val date = LocalDate.of(2026, 5, 15)

        viewModel.selectDate(date)

        assertEquals(date, viewModel.selectedDate.value)
        assertEquals(java.time.YearMonth.of(2026, 5), viewModel.currentMonth.value)
    }

    @Test
    fun navigateMonth_updatesCurrentMonth() {
        val initialMonth = viewModel.currentMonth.value

        viewModel.navigateMonth(1)

        assertEquals(initialMonth.plusMonths(1), viewModel.currentMonth.value)
    }
}
