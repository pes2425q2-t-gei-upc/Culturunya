package com.example.culturunya.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import java.util.*

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("NotificationReceiver", "Alarma recibida a las: ${Date()} con acción: ${intent?.action}")

        // Verificar acción específica
        if (intent?.action == "com.example.culturunya.ALARM_TRIGGER" ||
            intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            // Utilizando un WakeLock para asegurar que el dispositivo no se duerma
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "Culturunya:NotificationWakeLock"
            )

            try {
                wakeLock.acquire(10*60*1000L /*10 minutos*/)

                val service = NotificationService(context)
                service.showNotification()

                // Programar la siguiente notificación
                scheduleNextNotification(context)
            } catch (e: Exception) {
                Log.e("NotificationReceiver", "Error al mostrar notificación", e)
            } finally {
                if (wakeLock.isHeld) wakeLock.release()
            }
        }
    }
}