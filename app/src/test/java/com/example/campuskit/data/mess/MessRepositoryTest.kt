package com.example.campuskit.data.mess

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MessRepositoryTest {

    private lateinit var yuckDao: YuckItemDao
    private lateinit var repository: MessRepository

    @Before
    fun setup() {
        yuckDao = mockk()
        repository = MessRepository(yuckDao)
    }

    @Test
    fun getAllYuckItems_ReturnsFlowFromDao() = runTest {
        val mockData = listOf(YuckItemEntity(1, "Karela"))
        every { yuckDao.getAll() } returns flowOf(mockData)

        val result = repository.getAllYuckItems().first()

        assertEquals(mockData, result)
        coVerify(exactly = 1) { yuckDao.getAll() }
    }

    @Test
    fun insertYuckItem_CallsDao() = runTest {
        val item = YuckItemEntity(1, "Tori")
        coEvery { yuckDao.insert(any()) } returns 1L

        repository.insertYuckItem(item)

        coVerify(exactly = 1) { yuckDao.insert(item) }
    }

    @Test
    fun deleteYuckItemByName_CallsDao() = runTest {
        val itemName = "Tori"
        coEvery { yuckDao.deleteByName(any()) } returns Unit

        repository.deleteYuckItemByName(itemName)

        coVerify(exactly = 1) { yuckDao.deleteByName(itemName) }
    }
}
