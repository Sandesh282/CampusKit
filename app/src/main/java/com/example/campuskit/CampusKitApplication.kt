package com.example.campuskit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class CampusKitApplication : Application() {

    companion object {
        const val CHANNEL_ATTENDANCE = "attendance_channel"
        const val CHANNEL_YUCK = "yuck_alert_channel"
        const val CHANNEL_EVENTS = "event_reminder_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val attendanceChannel = NotificationChannel(
            CHANNEL_ATTENDANCE,
            "Attendance Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Pre-class attendance notifications"
        }

        val yuckChannel = NotificationChannel(
            CHANNEL_YUCK,
            "Yuck Alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily mess menu yuck alerts"
        }

        val eventsChannel = NotificationChannel(
            CHANNEL_EVENTS,
            "Event Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Campus event reminders"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(attendanceChannel)
        manager.createNotificationChannel(yuckChannel)
        manager.createNotificationChannel(eventsChannel)
    }
}
