package com.example.culturunya.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.culturunya.endpoints.events.Event
import com.example.culturunya.endpoints.events.EventViewModel
import com.example.culturunya.screens.events.EventInfo
import com.example.culturunya.ui.theme.Purple40
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
//imports relacionados con el cambio de idioma
import androidx.compose.ui.platform.LocalContext
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.R


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

// Función para abrir Google Maps con una ubicación específica
// si el dispositivo no dispone de google maps, se abre el navegador con una URL similar
fun openGoogleMaps(context: Context, latitude: Double, longitude: Double, label: String) {
    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")

    // Verificar si Google Maps está instalado
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        // Si Google Maps no está instalado, abrimos en el navegador
        val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
        context.startActivity(browserIntent)
    }
}

// Composable para solicitar permiso de localización
@Composable
fun RequestLocationPermission(
    onPermissionResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val permissionState = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Si ya tenemos el permiso, notificamos inmediatamente
    LaunchedEffect(permissionState.value) {
        if (permissionState.value) {
            onPermissionResult(true)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState.value = isGranted
        // Notificamos el resultado, sea cual sea
        onPermissionResult(isGranted)
    }

    // Solo lanzamos la solicitud si no tenemos el permiso
    LaunchedEffect(Unit) {
        if (!permissionState.value) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

// Pantalla que gestiona el mapa y permisos
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventMapScreen() {
    //variables relacionadas con el cambio de idioma
    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }

    //verificamos si ya disponemos de los permisos de ubicación
    var permissionChecked by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    // Estado para controlar la visibilidad del banner de advertencia
    var showPermissionBanner by remember { mutableStateOf(true) }

    // Estado para controlar la visibilidad del diálogo de confirmación
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Launcher para la solicitud de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionChecked = true
        permissionGranted = isGranted
    }

    // Si el permiso no ha sido verificado, lo solicitamos
    if (!permissionChecked) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Purple40)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Verificando permisos de localización...")
            }
        }

        RequestLocationPermission { isGranted ->
            permissionChecked = true
            permissionGranted = isGranted
        }
    } else {
        // Una vez verificado el permiso, mostramos el contenido apropiado
        if (permissionGranted) {
            MapContent(hasLocationPermission = true)
        } else {
            Column {
                /*
                Banner de advertencia con dialog explicando los requisitos de
                ubicación y botón de cierre del banner
                 */
                if (showPermissionBanner) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFFFF3CD),
                            contentColor = Color(0xFF856404)
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Contenido del banner
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clickable { showPermissionDialog = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Ubicación",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = getString(context, R.string.bannerTitle, currentLocale),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = getString(context, R.string.bannerContent, currentLocale),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            // Botón de cierre (cruz) en la esquina superior derecha
                            IconButton(
                                onClick = { showPermissionBanner = false },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cerrar aviso",
                                    tint = Color(0xFF856404)
                                )
                            }
                        }
                    }
                }

                // Mostrar el mapa con ubicación predeterminada
                MapContent(hasLocationPermission = false)
            }
        }
    }

    // Diálogo de confirmación para otorgar permisos
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = {getString(context, R.string.alertDialogTitle, currentLocale)},
            text = {
                Text(
                    text = getString(context, R.string.alertDialogContent, currentLocale),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            showPermissionDialog = false
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                    ) {
                        Text(
                            text = getString(context, R.string.accept, currentLocale),
                        )
                    }
                }
            }
        )
    }
}

