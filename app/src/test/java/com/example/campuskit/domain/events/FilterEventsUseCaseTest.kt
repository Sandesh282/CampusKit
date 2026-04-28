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

    private val sampleEvents = listOf(
        Event("1", "Hackathon", "Feb 15", "Lab 1", "", "Coding Club", "Build cool stuff"),
        Event("2", "Cultural Fest", "Feb 22", "OAT", "", "Cultural Senate", "Annual fest"),
        Event("3", "Tech Talk", "Mar 5", "Room 302", "", "IEEE Chapter", "AI and ML seminar"),
    )

    @Test
    fun `invoke with blank query returns all events`() = runTest {
        every { repository.getEvents() } returns flowOf(sampleEvents)

        val result = useCase("").first()

        assertEquals(sampleEvents, result)
        assertEquals(3, result.size)
    }

    @Test
    fun `invoke returns empty list when repository is empty`() = runTest {
        every { repository.getEvents() } returns flowOf(emptyList())

        val result = useCase().first()

        assertEquals(emptyList<Event>(), result)
    }

    @Test
    fun `invoke filters by title case-insensitively`() = runTest {
        every { repository.getEvents() } returns flowOf(sampleEvents)

        val result = useCase("hackathon").first()

        assertEquals(1, result.size)
        assertEquals("Hackathon", result[0].title)
    }

    @Test
    fun `invoke filters by organizer`() = runTest {
        every { repository.getEvents() } returns flowOf(sampleEvents)

        val result = useCase("IEEE").first()

        assertEquals(1, result.size)
        assertEquals("Tech Talk", result[0].title)
    }

    @Test
    fun `invoke filters by venue`() = runTest {
        every { repository.getEvents() } returns flowOf(sampleEvents)

        val result = useCase("OAT").first()

        assertEquals(1, result.size)
        assertEquals("Cultural Fest", result[0].title)
    }

    @Test
    fun `invoke filters by description`() = runTest {
        every { repository.getEvents() } returns flowOf(sampleEvents)

        val result = useCase("seminar").first()

        assertEquals(1, result.size)
        assertEquals("Tech Talk", result[0].title)
    }

    @Test
    fun `invoke with no matching query returns empty list`() = runTest {
        every { repository.getEvents() } returns flowOf(sampleEvents)

        val result = useCase("nonexistent").first()

        assertEquals(0, result.size)
    }
}
