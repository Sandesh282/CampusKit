package com.example.campuskit.domain.events

import com.example.campuskit.data.events.Event
import com.example.campuskit.data.events.EventsRepository
import com.example.campuskit.data.events.MockEvents
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterEventsUseCaseTest {

    private val repository: EventsRepository = mockk()
    private val useCase = FilterEventsUseCase(repository)

    @Test
    fun `invoke returns all events from repository`() = runTest {
        val mockEvents = MockEvents.getEvents()
        every { repository.getEvents() } returns flowOf(mockEvents)

        val result = useCase().first()

        assertEquals(mockEvents, result)
    }
}