// Contenido del Mapa
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapContent(hasLocationPermission: Boolean = true) {
    //variables relacionadas con el cambio de idioma
    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }

    // Definir els recursos per als mesos
    val monthResources = listOf(
        R.string.january, R.string.february, R.string.march, R.string.april,
        R.string.may, R.string.june, R.string.july, R.string.august,
        R.string.september, R.string.october, R.string.november, R.string.december
    )

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()

    val viewModel = remember { EventViewModel() }

    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var distanceKm by remember { mutableStateOf(10f) }

    val filteredEvents by viewModel.filteredEventsByDistanceAndDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var currentLocation by remember { mutableStateOf<Location?>(null) }

    // Estado para almacenar el evento seleccionado
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    // Estado para controlar si se muestra la pantalla de detalles del evento
    var showEventDetails by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            val location = getLastKnownLocation(context, fusedLocationClient)
            location?.let {
                currentLocation = it
                val userLatLng = LatLng(it.latitude, it.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)

                val firstDayOfMonth = currentDate.withDayOfMonth(1).toString()
                val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).toString()
                viewModel.filterEventsByRangeAndDate(
                    firstDayOfMonth,
                    lastDayOfMonth,
                    Pair(it.longitude, it.latitude),
                    distanceKm.toInt()
                )
            }
        } else {
            // Ubicación predeterminada - Centro de Barcelona (Plaza Catalunya)
            val defaultLocation = LatLng(41.3874, 2.1686)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 13f)

            val firstDayOfMonth = currentDate.withDayOfMonth(1).toString()
            val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).toString()
            // Usar la ubicación predeterminada para filtrar eventos
            viewModel.filterEventsByRangeAndDate(
                firstDayOfMonth,
                lastDayOfMonth,
                Pair(2.1686, 41.3874), // lon, lat de Plaza Catalunya
                distanceKm.toInt()
            )
        }
    }

    LaunchedEffect(currentDate, distanceKm) {
        if (hasLocationPermission && currentLocation != null) {
            val location = currentLocation!!
            val firstDayOfMonth = currentDate.withDayOfMonth(1).toString()
            val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).toString()
            viewModel.filterEventsByRangeAndDate(
                firstDayOfMonth,
                lastDayOfMonth,
                Pair(location.longitude, location.latitude),
                distanceKm.toInt()
            )
        } else {
            val firstDayOfMonth = currentDate.withDayOfMonth(1).toString()
            val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).toString()
            // Usar la ubicación predeterminada para filtrar eventos
            viewModel.filterEventsByRangeAndDate(
                firstDayOfMonth,
                lastDayOfMonth,
                Pair(2.1686, 41.3874), // lon, lat de Plaza Catalunya
                distanceKm.toInt()
            )
        }
    }

    // Si se muestra la pantalla de detalles, mostrar EventInfo
    if (showEventDetails && selectedEvent != null) {
        EventInfo(
            event = selectedEvent!!,
            onBack = { showEventDetails = false }
        )
    } else {
        // Pantalla de mapa normal
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Mes y Año
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentDate = currentDate.minusMonths(1) }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Mes anterior")
                }
                Text(
                    text = "${getString(context, monthResources[currentDate.month.ordinal], currentLocale)} ${currentDate.year}",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { currentDate = currentDate.plusMonths(1) }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Mes siguiente")
                }
            }

            // Google Map
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                    onMapClick = {
                        // Deseleccionar al hacer clic en el mapa
                        selectedEvent = null
                    }
                ) {
                    // Dibujamos todos los marcadores
                    filteredEvents.forEach { event ->
                        val position = LatLng(event.location.latitude, event.location.longitude)
                        val isSelected = selectedEvent == event

                        // Para cada evento, dibujamos un marcador
                        Marker(
                            state = MarkerState(position = position),
                            title = event.name,
                            snippet = event.description,
                            onClick = {
                                // Al hacer clic, seleccionar este evento
                                selectedEvent = event
                                // Importante: devolver false para que el sistema muestre el InfoWindow
                                false
                            },
                            icon = BitmapDescriptorFactory.defaultMarker(
                                if (isSelected) BitmapDescriptorFactory.HUE_BLUE else BitmapDescriptorFactory.HUE_RED
                            )
                        )
                    }
                }

                // Indicador de carga
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Purple40)
                    }
                }

                // Mensaje de error - usando llamada segura para error que puede ser nulo
                if (error?.isNotEmpty() == true) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDAD5))
                        ) {
                            Text(
                                text = error ?: "Error desconocido",
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Slider de Distancia
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                Row {
                    Text(
                        text = getString(context, R.string.distanceLabel, currentLocale),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = distanceKm.toInt().toString(), // el número, sin traducción
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = getString(context, R.string.kilometersLabel, currentLocale), // " km"
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Slider(
                    value = distanceKm,
                    onValueChange = { distanceKm = it },
                    valueRange = 10f..100f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botones para ver detalles y abrir en Google Maps (solo visibles si hay un evento seleccionado)
            if (selectedEvent != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón para ver detalles del evento
                    Button(
                        onClick = { showEventDetails = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                    ) {
                        Text(
                            text = getString(context, R.string.eventDetails, currentLocale),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Botón para abrir en Google Maps
                    Button(
                        onClick = {
                            selectedEvent?.let {
                                openGoogleMaps(
                                    context,
                                    it.location.latitude,
                                    it.location.longitude,
                                    it.name
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)) // Color de Google
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = "Abrir en Maps",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = getString(context, R.string.mapsButton, currentLocale),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}