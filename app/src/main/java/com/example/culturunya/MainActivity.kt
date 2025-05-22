package com.example.culturunya

import SessionManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturunya.navigation.AppNavigation
import com.example.culturunya.notifications.scheduleNextNotification
import com.example.culturunya.ui.theme.CulturunyaTheme
import com.example.culturunya.ui.theme.Morat
import com.example.culturunya.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notification setup
        checkNotificationPermission()
        try {
            scheduleNextNotification(this)
            val prefs = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
            prefs.edit().putLong("last_schedule_time", System.currentTimeMillis()).apply()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al programar notificación: ${e.message}", e)
        }

        // Compose content
        setContent {
            CulturunyaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppContent()
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
                Log.d("MainActivity", "Solicitando permiso POST_NOTIFICATIONS")
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d("MainActivity", "Permiso POST_NOTIFICATIONS ya concedido")
            }
        } else {
            Log.d("MainActivity", "No se requiere permiso POST_NOTIFICATIONS en esta versión de Android")
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                scheduleNextNotification(this)
            } else Log.d("MainActivity", "Permiso de notificaciones DENEGADO")
        }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAppContent() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authViewModel: AuthViewModel = viewModel()

    var isLoading by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(false) }

    // Load saved session
    LaunchedEffect(Unit) {
        authViewModel.loadSession(sessionManager)
    }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        isLoading = authState.isLoading
        isLoggedIn = authState.isAuthenticated
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_retallat),
                contentDescription = "Logo retallat",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        }
    } else {
        AppNavigation(
            isLoggedIn = isLoggedIn,
            onLogout = { authViewModel.logout(sessionManager) }
        )
    }
}
