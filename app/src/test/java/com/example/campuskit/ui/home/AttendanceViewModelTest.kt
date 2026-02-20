package com.example.campuskit.ui.home

import app.cash.turbine.test
import com.example.campuskit.data.attendance.AttendanceEntity
import com.example.campuskit.data.attendance.AttendanceRepository
import com.example.campuskit.domain.attendance.AttendanceStatus
import com.example.campuskit.domain.attendance.CalculateSafeBunksUseCase
import com.example.campuskit.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AttendanceViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: AttendanceRepository
    private lateinit var calculateSafeBunksUseCase: CalculateSafeBunksUseCase
    private lateinit var viewModel: AttendanceViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        calculateSafeBunksUseCase = mockk()
        
        // Mock default behavior for stateflow initialization
        every { repository.getAllSubjects() } returns flowOf(emptyList())
        viewModel = AttendanceViewModel(repository, calculateSafeBunksUseCase)
    }

    @Test
    fun getSubjects_initializesWithEmptyList() = runTest {
        viewModel.subjects.test {
            assertEquals(emptyList<AttendanceEntity>(), awaitItem())
        }
    }
    
    @Test
    fun getSubjects_collectsFromRepository() = runTest {
        val mockData = listOf(AttendanceEntity(1, "Cloud Computing", 15, 20, 75f))
        every { repository.getAllSubjects() } returns flowOf(mockData)
        
        // Re-init viewModel to pick up the new flow override
        viewModel = AttendanceViewModel(repository, calculateSafeBunksUseCase)

        viewModel.subjects.test {
            assertEquals(mockData, awaitItem())
        }
    }

    @Test
    fun addSubject_callsRepositoryInsert() = runTest {
        val subjectName = "Machine Learning"
        viewModel.addSubject(subjectName)

        coVerify(exactly = 1) { 
            repository.insertSubject(match { it.subjectName == subjectName })
        }
    }

    @Test
    fun markAttended_callsRepository() = runTest {
        viewModel.markAttended(1L)
        coVerify(exactly = 1) { repository.markAttended(1L) }
    }

    @Test
    fun markBunked_callsRepository() = runTest {
        viewModel.markBunked(2L)
        coVerify(exactly = 1) { repository.markBunked(2L) }
    }

    @Test
    fun getAttendanceStatus_delegatesToUseCase() {
        val expectedStatus = AttendanceStatus.Safe(2)
        every { calculateSafeBunksUseCase(10, 12, 75f) } returns expectedStatus

        val result = viewModel.getAttendanceStatus(10, 12, 75f)

        assertEquals(expectedStatus, result)
    }
}
