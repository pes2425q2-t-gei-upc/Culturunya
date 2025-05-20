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
        set(Calendar.MINUTE, 10)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        if (before(Calendar.getInstance())) {
            add(Calendar.DATE, 1)
        }
    }

    val triggerTime = calendar.timeInMillis
    Log.d("AlarmDebug", "Programando alarma inexacta para: ${Date(triggerTime)}")

    try {
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    } catch (e: Exception) {
        Log.e("AlarmDebug", "Error al programar alarma: ${e.message}", e)
    }
}