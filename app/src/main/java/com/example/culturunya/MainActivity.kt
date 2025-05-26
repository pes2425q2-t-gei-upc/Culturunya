package com.example.culturunya

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.culturunya.navigation.AppNavigation
import com.example.culturunya.notifications.scheduleNextNotification
import com.example.culturunya.ui.theme.CulturunyaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        try {
            scheduleNextNotification(this)
            val prefs = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
            prefs.edit().putLong("last_schedule_time", System.currentTimeMillis()).apply()

        } catch (e: Exception) {
            Log.e("MainActivity", "Error al programar notificación: ${e.message}", e)
        }

        setContent {
            CulturunyaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        AppNavigation()
                    }
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //Log.d("MainActivity", "Solicitando permiso POST_NOTIFICATIONS")
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                //Log.d("MainActivity", "Permiso POST_NOTIFICATIONS ya concedido")
            }
        } else {
            //Log.d("MainActivity", "No se requiere permiso POST_NOTIFICATIONS en esta versión de Android")
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                scheduleNextNotification(this)
            }
            else Log.d("MainActivity", "Permiso de notificaciones DENEGADO")
        }
}