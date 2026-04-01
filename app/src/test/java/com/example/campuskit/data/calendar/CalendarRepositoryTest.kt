package com.example.campuskit.data.calendar

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CalendarRepositoryTest {

    private val mockDao: CalendarEventDao = mockk(relaxed = true)

    @Test
    fun getAllEvents_mapsEntitiesToDomainModels() = runTest {
        val entities = listOf(
            CalendarEventEntity(
                id = "c1", title = "Team Meeting",
                dateString = "2026-04-01",
                startTimeString = "09:00",
                endTimeString = "10:00",
                colorArgb = 0xFFFF0000.toInt(),
            )
        )
        coEvery { mockDao.getAllEvents() } returns flowOf(entities)

        val repository = CalendarRepository(mockDao)
        val events = repository.getAllEvents().first()

        assertEquals(1, events.size)
        assertEquals("Team Meeting", events[0].title)
        assertEquals(LocalDate.of(2026, 4, 1), events[0].date)
    }

    @Test
    fun getEventsForDate_filtersCorrectly() = runTest {
        val date = LocalDate.of(2026, 4, 1)
        val entities = listOf(
            CalendarEventEntity(
                id = "c1", title = "Standup",
                dateString = "2026-04-01",
                startTimeString = "09:00",
                endTimeString = "09:30",
                colorArgb = 0xFF0000FF.toInt(),
            )
        )
        coEvery { mockDao.getEventsForDate("2026-04-01") } returns flowOf(entities)

        val repository = CalendarRepository(mockDao)
        val events = repository.getEventsForDate(date).first()

        assertEquals(1, events.size)
        assertEquals("Standup", events[0].title)
    }

    @Test
    fun addEvent_insertsEntityInDao() = runTest {
        val repository = CalendarRepository(mockDao)
        val event = CalendarEvent(
            id = "c99", title = "New Event",
            date = LocalDate.of(2026, 4, 5),
            startTime = java.time.LocalTime.of(14, 0),
            endTime = java.time.LocalTime.of(15, 0),
            color = androidx.compose.ui.graphics.Color.Blue,
        )

        repository.addEvent(event)

        coVerify { mockDao.insert(any()) }
    }

    @Test
    fun seedIfEmpty_insertsWhenCountIsZero() = runTest {
        coEvery { mockDao.getCount() } returns 0

        val repository = CalendarRepository(mockDao)
        repository.seedIfEmpty()

        coVerify { mockDao.insertAll(any()) }
    }

    @Test
    fun seedIfEmpty_skipsWhenDataExists() = runTest {
        coEvery { mockDao.getCount() } returns 5

        val repository = CalendarRepository(mockDao)
        repository.seedIfEmpty()

        coVerify(exactly = 0) { mockDao.insertAll(any()) }
    }
}
