package com.example.campuskit.domain.attendance

/**
 * Represents the current attendance health for a subject.
 *
 * Used by [CalculateSafeBunksUseCase] to classify a student's standing
 * based on their attended vs total classes and a minimum percentage threshold.
 */
sealed class AttendanceStatus {
    /** No classes have been recorded yet. */
    data object NoData : AttendanceStatus()

    /** Attendance is comfortably above the threshold. [canBunk] classes can be safely missed. */
    data class Safe(val canBunk: Int) : AttendanceStatus()

    /** Attendance is above threshold but within 10% margin. [canBunk] may be 0. */
    data class Warning(val canBunk: Int) : AttendanceStatus()

    /** Attendance is below the threshold. [needToAttend] consecutive classes must be attended to recover. */
    data class Critical(val needToAttend: Int) : AttendanceStatus()
}
