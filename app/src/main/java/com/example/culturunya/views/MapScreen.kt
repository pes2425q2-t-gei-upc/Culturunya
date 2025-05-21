package com.example.culturunya.views

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
import com.example.culturunya.dataclasses.events.Event
import com.example.culturunya.viewmodels.EventViewModel
import com.example.culturunya.ui.theme.Purple40
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import com.example.culturunya.CurrentSession
import com.example.culturunya.R
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import androidx.core.app.ActivityCompat
import kotlin.math.abs
import com.example.culturunya.dataclasses.chargingPoints.ChargingPointItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.culturunya.ChargingApi


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
            title = {getString(context, R.string.alertDialogTitle, currentLocale)},
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

    //obtenir la ubicació de l'usuari
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()

    // ViewModel per gestionar les dades dels esdeveniments
    val viewModel = remember { EventViewModel() }

    // Estats per filtrar esdeveniments
    var currentDate by remember { mutableStateOf(LocalDate.now()) }  // Data actual per filtrar
    var distanceKm by remember { mutableStateOf(10f) }  // Distància en km per filtrar

    // Col·leccions d'esdeveniments filtrats i estats de càrrega/error
    val filteredEvents by viewModel.filteredEventsByDistanceAndDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Emmagatzemar l'ubicació actual de l'usuari
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    // Estat per emmagatzemar l'esdeveniment seleccionat
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    // Estat per controlar si es mostra la pantalla de detalls de l'esdeveniment
    var showEventDetails by remember { mutableStateOf(false) }

    // Estat per emmagatzemar el punt de càrrega seleccionat
    var selectedChargingPoint by remember { mutableStateOf<ChargingPointItem?>(null) }

    var isCameraInitialized by remember { mutableStateOf(false) }

    var lastFilterLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var lastFilterDate by remember { mutableStateOf<LocalDate?>(null) }

    //variables relacionades amb els punts de carrega del servei extern
    var chargingPoints by remember { mutableStateOf<List<ChargingPointItem>>(emptyList()) }
    var lastChargingLocation by remember { mutableStateOf<Location?>(null) }

    // Efecte que s'executa quan es carrega el component per primera vegada
    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).apply {
                setMinUpdateIntervalMillis(2000)
            }.build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val newLocation = locationResult.lastLocation
                    if (newLocation != null) {
                        currentLocation = newLocation

                        if (isFarEnough(lastChargingLocation, newLocation)) {
                            lastChargingLocation = newLocation

                            // Crida a API de estacions de carrega
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val chargingResult = ChargingApi.instance.getNearestChargingPoints(
                                        newLocation.latitude,
                                        newLocation.longitude
                                    )
                                    withContext(Dispatchers.Main) {
                                        chargingPoints = chargingResult
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        // Només centra la càmera un cop
                        if (!isCameraInitialized) {
                            val userLatLng = LatLng(newLocation.latitude, newLocation.longitude)
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
                            isCameraInitialized = true
                        }

                        val lat = newLocation.latitude
                        val lon = newLocation.longitude

                        // Només filtra si la ubicació o la data han canviat significativament
                        val sameLocation = lastFilterLocation?.let {
                            abs(it.first - lon) < 0.0001 && abs(it.second - lat) < 0.0001
                        } ?: false

                        val sameDate = lastFilterDate == currentDate

                        if (!sameLocation || !sameDate) {
                            lastFilterLocation = Pair(lon, lat)
                            lastFilterDate = currentDate

                            val firstDayOfMonth = currentDate.withDayOfMonth(1).toString()
                            val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).toString()

                            viewModel.filterEventsByRangeAndDate(
                                firstDayOfMonth,
                                lastDayOfMonth,
                                Pair(lon, lat),
                                distanceKm.toInt()
                            )
                        }
                    }
                }
            }

            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }

        } else {
            // Ubicació per defecte (Barcelona plaça cat)
            val defaultLocation = LatLng(41.3874, 2.1686)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 13f)

            val firstDayOfMonth = currentDate.withDayOfMonth(1).toString()
            val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).toString()

            viewModel.filterEventsByRangeAndDate(
                firstDayOfMonth,
                lastDayOfMonth,
                Pair(2.1686, 41.3874),
                distanceKm.toInt()
            )
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
                Pair(2.1686, 41.3874), // lon, lat de Plaça Cat
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
                        selectedChargingPoint = null
                    }
                ) {
                    // Afegir un cercle transparent de 75 metres al voltant de la ubicació de l'usuari
                    if (hasLocationPermission && currentLocation != null) {
                        val userLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                        Circle(
                            center = userLatLng,
                            radius = 75.0, // 75 metres
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
                                // En fer clic, seleccionar aquest esdeveniment i netejar qualsevol punt de càrrega seleccionat
                                selectedEvent = event
                                selectedChargingPoint = null
                                // retornar false perquè el sistema mostri l'InfoWindow
                                false
                            },
                            icon = BitmapDescriptorFactory.defaultMarker(
                                if (isSelected) BitmapDescriptorFactory.HUE_BLUE else BitmapDescriptorFactory.HUE_RED
                            )
                        )
                    }

                    // Marcadors de punts de càrrega
                    chargingPoints.forEach { point ->
                        val station = point.estacio_carrega
                        val position = LatLng(station.lat, station.lng)
                        val isSelected = selectedChargingPoint == point

                        Marker(
                            state = MarkerState(position = position),
                            title = getString(context, R.string.chargingPointLabel, currentLocale),
                            snippet = "${station.direccio} - ${station.potencia} kW",
                            onClick = {
                                // En fer clic, seleccionar aquest punt de càrrega i netejar qualsevol esdeveniment seleccionat
                                selectedChargingPoint = point
                                selectedEvent = null
                                false
                            },
                            icon = BitmapDescriptorFactory.defaultMarker(
                                if (isSelected) BitmapDescriptorFactory.HUE_CYAN else BitmapDescriptorFactory.HUE_GREEN
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
            }

            // Slider de Distància per ajustar el radi per el filtre de Km
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
                        text = distanceKm.toInt().toString(), // númerode Km, no té traducció
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
                        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), // més clar
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTickColor = MaterialTheme.colorScheme.primary,
                        inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                )
            }

            // Composable per mostrar els botons d'obertura a Google Maps
            GoogleMapsButton(
                selectedEvent = selectedEvent,
                selectedChargingPoint = selectedChargingPoint,
                showEventDetails = showEventDetails,
                onShowEventDetailsChange = { showEventDetails = it },
                context = context,
                currentLocale = currentLocale
            )
        }
    }
}

