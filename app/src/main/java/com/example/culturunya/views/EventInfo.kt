package com.example.culturunya.views

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.R
import com.example.culturunya.dataclasses.events.Event
import com.example.culturunya.viewmodels.RatingViewModel
import com.example.culturunya.viewmodels.UserViewModel
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.example.culturunya.Api
import retrofit2.HttpException
import androidx.compose.material.icons.filled.Check

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventInfo(
    event: Event,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val purpleGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF6A1B9A),
            Color(0xFF9C27B0),
            Color(0xFFBA68C8)
        )
    )

    val ratingViewModel: RatingViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = event.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6A1B9A)
                )
            )

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(Color(0xFFF3E5F5)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sección de imagen del evento
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(purpleGradient),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = getEventImageUrl(event.id),
                    contentDescription = "Event Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.logo_retallat),
                    placeholder = painterResource(R.drawable.logo_retallat)
                )
            }

            // Botón para añadir al calendario de Google
            GoogleCalendarButton(event)

            Spacer(modifier = Modifier.height(8.dp))

            // Botó per confirmar assistència
            AssistButton(event = event)


            // Contenido del evento
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Categorías
                if (event.categories.isNotEmpty()) {
                    Text(
                        text = "Categorías:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            color = Color(0xFF7B1FA2)
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = event.categories.joinToString(", "),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Ubicación
                InfoItem(
                    title = "Ubicación",
                    content = event.location.address,
                    iconRes = R.drawable.ic_location
                )

                // Fechas y horas
                Text(
                    text = "Fechas:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        color = Color(0xFF7B1FA2)
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(
                        title = "Inicio",
                        content = event.date_start.split("T")[0],  //fecha
                        iconRes = R.drawable.ic_calendar,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    InfoItem(
                        title = "Hora",
                        content = event.date_start.split("T")[1].take(5),  //hora (HH:MM)
                        iconRes = R.drawable.ic_time,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(
                        title = "Fin",
                        content = event.date_end.split("T")[0],
                        iconRes = R.drawable.ic_calendar,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    InfoItem(
                        title = "Hora",
                        content = event.date_end.split("T")[1].take(5),
                        iconRes = R.drawable.ic_time,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Precio
                InfoItem(
                    title = "Precio",
                    content = event.price,
                    iconRes = R.drawable.ic_price
                )

                // Descripción
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        color = Color(0xFF6A1B9A)
                    ),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Column {
                    RatingListScreen(
                        eventId = event.id.toLong(),
                        onRatingSelected = { rating ->
                            println("Selected rating: ${rating.id}")
                        },
                        ratingViewModel = ratingViewModel,
                        userViewModel = userViewModel
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoogleCalendarButton(event: Event) {
    val context = LocalContext.current
    val hasGoogleAccount = remember { CurrentSession.getGoogleToken().isNotEmpty() }

    Button(
        onClick = {
            if (hasGoogleAccount) {
                // Crear la URL para añadir evento a Google Calendar
                val startDate = formatForGoogleCalendar(event.date_start)
                val endDate = formatForGoogleCalendar(event.date_end)
                val encodedTitle = URLEncoder.encode(event.name, "UTF-8")
                val encodedLocation = URLEncoder.encode(event.location.address, "UTF-8")
                val encodedDetails = URLEncoder.encode(event.description, "UTF-8")

                val calendarUrl = "https://www.google.com/calendar/render?action=TEMPLATE" +
                        "&text=$encodedTitle" +
                        "&dates=$startDate/$endDate" +
                        "&details=$encodedDetails" +
                        "&location=$encodedLocation" +
                        "&sf=true&output=xml"

                // Abrir la URL en el navegador
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(calendarUrl)
                context.startActivity(intent)
            } else {
                // Mostrar mensaje de error si no hay cuenta de Google
                Toast.makeText(
                    context,
                    "Necesitas iniciar sesión con Google para usar esta función",
                    Toast.LENGTH_LONG
                ).show()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Añadir al calendario",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Añadir a Google Calendar",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatForGoogleCalendar(dateTime: String): String {
    // Convertir formato ISO 8601 a formato Google Calendar
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val dateTimeObj = LocalDateTime.parse(dateTime, formatter)
    return dateTimeObj.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
}

@Composable
private fun InfoItem(
    title: String,
    content: String,
    @DrawableRes iconRes: Int? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 16.sp,
                color = Color(0xFF7B1FA2).copy(alpha = 0.8f)
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp
                ),
                color = Color.Black
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
@Composable
fun AssistButton(event: Event) {
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    Button(
        onClick = {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED

            if (!permissionGranted) {
                dialogMessage = com.example.culturunya.views.functions.getString(
                    context,
                    R.string.localizationPermissionsDenied,
                    currentLocale
                )
                showDialog = true
                return@Button
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val eventLocation = Location("").apply {
                        latitude = event.location.latitude
                        longitude = event.location.longitude
                    }

                    val distance = location.distanceTo(eventLocation)

                    if (distance > 50) {
                        dialogMessage = com.example.culturunya.views.functions.getString(
                            context,
                            R.string.wrongPosition,
                            currentLocale
                        )
                        showDialog = true
                        return@addOnSuccessListener
                    }

                    val now = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ISO_DATE_TIME
                    val start = LocalDateTime.parse(event.date_start, formatter)
                    val end = LocalDateTime.parse(event.date_end, formatter)

                    /*
                    if (now.isBefore(start) || now.isAfter(end)) {
                        dialogMessage =
                            com.example.culturunya.views.functions.getString(context, R.string.wrongTime, currentLocale)
                        showDialog = true
                        return@addOnSuccessListener
                    }
                     */

                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            // Crida a l'endpoint per SUMAR punts en el BACKEND
                            val response = Api.instance.getPointsEvent(
                                eventId = event.id, // event.id ja està en format String
                                token = CurrentSession.getAuthHeader() // La crida retorna "Token {token}"
                            )

                            if (!response.isSuccessful) {
                                println("Error Body: ${response.errorBody()?.string()}")
                            }

                            dialogMessage = when (response.code()) {
                                200 -> com.example.culturunya.views.functions.getString(
                                    context,
                                    R.string.pointsAddedCorrectly,
                                    currentLocale
                                )
                                403 -> com.example.culturunya.views.functions.getString(
                                    context,
                                    R.string.pointsAlreadyAdded,
                                    currentLocale
                                )
                                else -> com.example.culturunya.views.functions.getString(
                                    context,
                                    R.string.pointsAddedCorrectly,
                                    currentLocale
                                )
                            }
                        } catch (e: HttpException) {
                            dialogMessage = "Error HTTP: ${e.code()} - ${e.message()}"
                        } catch (e: Exception) {
                            dialogMessage = "Error: ${e.localizedMessage}"
                        }
                        showDialog = true
                    }
                } else {
                    dialogMessage = com.example.culturunya.views.functions.getString(
                        context,
                        R.string.localizationEerror,
                        currentLocale
                    )
                    showDialog = true
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Confirmar assistència",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = com.example.culturunya.views.functions.getString(
                    context,
                    R.string.assistanceConfirmation,
                    currentLocale
                ),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(
                com.example.culturunya.views.functions.getString(
                    context,
                    R.string.assistance,
                    currentLocale
                )
            ) },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}