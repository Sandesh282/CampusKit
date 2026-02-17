package com.example.campuskit.domain.attendance

import com.example.campuskit.data.attendance.AttendanceRepository
import javax.inject.Inject

class UpdateAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend fun markAttended(subjectId: Long) {
        repository.markAttended(subjectId)
    }

    suspend fun markBunked(subjectId: Long) {
        repository.markBunked(subjectId)
    }
}