//Composable per gestionar els botons d'esdeveniments i punts de càrrega
@Composable
fun GoogleMapsButton(
    selectedEvent: Event?,
    selectedChargingPoint: ChargingPointItem?,
    showEventDetails: Boolean,
    onShowEventDetailsChange: (Boolean) -> Unit,
    context: Context,
    currentLocale: String
) {
    // Si hi ha un esdeveniment seleccionat
    if (selectedEvent != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botó per veure detalls de l'esdeveniment
            Button(
                onClick = { onShowEventDetailsChange(true) },
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
            MapOpenButton(
                onClick = {
                    openGoogleMaps(
                        context,
                        selectedEvent.location.latitude,
                        selectedEvent.location.longitude,
                        selectedEvent.name
                    )
                },
                text = getString(context, R.string.mapsButton, currentLocale),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )
        }
    }
    // Si hi ha un punt de càrrega seleccionat
    else if (selectedChargingPoint != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botó per obrir el punt de càrrega en Google Maps
            MapOpenButton(
                onClick = {
                    val station = selectedChargingPoint.estacio_carrega
                    openGoogleMaps(
                        context,
                        station.lat,
                        station.lng,
                        "${getString(context, R.string.chargingPointLabel, currentLocale)} ${station.direccio}"
                    )
                },
                text = getString(context, R.string.mapsButton, currentLocale),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

//Composable per al botó d'obrir a Google Maps
@Composable
fun MapOpenButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
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
                text = text,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

fun isFarEnough(oldLocation: Location?, newLocation: Location, thresholdMeters: Float = 250f): Boolean {
    if (oldLocation == null) return true
    return oldLocation.distanceTo(newLocation) > thresholdMeters
}