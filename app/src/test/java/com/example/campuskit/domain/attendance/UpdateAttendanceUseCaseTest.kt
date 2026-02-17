package com.example.campuskit.domain.attendance

import com.example.campuskit.data.attendance.AttendanceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateAttendanceUseCaseTest {

    private val repository: AttendanceRepository = mockk()
    private val useCase = UpdateAttendanceUseCase(repository)

    @Test
    fun `markAttended calls repository markAttended`() = runTest {
        val subjectId = 1L
        coEvery { repository.markAttended(subjectId) } returns Unit

        useCase.markAttended(subjectId)

        coVerify { repository.markAttended(subjectId) }
    }

    @Test
    fun `markBunked calls repository markBunked`() = runTest {
        val subjectId = 1L
        coEvery { repository.markBunked(subjectId) } returns Unit

        useCase.markBunked(subjectId)

        coVerify { repository.markBunked(subjectId) }
    }
}
