package com.example.culturunya.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await
//-----
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

// Función para obtener la última localización conocida
@SuppressLint("MissingPermission")
suspend fun getLastKnownLocation(
    context: Context,
    fusedLocationProviderClient: FusedLocationProviderClient
): Location? {
    return try {
        fusedLocationProviderClient.lastLocation.await()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Composable para solicitar permiso de localización
@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val permissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted.value = isGranted
        if (isGranted) {
            onPermissionGranted()
        }
    }

    LaunchedEffect(true) {
        if (!permissionGranted.value) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            onPermissionGranted()
        }
    }
}

// Pantalla que gestiona el mapa y permisos
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventMapScreen() {
    var permissionGranted by remember { mutableStateOf(false) }

    RequestLocationPermission {
        permissionGranted = true
    }

    if (permissionGranted) {
        MapContent()
    } else {
        Text(text = "Esperando permisos de localización...")
    }
}

// Contenido del Mapa
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapContent() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()

    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var distanceKm by remember { mutableStateOf(10f) } // Distància seleccionada

    LaunchedEffect(Unit) {
        val location = getLastKnownLocation(context, fusedLocationClient)
        location?.let {
            val userLatLng = LatLng(it.latitude, it.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header Mes i Any
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentDate = currentDate.minusMonths(1)
            }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Mes anterior")
            }
            Text(
                text = "${currentDate.month.getDisplayName(TextStyle.FULL, Locale("es"))
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() }} ${currentDate.year}",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = {
                currentDate = currentDate.plusMonths(1)
            }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Mes següent")
            }
        }

        // Google Map
        Box(
            modifier = Modifier
                .weight(1f) // Que ocupi tot l'espai disponible entre header i slider
                .fillMaxWidth()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true)
            )
        }

        // Slider de Distància
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Distancia: ${distanceKm.toInt()} km",
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                value = distanceKm,
                onValueChange = { distanceKm = it },
                valueRange = 10f..100f,
                steps = 9, // 10, 20, ..., 100 → 9 pasos intermedios
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
