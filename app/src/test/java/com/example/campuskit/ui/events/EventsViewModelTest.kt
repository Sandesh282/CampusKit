package com.example.campuskit.ui.events

import app.cash.turbine.test
import com.example.campuskit.data.events.Event
import com.example.campuskit.data.events.EventsRepository
import com.example.campuskit.domain.events.FilterEventsUseCase
import com.example.campuskit.utils.MainDispatcherRule
import io.mockk.coEvery
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

class EventsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var filterEventsUseCase: FilterEventsUseCase
    private lateinit var repository: EventsRepository
    private lateinit var viewModel: EventsViewModel

    @Before
    fun setup() {
        filterEventsUseCase = mockk()
        repository = mockk(relaxed = true)
        every { filterEventsUseCase() } returns flowOf(emptyList())
        every { repository.getRemindedEventIds() } returns flowOf(emptyList())

        viewModel = EventsViewModel(filterEventsUseCase, repository)
    }

    @Test
    fun getEvents_collectsFromUseCase() = runTest {
        val mockEvents = listOf(
            Event("1", "Tech Talk", "Feb 15", "Room 1", "", "Coding Club", "")
        )
        every { filterEventsUseCase() } returns flowOf(mockEvents)

        viewModel = EventsViewModel(filterEventsUseCase, repository)

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
    fun toggleReminder_delegatesToRepository() = runTest {
        viewModel.toggleReminder("event_1")

        coVerify { repository.toggleReminder("event_1") }
    }

    @Test
    fun seedIfEmpty_calledOnInit() = runTest {
        coVerify { repository.seedIfEmpty() }
    }
}
