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


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Composable function to display event information.
 * @param event The event to be displayed.
 * @param onBack Callback function to handle back navigation.
 */
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
/**
 * Composable function to create a button for adding an event to Google Calendar.
 * @param event The event to be added to the calendar.
 */
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
/**
 * Function to format a date string for Google Calendar.
 * @param dateTime The date string in ISO 8601 format.
 * @return The formatted date string for Google Calendar.
 */
private fun formatForGoogleCalendar(dateTime: String): String {
    // Convertir formato ISO 8601 a formato Google Calendar
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val dateTimeObj = LocalDateTime.parse(dateTime, formatter)
    return dateTimeObj.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
}

@Composable
/**
 * Composable function to display an information item with an icon.
 * @param title The title of the item.
 * @param content The content of the item.
 * @param iconRes The resource ID of the icon (optional).
 * @param modifier Modifier for styling.
 */
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