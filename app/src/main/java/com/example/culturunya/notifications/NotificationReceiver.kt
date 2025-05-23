package com.example.culturunya.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import java.util.*

/**
 * @class NotificationReceiver
 * @brief Clase que recibe las notificaciones y maneja su programación.
 *
 * Esta clase se encarga de recibir las notificaciones y programar la siguiente notificación.
 */
class NotificationReceiver : BroadcastReceiver() {

    /**
     * @brief Método llamado al recibir una notificación.
     *
     * Este método se encarga de manejar la recepción de la notificación y programar la siguiente.
     *
     * @param context Contexto de la aplicación.
     * @param intent Intención que desencadenó el evento.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("NotificationReceiver", "================================")
        Log.d("NotificationReceiver", "onReceive llamado a las: ${Date()}")
        Log.d("NotificationReceiver", "Acción: ${intent?.action}")

        val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("last_received_time", System.currentTimeMillis())
            .putString("last_received_action", intent?.action ?: "null")
            .apply()

        if (intent?.action == "com.example.culturunya.ALARM_TRIGGER" ||
            intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "Culturunya:NotificationWakeLock"
            )

            try {
                wakeLock.acquire(30*1000L)
                Log.d("NotificationReceiver", "WakeLock adquirido")

                val service = NotificationService(context)
                service.showNotification()
                Log.d("NotificationReceiver", "Notificación mostrada")

                scheduleNextNotification(context)
                Log.d("NotificationReceiver", "Siguiente notificación programada")

            } catch (e: Exception) {
                Log.e("NotificationReceiver", "Error en onReceive: ${e.message}", e)
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                    Log.d("NotificationReceiver", "WakeLock liberado")
                }
            }
        } else {
            Log.d("NotificationReceiver", "Acción no reconocida")
        }

        Log.d("NotificationReceiver", "================================")
    }
}