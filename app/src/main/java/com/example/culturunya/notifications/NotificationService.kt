package com.example.culturunya.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.culturunya.MainActivity
import com.example.culturunya.R

const val NOTIFICATION_CHANNEL_ID = "ch-1"
const val NOTIFICATION_CHANNEL_NAME = "Test Notification"
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
        val notification =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_retallat)
                .setContentTitle("Culturunya")
                .setContentText("Recuerda hacer tus test diarios culturales. Si no los haces, te deportaremos a Italia :)")
                .setPriority(NotificationCompat.PRIORITY_MAX) // Usar MAX en lugar de HIGH
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_ALARM) // Categoría de alarma para dar prioridad
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Forzar vibración, sonido e iluminación LED
                .setVibrate(longArrayOf(0, 250, 250, 250))
                .setLights(android.graphics.Color.RED, 1000, 300)
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .build()

        // Adicionalmente, logueamos cuando se muestra la notificación
        Log.d("NotificationDebug", "Mostrando notificación con ID: $NOTIFICATION_ID")

        try {
            notificationManager.notify(NOTIFICATION_ID, notification)
            Log.d("NotificationDebug", "Notificación enviada correctamente")
        } catch (e: Exception) {
            Log.e("NotificationDebug", "Error al mostrar notificación", e)
        }
    }
}