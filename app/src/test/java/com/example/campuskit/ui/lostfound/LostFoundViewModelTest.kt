package com.example.campuskit.ui.lostfound

import app.cash.turbine.test
import com.example.campuskit.data.lostfound.LostFoundItem
import com.example.campuskit.data.lostfound.LostFoundRepository
import com.example.campuskit.utils.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LostFoundViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: LostFoundRepository
    private lateinit var viewModel: LostFoundViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        every { repository.getItems() } returns flowOf(emptyList())

        viewModel = LostFoundViewModel(repository)
    }

    @Test
    fun items_collectsFromRepository() = runTest {
        val mockData = listOf(
            LostFoundItem("1", "Keys", "Lobby", emptyList(), "Now")
        )
        every { repository.getItems() } returns flowOf(mockData)

        // Re-init viewmodel
        viewModel = LostFoundViewModel(repository)

        viewModel.items.test {
            assertEquals(mockData, awaitItem())
        }
    }

    @Test
    fun addItem_delegatesToRepository() {
        val item = LostFoundItem("1", "Keys", "Lobby", emptyList(), "Now")
        
        viewModel.addItem(item)
        
        verify(exactly = 1) { repository.addItem(item) }
    }
}
