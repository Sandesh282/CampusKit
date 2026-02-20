package com.example.campuskit.data.lostfound

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LostFoundRepositoryTest {

    @Test
    fun getItems_ReturnsInitialMockItems() = runTest {
        val repository = LostFoundRepository()
        
        val items = repository.getItems().first()
        
        assertEquals(MockLostFound.getItems(), items)
    }

    @Test
    fun addItem_PrependsToItemList() = runTest {
        val repository = LostFoundRepository()
        
        val initialSize = repository.getItems().first().size
        
        val newItem = LostFoundItem(
            id = "new_1",
            itemName = "Calculator",
            description = "Casio fx-991EX",
            foundLocation = "Library",
            imageUrls = emptyList(),
            timestamp = "Just now",
            contactWhatsApp = null,
            contactTelegram = null,
            contactPhone = "12345"
        )
        
        repository.addItem(newItem)
        
        val finalItems = repository.getItems().first()
        assertEquals(initialSize + 1, finalItems.size)
        assertEquals(newItem, finalItems[0]) // Prepended to top
    }
}
