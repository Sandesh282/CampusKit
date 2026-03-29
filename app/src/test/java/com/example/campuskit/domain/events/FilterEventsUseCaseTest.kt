package com.example.campuskit.domain.events

import com.example.campuskit.data.events.Event
import com.example.campuskit.data.events.EventsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterEventsUseCaseTest {

    private val repository: EventsRepository = mockk(relaxed = true)
    private val useCase = FilterEventsUseCase(repository)

    @Test
    fun `invoke returns all events from repository`() = runTest {
        val mockEvents = listOf(
            Event("1", "Hackathon", "Feb 15", "Lab 1", "", "Coding Club", "Desc"),
            Event("2", "Fest", "Feb 22", "OAT", "", "Cultural Senate", "Desc"),
        )
        every { repository.getEvents() } returns flowOf(mockEvents)

        val result = useCase().first()

        assertEquals(mockEvents, result)
        assertEquals(2, result.size)
    }

    @Test
    fun `invoke returns empty list when repository is empty`() = runTest {
        every { repository.getEvents() } returns flowOf(emptyList())

        val result = useCase().first()

        assertEquals(emptyList<Event>(), result)
    }
}
