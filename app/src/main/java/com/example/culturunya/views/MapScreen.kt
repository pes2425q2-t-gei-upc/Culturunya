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
//imports relacionados con el cambio de idioma
import com.example.culturunya.CurrentSession
import com.example.culturunya.R


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
                Text(getString(context, R.string.checkingPermissions, currentLocale))
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
                            // Contingut del banner
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clickable { showPermissionDialog = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Ubicació",
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


    // Emmagatzemar l'ubicació actual de l'usuari
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    // Estat per emmagatzemar l'esdeveniment seleccionat
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    // Estat per controlar si es mostra la pantalla de detalls de l'esdeveniment
    var showEventDetails by remember { mutableStateOf(false) }

    // Efecte que s'executa quan es carrega el component per primera vegada
    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            // Si tenim permís d'ubicació, obtenim la ubicació actual

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
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Mes anterior")
                }
                Text(

                    text = "${
                        getString(
                            context,
                            monthResources[currentDate.month.ordinal],
                            currentLocale
                        )
                    } ${currentDate.year}",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { currentDate = currentDate.plusMonths(1) }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Mes següent")
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
                    }
                ) {
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
                                // Important: retornar false perquè el sistema mostri l'InfoWindow
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
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = distanceKm.toInt().toString(), // el número, sense traducció
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