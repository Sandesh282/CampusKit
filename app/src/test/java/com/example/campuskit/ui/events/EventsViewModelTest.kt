package com.example.campuskit.ui.events

import app.cash.turbine.test
import com.example.campuskit.domain.events.FilterEventsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventsViewModelTest {

    private val filterEventsUseCase: FilterEventsUseCase = mockk()

    @Test
    fun `toggleReminder adds eventId if not present`() = runTest {
        every { filterEventsUseCase() } returns flowOf(emptyList())
        val viewModel = EventsViewModel(filterEventsUseCase)

        viewModel.toggleReminder("1")

        viewModel.remindedEvents.test {
            assertEquals(setOf("1"), awaitItem())
        }
    }

    @Test
    fun `toggleReminder removes eventId if present`() = runTest {
        every { filterEventsUseCase() } returns flowOf(emptyList())
        val viewModel = EventsViewModel(filterEventsUseCase)

        viewModel.toggleReminder("1")
        viewModel.toggleReminder("1")

        viewModel.remindedEvents.test {
            assertTrue(awaitItem().isEmpty())
        }
    }
}
