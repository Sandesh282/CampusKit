package com.example.campuskit.data.attendance

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val timetableDao: TimetableDao
) {
    fun getAllSubjects(): Flow<List<AttendanceEntity>> = attendanceDao.getAllSubjects()

    suspend fun insertSubject(subject: AttendanceEntity) = attendanceDao.insert(subject)

    suspend fun deleteSubject(subject: AttendanceEntity) = attendanceDao.delete(subject)

    suspend fun markAttended(subjectId: Long) = attendanceDao.markAttended(subjectId)

    suspend fun markBunked(subjectId: Long) = attendanceDao.markBunked(subjectId)
    
    fun getAllTimetableItems(): Flow<List<TimetableEntity>> = timetableDao.getAll()
}
