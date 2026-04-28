package com.example.campuskit.data.events

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.campuskit.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class EventDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: EventDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.eventDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetAllEvents() = runTest {
        val event = EventEntity("e1", "Hackathon", "Feb 15", "Lab 1", "", "Coding Club", "Desc")
        dao.insert(event)

        val events = dao.getAllEvents().first()
        assertEquals(1, events.size)
        assertEquals("Hackathon", events[0].title)
    }

    @Test
    fun insertAll_ignoresDuplicates() = runTest {
        val events = listOf(
            EventEntity("e1", "Hackathon", "Feb 15", "Lab 1", "", "Coding Club", ""),
            EventEntity("e1", "Duplicate", "Feb 15", "Lab 1", "", "Other", ""),
        )
        dao.insertAll(events)

        val result = dao.getAllEvents().first()
        assertEquals(1, result.size)
        assertEquals("Hackathon", result[0].title)
    }

    @Test
    fun toggleReminder_togglesFlag() = runTest {
        val event = EventEntity("e1", "Hackathon", "Feb 15", "Lab 1", "", "Coding Club", "")
        dao.insert(event)

        assertFalse(dao.getAllEvents().first()[0].isReminded)

        dao.toggleReminder("e1")
        assertTrue(dao.getAllEvents().first()[0].isReminded)

        dao.toggleReminder("e1")
        assertFalse(dao.getAllEvents().first()[0].isReminded)
    }

    @Test
    fun getRemindedEventIds_returnsOnlyRemindedIds() = runTest {
        dao.insert(EventEntity("e1", "Event 1", "Feb 15", "Lab", "", "Club", ""))
        dao.insert(EventEntity("e2", "Event 2", "Feb 16", "Lab", "", "Club", ""))

        dao.toggleReminder("e1")

        val reminded = dao.getRemindedEventIds().first()
        assertEquals(listOf("e1"), reminded)
    }

    @Test
    fun getCount_returnsCorrectCount() = runTest {
        assertEquals(0, dao.getCount())

        dao.insert(EventEntity("e1", "Event 1", "Feb 15", "Lab", "", "Club", ""))
        dao.insert(EventEntity("e2", "Event 2", "Feb 16", "Lab", "", "Club", ""))

        assertEquals(2, dao.getCount())
    }

    @Test
    fun deleteById_removesEvent() = runTest {
        dao.insert(EventEntity("e1", "Event 1", "Feb 15", "Lab", "", "Club", ""))
        dao.insert(EventEntity("e2", "Event 2", "Feb 16", "Lab", "", "Club", ""))

        dao.deleteById("e1")

        val events = dao.getAllEvents().first()
        assertEquals(1, events.size)
        assertEquals("e2", events[0].id)
    }
}
