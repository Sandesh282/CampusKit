package com.example.campuskit.data.lostfound

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LostFoundRepositoryTest {

    private val mockDao: LostFoundDao = mockk(relaxed = true)

    @Test
    fun getItems_mapsEntitiesToDomainModels() = runTest {
        val entities = listOf(
            LostFoundEntity(
                id = "1", itemName = "Keys", foundLocation = "Lobby",
                imageUrlsCsv = "", timestamp = "Now",
            )
        )
        coEvery { mockDao.getAllItems() } returns flowOf(entities)

        val repository = LostFoundRepository(mockDao)
        val items = repository.getItems().first()

        assertEquals(1, items.size)
        assertEquals("Keys", items[0].itemName)
        assertEquals(emptyList<String>(), items[0].imageUrls)
    }

    @Test
    fun getItems_parsesImageUrlsCsv() = runTest {
        val entities = listOf(
            LostFoundEntity(
                id = "1", itemName = "Phone", foundLocation = "Lab",
                imageUrlsCsv = "url1,url2,url3", timestamp = "Now",
            )
        )
        coEvery { mockDao.getAllItems() } returns flowOf(entities)

        val repository = LostFoundRepository(mockDao)
        val items = repository.getItems().first()

        assertEquals(listOf("url1", "url2", "url3"), items[0].imageUrls)
    }

    @Test
    fun addItem_insertsEntityInDao() = runTest {
        val repository = LostFoundRepository(mockDao)
        val item = LostFoundItem(
            id = "new_1", itemName = "Calculator", foundLocation = "Library",
            imageUrls = emptyList(), timestamp = "Just now",
        )

        repository.addItem(item)

        coVerify { mockDao.insert(any()) }
    }

    @Test
    fun seedIfEmpty_insertsWhenCountIsZero() = runTest {
        coEvery { mockDao.getCount() } returns 0

        val repository = LostFoundRepository(mockDao)
        repository.seedIfEmpty()

        coVerify { mockDao.insertAll(any()) }
    }

    @Test
    fun seedIfEmpty_skipsWhenDataExists() = runTest {
        coEvery { mockDao.getCount() } returns 3

        val repository = LostFoundRepository(mockDao)
        repository.seedIfEmpty()

        coVerify(exactly = 0) { mockDao.insertAll(any()) }
    }
}
