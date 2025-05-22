package com.example.culturunya.notifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class CulturunyaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Canal para notificaciones diarias"
            enableLights(true)
            lightColor = android.graphics.Color.RED
            enableVibration(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        Log.d("NotificationChannel", "Canal de notificación creado: $NOTIFICATION_CHANNEL_ID")
    }
}