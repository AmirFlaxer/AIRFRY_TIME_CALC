package com.example.airfrycalc.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "airfry_timer"
        private const val NOTIF_STEP_ID = 1001
        private const val NOTIF_DONE_ID = 1002

        fun createChannel(context: Context) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "טיימר אייר פריי",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "התראות על זמן הוספת מרכיבים"
                enableVibration(true)
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    fun sendAddIngredientAlert(ingredientName: String) {
        if (!hasPermission()) return
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("הוסף מרכיב עכשיו!")
            .setContentText("זמן להוסיף: $ingredientName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIF_STEP_ID, notif)
    }

    fun sendDoneAlert() {
        if (!hasPermission()) return
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("הבישול הסתיים!")
            .setContentText("האוכל מוכן. תהנה!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIF_DONE_ID, notif)
    }

    private fun hasPermission(): Boolean =
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
}
