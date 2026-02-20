package com.example.campuskit.ui.mess

import app.cash.turbine.test
import com.example.campuskit.data.mess.MessRepository
import com.example.campuskit.data.mess.YuckItemEntity
import com.example.campuskit.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MessViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: MessRepository
    private lateinit var viewModel: MessViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        every { repository.getAllYuckItems() } returns flowOf(emptyList())

        viewModel = MessViewModel(repository)
    }

    @Test
    fun yuckItems_collectsFromRepository() = runTest {
        val mockData = listOf(YuckItemEntity(2, "Bhindi"))
        every { repository.getAllYuckItems() } returns flowOf(mockData)

        // Re-initialize to pick up the mocked flow
        viewModel = MessViewModel(repository)

        viewModel.yuckItems.test {
            assertEquals(mockData, awaitItem())
        }
    }

    @Test
    fun todayMenu_isNotNull() = runTest {
        viewModel.todayMenu.test {
            assertNotNull(awaitItem())
        }
    }

    @Test
    fun weeklyMenu_isNotNullAndNotEmpty() = runTest {
        viewModel.weeklyMenu.test {
            val menu = awaitItem()
            assertNotNull(menu)
            assertTrue(menu.isNotEmpty())
        }
    }

    @Test
    fun addYuckItem_callsRepository() = runTest {
        viewModel.addYuckItem("Tinda")

        coVerify(exactly = 1) {
            repository.insertYuckItem(match { it.itemName == "Tinda" })
        }
    }

    @Test
    fun removeYuckItem_callsRepository() = runTest {
        viewModel.removeYuckItem("Tinda")

        coVerify(exactly = 1) { repository.deleteYuckItemByName("Tinda") }
    }

    @Test
    fun isYuckItem_returnsTrueForExactMatch() {
        val list = listOf(YuckItemEntity(1, "Karela"))
        val result = viewModel.isYuckItem("Karela", list)
        assertTrue(result)
    }

    @Test
    fun isYuckItem_returnsTrueForPartialMatchCaseInsensitive() {
        val list = listOf(YuckItemEntity(1, "Karela"))
        // E.g. menu contains "Crispy Karela Fry"
        val result = viewModel.isYuckItem("Crispy karela Fry", list)
        assertTrue(result)
    }

    @Test
    fun isYuckItem_returnsFalseIfNoMatch() {
        val list = listOf(YuckItemEntity(1, "Karela"))
        val result = viewModel.isYuckItem("Paneer Butter Masala", list)
        assertFalse(result)
    }
}
