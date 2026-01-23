package com.example.campuskit.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SystemEventReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BATTERY_LOW -> {
                Toast.makeText(
                    context,
                    "Battery is low. Save your work!",
                    Toast.LENGTH_LONG
                ).show()
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // In a real app, you'd reschedule all reminders here
                // For this MVP, we'll just show a toast
                Toast.makeText(
                    context,
                    "Device booted. App is ready.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
