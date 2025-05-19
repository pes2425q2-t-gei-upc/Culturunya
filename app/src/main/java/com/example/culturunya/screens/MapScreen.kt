package com.example.culturunya.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Looper
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.culturunya.endpoints.events.Event
import com.example.culturunya.endpoints.events.EventViewModel
import com.example.culturunya.ui.theme.Purple40
import com.google.android.gms.location.*
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
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect


/**
 * Funció per obtenir l'última ubicació coneguda de l'usuari.
 * Utilitza el FusedLocationProviderClient i retorna l'ubicació o null si hi ha un error.
 */
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


/**
 * Funció per obrir Google Maps amb una ubicació específica.
 * Si Google Maps no està instal·lat, obre la ubicació en el navegador web.
 *
 * @param context Context de l'aplicació.
 * @param latitude Latitud de la ubicació a mostrar.
 * @param longitude Longitud de la ubicació a mostrar.
 * @param label Etiqueta o nom del lloc.
 */
fun openGoogleMaps(context: Context, latitude: Double, longitude: Double, label: String) {
    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")


    // Verificar si Google Maps està instal·lat
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        // Si Google Maps no està instal·lat, obrim en el navegador
        val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
        context.startActivity(browserIntent)
    }
}


/**
 * Component Composable que sol·licita permisos de localització a l'usuari.
 * Notifica el resultat mitjançant una funció de callback.
 *
 * @param onPermissionResult Funció que es crida amb el resultat de la sol·licitud de permisos.
 */
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


    // Si ja tenim el permís, notifiquem immediatament
    LaunchedEffect(permissionState.value) {
        if (permissionState.value) {
            onPermissionResult(true)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState.value = isGranted

        // Notifiquem el resultat, sigui quin sigui
        onPermissionResult(isGranted)
    }

    // Només llancem la sol·licitud si no tenim el permís
    LaunchedEffect(Unit) {
        if (!permissionState.value) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}


/**
 * Component principal que gestiona la pantalla del mapa d'esdeveniments.
 * Controla els permisos d'ubicació i mostra contingut diferent segons si els permisos estan concedits o no.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventMapScreen() {
    //variables relacionades amb el canvi d'idioma
    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }

    //verifiquem si ja disposem dels permisos d'ubicació
    var permissionChecked by remember { mutableStateOf(false) }  // Indica si s'ha verificat el permís
    var permissionGranted by remember { mutableStateOf(false) }  // Indica si el permís està concedit

    // Estat per controlar la visibilitat del banner d'advertència
    var showPermissionBanner by remember { mutableStateOf(true) }

    // Estat per controlar la visibilitat del diàleg de confirmació
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Launcher per a la sol·licitud de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionChecked = true
        permissionGranted = isGranted
    }


    // Si el permís no ha estat verificat, el sol·licitem
    if (!permissionChecked) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Purple40)
                Spacer(modifier = Modifier.height(16.dp))
                Text(getString(context, R.string.checkingPermissions, currentLocale), color = Color.Black)
            }
        }

        RequestLocationPermission { isGranted ->
            permissionChecked = true
            permissionGranted = isGranted
        }
    } else {

        // Un cop verificat el permís, mostrem el contingut apropiat
        if (permissionGranted) {
            MapContent(hasLocationPermission = true)
        } else {
            Column {

                /*
                Banner d'advertència amb dialog explicant els requisits d'ubicació
                i botó de tancament del banner
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
                            // Contingut del banner/prompt
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clickable { showPermissionDialog = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Ubicació",
                                    modifier = Modifier.padding(end = 8.dp),
                                    tint = Color(0xFF856404)
                                )
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = getString(context, R.string.bannerTitle, currentLocale),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF856404)
                                    )
                                    Text(
                                        text = getString(context, R.string.bannerContent, currentLocale),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF856404)
                                    )
                                }
                            }

                            // Botó de tancament (creu) a la cantonada superior dreta
                            IconButton(
                                onClick = { showPermissionBanner = false },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Tancar avís",
                                    tint = Color(0xFF856404)
                                )
                            }
                        }
                    }
                }

                // Mostrar el mapa amb ubicació predeterminada
                MapContent(hasLocationPermission = false)
            }
        }
    }


    // Diàleg informatiu sobre els permisos d'ubicació
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = {Text(getString(context, R.string.alertDialogTitle, currentLocale))},
            text = {
                Text(
                    text = getString(context, R.string.alertDialogContent, currentLocale),
                    textAlign = TextAlign.Center,
                    color = Color.Black
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
                            color = Color.White
                        )
                    }
                }
            }
        )
    }
}

/**
 * Component que mostra el mapa de Google amb marcadors per a esdeveiments.
 * Permet filtrar esdeveniments per mes i distància.
 *
 * @param hasLocationPermission Indica si l'aplicació té permís per utilitzar la ubicació de l'usuari.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapContent(hasLocationPermission: Boolean = true) {
    //variables relacionades amb el canvi d'idioma
    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }

    // Definir els recursos per als mesos
    val monthResources = listOf(
        R.string.january, R.string.february, R.string.march, R.string.april,
        R.string.may, R.string.june, R.string.july, R.string.august,
        R.string.september, R.string.october, R.string.november, R.string.december
    )

    // Client per obtenir la ubicació de l'usuari
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()

    // ViewModel per gestionar les dades dels esdeveniments
    val viewModel = remember { EventViewModel() }

    // Estats per filtrar esdeveniments
    var currentDate by remember { mutableStateOf(LocalDate.now()) }  // Data actual per filtrar
    var distanceKm by remember { mutableStateOf(10f) }  // Distància en km per filtrar

    // Col·leccions reactives d'esdeveniments filtrats i estats de càrrega/error
    val filteredEvents by viewModel.filteredEventsByDistanceAndDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Emmagatzemar l'ubicació actual de l'usuari amb estat observable
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    // Variable per controlar la freqüència d'actualització del cercle (en mil·lisegons)
    val circleUpdateInterval = 100L // Actualitzar cada 100ms

    // Variable per forçar la recomposició del cercle
    var circleUpdateTrigger by remember { mutableStateOf(0) }

    // Estat per emmagatzemar l'esdeveniment seleccionat
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    // Estat per controlar si es mostra la pantalla de detalls de l'esdeveniment
    var showEventDetails by remember { mutableStateOf(false) }

    // Variable per controlar si se está siguiendo al usuario
    var isFollowingUser by remember { mutableStateOf(true) }

    // Crear un objeto LocationCallback para recibir actualizaciones de ubicación
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    currentLocation = location

                    // Actualizar la posición de la cámara solo si estamos siguiendo al usuario
                    if (isFollowingUser) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        // Mantener el nivel de zoom actual cuando actualizamos la posición
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.position = CameraPosition.Builder()
                            .target(userLatLng)
                            .zoom(currentZoom) // Mantener el zoom que tenía el usuario
                            .build()
                    }
                }
            }
        }
    }

    // LaunchedEffect per actualitzar el cercle amb alta freqüència
    LaunchedEffect(Unit) {
        while (true) {
            delay(circleUpdateInterval)
            circleUpdateTrigger += 1  // Incrementar per forçar recomposició
        }
    }

    // Efecte que s'executa quan es carrega el component per primera vegada
    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            // Si tenim permís d'ubicació, obtenim la ubicació actual i configurem actualizacions

            // Primero obtenemos la ubicación inicial
            val location = getLastKnownLocation(context, fusedLocationClient)
            location?.let {
                currentLocation = it
                val userLatLng = LatLng(it.latitude, it.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)

                // Filtrem esdeveniments per data i ubicació
                val firstDayOfMonth = currentDate.withDayOfMonth(1).toString()
                val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).toString()
                viewModel.filterEventsByRangeAndDate(
                    firstDayOfMonth,
                    lastDayOfMonth,
                    Pair(it.longitude, it.latitude),
                    distanceKm.toInt()
                )
            }

            // Configuramos las actualizaciones de ubicación en tiempo real - Augmentem la freqüència per a més fluidesa
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMinUpdateIntervalMillis(500)  // Augmentem la freqüència a 500ms
                .setWaitForAccurateLocation(false)
                .build()

            // Iniciar las actualizaciones de ubicación
            if (hasLocationPermission) {
                // Double-check permission programmatically before requesting location updates
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            }
        } else {
            // Ubicació predeterminada - Centre de Barcelona (Plaça Catalunya)
            val defaultLocation = LatLng(41.3874, 2.1686)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 13f)

            val firstDayOfMonth = currentDate.withDayOfMonth(1).toString()
            val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).toString()

            // Usar la ubicació predeterminada per filtrar esdeveniments
            viewModel.filterEventsByRangeAndDate(
                firstDayOfMonth,
                lastDayOfMonth,
                Pair(2.1686, 41.3874), // lon, lat de Plaça Catalunya
                distanceKm.toInt()
            )
        }
    }

    // Efecte que s'executa per aturar les actualitzacions quan el component es desmunta
    DisposableEffect(Unit) {
        onDispose {
            // Detener las actualizaciones de ubicación cuando el componente se desmonta
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Efecte que s'executa quan canvia la data o la distància
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

            // Usar la ubicació predeterminada per filtrar esdeveniments
            viewModel.filterEventsByRangeAndDate(
                firstDayOfMonth,
                lastDayOfMonth,
                Pair(2.1686, 41.3874), // lon, lat de Plaza Catalunya
                distanceKm.toInt()
            )
        }
    }

    // Si es mostra la pantalla de detalls, mostrar EventInfo
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

            // Capçalera Mes i Any - permet navegar entre mesos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentDate = currentDate.minusMonths(1) }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Mes anterior", tint = Color.Black)
                }
                Text(
                    text = "${
                        getString(
                            context,
                            monthResources[currentDate.month.ordinal],
                            currentLocale
                        )
                    } ${currentDate.year}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                IconButton(onClick = { currentDate = currentDate.plusMonths(1) }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Mes següent", tint = Color.Black)
                }
            }

            // Google Map amb marcadors d'esdeveniments
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
                        // Deseleccionar en fer clic al mapa
                        selectedEvent = null
                        // Desactivar el seguimiento automático cuando el usuario interactúa con el mapa
                        isFollowingUser = false
                    },
                    onMapLongClick = {
                        // Reactivamos el seguimiento al usuario con un clic largo
                        if (hasLocationPermission && currentLocation != null) {
                            isFollowingUser = true
                            val userLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
                        }
                    }
                ) {
                    // Afegir el cercle transparent al voltant de la ubicació de l'usuari, que es recomposa amb alta freqüència
                    if (hasLocationPermission && currentLocation != null) {
                        // Utilitzem la variable trigger per forçar recomposició
                        val valorInutilizado = circleUpdateTrigger
                        val userLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                        Circle(
                            center = userLatLng,
                            radius = 75.0, // 150 metres de diàmetre (75 de radi)
                            strokeColor = Color.Blue.copy(alpha = 0.3f),
                            fillColor = Color.Blue.copy(alpha = 0.1f)
                        )
                    }

                    // Dibuixem tots els marcadors
                    filteredEvents.forEach { event ->
                        val position = LatLng(event.location.latitude, event.location.longitude)
                        val isSelected = selectedEvent == event

                        // Per a cada esdeveniment, dibuixem un marcador
                        Marker(
                            state = MarkerState(position = position),
                            title = event.name,
                            snippet = event.description,
                            onClick = {
                                // En fer clic, seleccionar aquest esdeveniment
                                selectedEvent = event
                                // Desactivamos el seguimiento automático cuando se selecciona un marcador
                                isFollowingUser = false
                                // retornar false perquè el sistema mostri l'InfoWindow
                                false
                            },
                            icon = BitmapDescriptorFactory.defaultMarker(
                                if (isSelected) BitmapDescriptorFactory.HUE_BLUE else BitmapDescriptorFactory.HUE_RED
                            )
                        )
                    }
                }

                // Indicador de càrrega
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

                // Missatge d'error - usant crida segura per a error que pot ser nul
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
                                text = error ?: getString(context, R.string.unknownError, currentLocale),
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Botón para reactivar el seguimiento automático
                if (!isFollowingUser && hasLocationPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp, end = 16.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        FloatingActionButton(
                            onClick = {
                                if (currentLocation != null) {
                                    isFollowingUser = true
                                    val userLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
                                }
                            },
                            containerColor = Purple40
                        ) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Centrar al meu lloc",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Slider de Distància per ajustar el radi de cerca
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row {
                    Text(
                        text = getString(context, R.string.distanceLabel, currentLocale),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Text(
                        text = distanceKm.toInt().toString(), // el número, sense traducció
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Text(
                        text = getString(context, R.string.kilometersLabel, currentLocale), // " km"
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }

                Slider(
                    value = distanceKm,
                    onValueChange = { distanceKm = it },
                    valueRange = 10f..100f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), // más claro
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTickColor = MaterialTheme.colorScheme.primary,
                        inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                )
            }

            // Botons per veure detalls i obrir en Google Maps (només visibles si hi ha un esdeveniment seleccionat)
            if (selectedEvent != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Botó per veure detalls de l'esdeveniment
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

                    // Botó per obrir en Google Maps
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
                                contentDescription = "Obrir en Maps",
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