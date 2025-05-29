package com.example.culturunya.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.culturunya.MainActivity
import com.example.culturunya.R
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.views.functions.getString

const val NOTIFICATION_CHANNEL_ID = "ch-1"
const val NOTIFICATION_CHANNEL_NAME = "Daily notification"
const val NOTIFICATION_ID = 100
const val REQUEST_CODE = 200

class NotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val myIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        myIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    fun showNotification() {
        CurrentSession.getInstance()
        val currentLocale = CurrentSession.language
        val notification =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Culturunya")
                .setSmallIcon(R.drawable.logo_sense_fons)
                .setContentText(getString(context, R.string.dailyNotification, currentLocale))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVibrate(longArrayOf(0, 250, 250, 250))
                .setLights(android.graphics.Color.RED, 1000, 300)
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .build()

        Log.d("NotificationDebug", "Mostrando notificación con ID: $NOTIFICATION_ID")

        try {
            notificationManager.notify(NOTIFICATION_ID, notification)
            Log.d("NotificationDebug", "Notificación enviada correctamente")
        } catch (e: Exception) {
            Log.e("NotificationDebug", "Error al mostrar notificación", e)
        }
    }
}