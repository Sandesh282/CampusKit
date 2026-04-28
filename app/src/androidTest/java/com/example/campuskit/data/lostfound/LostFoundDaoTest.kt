package com.example.campuskit.data.lostfound

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
class LostFoundDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: LostFoundDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.lostFoundDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetAllItems() = runTest {
        val item = LostFoundEntity(
            id = "lf1", itemName = "Keys", foundLocation = "Lobby",
            imageUrlsCsv = "", timestamp = "Now",
        )
        dao.insert(item)

        val items = dao.getAllItems().first()
        assertEquals(1, items.size)
        assertEquals("Keys", items[0].itemName)
    }

    @Test
    fun insertAll_ignoresDuplicates() = runTest {
        val items = listOf(
            LostFoundEntity("lf1", "Keys", "Lobby", "", "Now"),
            LostFoundEntity("lf1", "Duplicate", "Lab", "", "Now"),
        )
        dao.insertAll(items)

        val result = dao.getAllItems().first()
        assertEquals(1, result.size)
        assertEquals("Keys", result[0].itemName)
    }

    @Test
    fun getCount_returnsCorrectCount() = runTest {
        assertEquals(0, dao.getCount())

        dao.insert(LostFoundEntity("lf1", "Keys", "Lobby", "", "Now"))
        dao.insert(LostFoundEntity("lf2", "Phone", "Lab", "", "Now"))

        assertEquals(2, dao.getCount())
    }

    @Test
    fun deleteById_removesItem() = runTest {
        dao.insert(LostFoundEntity("lf1", "Keys", "Lobby", "", "Now"))
        dao.insert(LostFoundEntity("lf2", "Phone", "Lab", "", "Now"))

        dao.deleteById("lf1")

        val items = dao.getAllItems().first()
        assertEquals(1, items.size)
        assertEquals("lf2", items[0].id)
    }

    @Test
    fun imageUrlsCsv_preservesCorrectly() = runTest {
        val item = LostFoundEntity(
            id = "lf1", itemName = "Bag", foundLocation = "Canteen",
            imageUrlsCsv = "url1,url2,url3", timestamp = "Now",
        )
        dao.insert(item)

        val result = dao.getAllItems().first()[0]
        assertEquals("url1,url2,url3", result.imageUrlsCsv)
    }
}
