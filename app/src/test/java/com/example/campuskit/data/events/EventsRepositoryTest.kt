package com.example.campuskit.data.events

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class EventsRepositoryTest {

    private val mockDao: EventDao = mockk(relaxed = true)

    @Test
    fun getEvents_mapsEntitiesToDomainModels() = runTest {
        val entities = listOf(
            EventEntity("1", "Hackathon", "Feb 15", "Lab 1", "", "Coding Club", "Desc")
        )
        coEvery { mockDao.getAllEvents() } returns flowOf(entities)

        val repository = EventsRepository(mockDao)
        val events = repository.getEvents().first()

        assertEquals(1, events.size)
        assertEquals("Hackathon", events[0].title)
        assertEquals("Coding Club", events[0].organizer)
    }

    @Test
    fun seedIfEmpty_insertsWhenCountIsZero() = runTest {
        coEvery { mockDao.getCount() } returns 0

        val repository = EventsRepository(mockDao)
        repository.seedIfEmpty()

        io.mockk.coVerify { mockDao.insertAll(any()) }
    }

    @Test
    fun seedIfEmpty_skipsWhenDataExists() = runTest {
        coEvery { mockDao.getCount() } returns 5

        val repository = EventsRepository(mockDao)
        repository.seedIfEmpty()

        io.mockk.coVerify(exactly = 0) { mockDao.insertAll(any()) }
    }

    @Test
    fun toggleReminder_delegatesToDao() = runTest {
        val repository = EventsRepository(mockDao)
        repository.toggleReminder("event_1")

        io.mockk.coVerify { mockDao.toggleReminder("event_1") }
    }
}
