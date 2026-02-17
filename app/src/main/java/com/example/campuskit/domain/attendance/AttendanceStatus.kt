package com.example.campuskit.domain.attendance

sealed class AttendanceStatus {
    data object NoData : AttendanceStatus()
    data class Safe(val canBunk: Int) : AttendanceStatus()
    data class Warning(val canBunk: Int) : AttendanceStatus()
    data class Critical(val needToAttend: Int) : AttendanceStatus()
}
