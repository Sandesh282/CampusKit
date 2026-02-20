package com.example.campuskit.data.mess

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
class YuckItemDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: YuckItemDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.yuckItemDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetYuckItems() = runTest {
        val item = YuckItemEntity(itemName = "Karela")
        dao.insert(item)

        val items = dao.getAll().first()
        assertEquals(1, items.size)
        assertEquals("Karela", items[0].itemName)
    }

    @Test
    fun insertMultipleItems_RetrievesInAlphabeticalOrder() = runTest {
        val item1 = YuckItemEntity(itemName = "Tinda")
        val item2 = YuckItemEntity(itemName = "Karela")
        val item3 = YuckItemEntity(itemName = "Baingan")
        
        dao.insert(item1)
        dao.insert(item2)
        dao.insert(item3)

        val items = dao.getAll().first()
        assertEquals(3, items.size)
        assertEquals("Baingan", items[0].itemName)
        assertEquals("Karela", items[1].itemName)
        assertEquals("Tinda", items[2].itemName)
    }

    @Test
    fun deleteYuckItem() = runTest {
        val item = YuckItemEntity(itemName = "Karela")
        dao.insert(item)

        // we need the item from the DB because the inserted ID is auto-generated
        var items = dao.getAll().first()
        assertEquals(1, items.size)
        
        val itemToDelete = items[0]
        dao.delete(itemToDelete)

        items = dao.getAll().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun deleteByName() = runTest {
        dao.insert(YuckItemEntity(itemName = "Tinda"))
        dao.insert(YuckItemEntity(itemName = "Karela"))
        
        var items = dao.getAll().first()
        assertEquals(2, items.size)
        
        dao.deleteByName("Tinda")
        
        items = dao.getAll().first()
        assertEquals(1, items.size)
        assertEquals("Karela", items[0].itemName)
    }
}
