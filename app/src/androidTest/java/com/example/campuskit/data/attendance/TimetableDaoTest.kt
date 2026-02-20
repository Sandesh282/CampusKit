package com.example.campuskit.data.attendance

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.campuskit.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TimetableDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: TimetableDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.timetableDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetTimetable() = runTest {
        val entity = TimetableEntity(
            dayOfWeek = 1,
            startTime = "09:00",
            subjectId = 101L,
        )
        dao.insert(entity)

        val items = dao.getAll().first()
        assertEquals(1, items.size)
        assertEquals(1, items[0].dayOfWeek)
        assertEquals("09:00", items[0].startTime)
        assertEquals(101L, items[0].subjectId)
    }

    @Test
    fun getByDay_FiltersAndSortsCorrectly() = runTest {
        val mondayClass1 = TimetableEntity(dayOfWeek = 1, startTime = "11:00", subjectId = 102L)
        val mondayClass2 = TimetableEntity(dayOfWeek = 1, startTime = "09:00", subjectId = 101L)
        val tuesdayClass = TimetableEntity(dayOfWeek = 2, startTime = "10:00", subjectId = 103L)
        
        dao.insert(mondayClass1)
        dao.insert(mondayClass2)
        dao.insert(tuesdayClass)

        // Test getting items by day
        val mondayItems = dao.getByDay(1).first()
        
        assertEquals(2, mondayItems.size)
        // Ensure sorted by startTime ASC
        assertEquals("09:00", mondayItems[0].startTime)
        assertEquals("11:00", mondayItems[1].startTime)
        
        val tuesdayItems = dao.getByDay(2).first()
        assertEquals(1, tuesdayItems.size)
        assertEquals("10:00", tuesdayItems[0].startTime)
    }

    @Test
    fun getAll_SortsByDayThenTime() = runTest {
        val class1 = TimetableEntity(dayOfWeek = 2, startTime = "09:00", subjectId = 103L)
        val class2 = TimetableEntity(dayOfWeek = 1, startTime = "11:00", subjectId = 102L)
        val class3 = TimetableEntity(dayOfWeek = 1, startTime = "09:00", subjectId = 101L)
        
        dao.insert(class1)
        dao.insert(class2)
        dao.insert(class3)

        val allItems = dao.getAll().first()
        assertEquals(3, allItems.size)
        
        // Expected order:
        // Day 1, 09:00
        // Day 1, 11:00
        // Day 2, 09:00
        assertEquals(1, allItems[0].dayOfWeek)
        assertEquals("09:00", allItems[0].startTime)
        
        assertEquals(1, allItems[1].dayOfWeek)
        assertEquals("11:00", allItems[1].startTime)
        
        assertEquals(2, allItems[2].dayOfWeek)
        assertEquals("09:00", allItems[2].startTime)
    }

    @Test
    fun deleteTimetable() = runTest {
        val entity = TimetableEntity(dayOfWeek = 1, startTime = "09:00", subjectId = 101L)
        dao.insert(entity)

        var items = dao.getAll().first()
        assertEquals(1, items.size)

        val itemToDelete = items[0]
        dao.delete(itemToDelete)

        items = dao.getAll().first()
        assertTrue(items.isEmpty())
    }
}
