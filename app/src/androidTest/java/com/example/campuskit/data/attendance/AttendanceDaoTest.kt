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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AttendanceDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AttendanceDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.attendanceDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetSubject() = runTest {
        val subject = AttendanceEntity(
            subjectId = 1,
            subjectName = "Android Development",
            attendedClasses = 5,
            totalClasses = 6,
            minimumPercentage = 75f
        )
        dao.insert(subject)
        
        val result = dao.getBySubjectId(1)
        assertNotNull(result)
        assertEquals("Android Development", result?.subjectName)
    }

    @Test
    fun markAttendedIncrementsBothClasses() = runTest {
        val subject = AttendanceEntity(
            subjectId = 1,
            subjectName = "OS",
            attendedClasses = 10,
            totalClasses = 15,
            minimumPercentage = 75f
        )
        dao.insert(subject)
        
        dao.markAttended(1)
        
        val result = dao.getBySubjectId(1)
        assertEquals(11, result?.attendedClasses)
        assertEquals(16, result?.totalClasses)
    }

    @Test
    fun markBunkedIncrementsOnlyTotalClasses() = runTest {
        val subject = AttendanceEntity(
            subjectId = 1,
            subjectName = "DBMS",
            attendedClasses = 10,
            totalClasses = 15,
            minimumPercentage = 75f
        )
        dao.insert(subject)
        
        dao.markBunked(1)
        
        val result = dao.getBySubjectId(1)
        assertEquals(10, result?.attendedClasses)
        assertEquals(16, result?.totalClasses)
    }

    @Test
    fun getAllSubjectsReturnsFlow() = runTest {
        val s1 = AttendanceEntity(subjectId = 1, subjectName = "B", attendedClasses = 0, totalClasses = 0)
        val s2 = AttendanceEntity(subjectId = 2, subjectName = "A", attendedClasses = 0, totalClasses = 0)
        dao.insert(s1)
        dao.insert(s2)
        
        val result = dao.getAllSubjects().first()
        assertEquals(2, result.size)
        assertEquals("A", result[0].subjectName) // Ordered ASC
        assertEquals("B", result[1].subjectName)
    }

    @Test
    fun deleteSubjectRemovesIt() = runTest {
        val subject = AttendanceEntity(subjectId = 1, subjectName = "DS", attendedClasses = 0, totalClasses = 0)
        dao.insert(subject)
        dao.delete(subject)
        
        val result = dao.getBySubjectId(1)
        assertNull(result)
    }
}
