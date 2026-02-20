package com.example.campuskit.data.attendance

import com.example.campuskit.domain.attendance.AttendanceStatus
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

class AttendanceRepositoryTest {

    private lateinit var attendanceDao: AttendanceDao
    private lateinit var timetableDao: TimetableDao
    private lateinit var repository: AttendanceRepository

    @Before
    fun setup() {
        attendanceDao = mockk()
        timetableDao = mockk()
        repository = AttendanceRepository(attendanceDao, timetableDao)
    }

    @Test
    fun getAllSubjects_ReturnsFlowFromDao() = runTest {
        val mockData = listOf(
            AttendanceEntity(1, "OS", 10, 12, 75f)
        )
        every { attendanceDao.getAllSubjects() } returns flowOf(mockData)

        val result = repository.getAllSubjects().first()
        
        assertEquals(mockData, result)
        coVerify(exactly = 1) { attendanceDao.getAllSubjects() }
    }

    @Test
    fun getAllTimetableItems_ReturnsFlowFromDao() = runTest {
        val mockData = listOf(
            TimetableEntity(1, 1, "09:00", 1L)
        )
        every { timetableDao.getAll() } returns flowOf(mockData)

        val result = repository.getAllTimetableItems().first()
        
        assertEquals(mockData, result)
        coVerify(exactly = 1) { timetableDao.getAll() }
    }

    @Test
    fun insertSubject_InsertsIntoDao() = runTest {
        val subject = AttendanceEntity(subjectName = "Compiler Design")
        coEvery { attendanceDao.insert(any()) } returns 1L

        repository.insertSubject(subject)

        coVerify(exactly = 1) { attendanceDao.insert(subject) }
    }

    @Test
    fun deleteSubject_DeletesFromDao() = runTest {
        val subject = AttendanceEntity(1, "OS", 10, 12, 75f)
        coEvery { attendanceDao.delete(any()) } returns Unit

        repository.deleteSubject(subject)

        coVerify(exactly = 1) { attendanceDao.delete(subject) }
    }
}
