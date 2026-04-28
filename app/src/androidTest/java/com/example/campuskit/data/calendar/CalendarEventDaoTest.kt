package com.example.campuskit.data.calendar

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.campuskit.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CalendarEventDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: CalendarEventDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.calendarEventDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetAllEvents() = runTest {
        val event = CalendarEventEntity(
            id = "c1", title = "Team Meeting",
            dateString = "2026-04-01",
            startTimeString = "09:00",
            endTimeString = "10:00",
            colorArgb = 0xFFFF0000.toInt(),
        )
        dao.insert(event)

        val events = dao.getAllEvents().first()
        assertEquals(1, events.size)
        assertEquals("Team Meeting", events[0].title)
    }

    @Test
    fun getEventsForDate_filtersCorrectly() = runTest {
        dao.insert(CalendarEventEntity("c1", "Event A", "2026-04-01", "09:00", "10:00", 0xFF0000))
        dao.insert(CalendarEventEntity("c2", "Event B", "2026-04-02", "09:00", "10:00", 0x00FF00))
        dao.insert(CalendarEventEntity("c3", "Event C", "2026-04-01", "14:00", "15:00", 0x0000FF))

        val apr1Events = dao.getEventsForDate("2026-04-01").first()
        assertEquals(2, apr1Events.size)

        val apr2Events = dao.getEventsForDate("2026-04-02").first()
        assertEquals(1, apr2Events.size)
        assertEquals("Event B", apr2Events[0].title)
    }

    @Test
    fun insertAll_ignoresDuplicates() = runTest {
        val events = listOf(
            CalendarEventEntity("c1", "Event A", "2026-04-01", "09:00", "10:00", 0xFF0000),
            CalendarEventEntity("c1", "Duplicate", "2026-04-01", "09:00", "10:00", 0x00FF00),
        )
        dao.insertAll(events)

        val result = dao.getAllEvents().first()
        assertEquals(1, result.size)
        assertEquals("Event A", result[0].title)
    }

    @Test
    fun getCount_returnsCorrectCount() = runTest {
        assertEquals(0, dao.getCount())

        dao.insert(CalendarEventEntity("c1", "Event A", "2026-04-01", "09:00", "10:00", 0xFF0000))
        dao.insert(CalendarEventEntity("c2", "Event B", "2026-04-02", "09:00", "10:00", 0x00FF00))

        assertEquals(2, dao.getCount())
    }

    @Test
    fun delete_removesEvent() = runTest {
        val event = CalendarEventEntity("c1", "Event A", "2026-04-01", "09:00", "10:00", 0xFF0000)
        dao.insert(event)

        dao.delete(event)

        val events = dao.getAllEvents().first()
        assertTrue(events.isEmpty())
    }

    @Test
    fun optionalFields_preserveCorrectly() = runTest {
        val event = CalendarEventEntity(
            id = "c1", title = "Full Event",
            dateString = "2026-04-05",
            startTimeString = "14:00",
            endTimeString = "15:30",
            colorArgb = 0xFF00FF,
            assignee = "John Doe",
            attendeeCount = 12,
            location = "Room 302",
            description = "Sprint planning session",
        )
        dao.insert(event)

        val result = dao.getAllEvents().first()[0]
        assertEquals("John Doe", result.assignee)
        assertEquals(12, result.attendeeCount)
        assertEquals("Room 302", result.location)
        assertEquals("Sprint planning session", result.description)
    }
}
