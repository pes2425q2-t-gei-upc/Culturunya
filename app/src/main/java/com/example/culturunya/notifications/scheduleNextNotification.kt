package com.example.culturunya.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.culturunya.MainActivity
import java.util.*

fun scheduleNextNotification(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Usa una acción específica para tu aplicación
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        action = "com.example.culturunya.ALARM_TRIGGER"
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        add(Calendar.MINUTE, 1)
    }

    Log.d("AlarmDebug", "Alarma programada para: ${calendar.time}")

    // Verificar y solicitar permiso para alarmas exactas en Android 12+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            // Redirigir al usuario a configuración para habilitar alarmas exactas
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Log.d("AlarmDebug", "Solicitando permiso para alarmas exactas")
            return
        }

        // Usar setAlarmClock en lugar de setExactAndAllowWhileIdle para mayor prioridad
        val showTime = System.currentTimeMillis() + 60000 // 1 minuto después
        val pendingShowIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(showTime, pendingShowIntent),
            pendingIntent
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    Log.d("AlarmDebug", "Alarma configurada exitosamente")
}