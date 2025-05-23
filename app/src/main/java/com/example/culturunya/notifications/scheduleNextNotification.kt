package com.example.culturunya.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.culturunya.MainActivity
import java.util.*

/**
 * @class NotificationScheduler
 * @brief Clase que maneja la programación de notificaciones.
 *
 * Esta clase se encarga de programar las notificaciones diarias en la aplicación.
 *
 * @param context Contexto de la aplicación.
 */
fun scheduleNextNotification(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        action = "com.example.culturunya.ALARM_TRIGGER"
        putExtra("timestamp", System.currentTimeMillis())
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 16)
        set(Calendar.MINUTE, 15)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        if (before(Calendar.getInstance())) {
            add(Calendar.DATE, 1)
        }
    }

    val triggerTime = calendar.timeInMillis
    Log.d("NotificationDebug", "Programando alarma exacta para: ${Date(triggerTime)}")

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )

            val tomorrow = Calendar.getInstance().apply {
                timeInMillis = triggerTime
                add(Calendar.DATE, 1)
            }

            val tomorrowIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE + 1,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                tomorrow.timeInMillis,
                tomorrowIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }

        val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putLong("next_notification_time", triggerTime)
            .putString("next_notification_date", Date(triggerTime).toString())
            .apply()

    } catch (e: Exception) {
        Log.e("NotificationDebug", "Error al programar alarma: ${e.message}", e)
    }
}