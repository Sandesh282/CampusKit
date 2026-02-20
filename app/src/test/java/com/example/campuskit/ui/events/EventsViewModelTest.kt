package com.example.campuskit.ui.events

import app.cash.turbine.test
import com.example.campuskit.data.events.Event
import com.example.campuskit.domain.events.FilterEventsUseCase
import com.example.campuskit.utils.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var filterEventsUseCase: FilterEventsUseCase
    private lateinit var viewModel: EventsViewModel

    @Before
    fun setup() {
        filterEventsUseCase = mockk()
        every { filterEventsUseCase() } returns flowOf(emptyList())

        viewModel = EventsViewModel(filterEventsUseCase)
    }

    @Test
    fun getEvents_collectsFromUseCase() = runTest {
        val mockEvents = listOf(
            Event("1", "Tech Talk", "Feb 15", "Room 1", "", "Coding Club", "")
        )
        every { filterEventsUseCase() } returns flowOf(mockEvents)
        
        // Re-initialize to pick up the mocked flow
        viewModel = EventsViewModel(filterEventsUseCase)

        viewModel.events.test {
            assertEquals(mockEvents, awaitItem())
        }
    }

    @Test
    fun getRemindedEvents_initializesEmpty() = runTest {
        viewModel.remindedEvents.test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    @Test
    fun toggleReminder_addsEventIdIfNotPresent() = runTest {
        viewModel.toggleReminder("event_1")

        viewModel.remindedEvents.test {
            val items = awaitItem()
            assertTrue(items.contains("event_1"))
            assertEquals(1, items.size)
        }
    }

    @Test
    fun toggleReminder_removesEventIdIfPresent() = runTest {
        // Add it first
        viewModel.toggleReminder("event_1")
        
        // Remove it
        viewModel.toggleReminder("event_1")

        viewModel.remindedEvents.test {
            val items = awaitItem()
            assertFalse(items.contains("event_1"))
            assertTrue(items.isEmpty())
        }
    }
}
