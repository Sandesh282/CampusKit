package com.example.campuskit.data.events

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class EventsRepositoryTest {

    @Test
    fun getEvents_ReturnsMockEventsFlow() = runTest {
        val repository = EventsRepository()
        
        val events = repository.getEvents().first()
        
        assertEquals(MockEvents.getEvents(), events)
    }
}
