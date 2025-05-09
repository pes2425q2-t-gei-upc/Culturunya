package com.example.culturunya.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.culturunya.R
import com.example.culturunya.dataclasses.events.Event
import com.example.culturunya.CurrentSession
import com.example.culturunya.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun getEventImageUrl(eventId: String): String {
    // Assuming your base URL is something like "http://yourserver.com/media/"
    val baseUrl = "http://nattech.fib.upc.edu:40369/media/"
    println("${baseUrl}event_images/${eventId}.jpg")
    return "${baseUrl}event_images/${eventId}.jpg"
}

//Funcion provisional para formatear la fecha
@RequiresApi(Build.VERSION_CODES.O)
fun formatDateString(dateString: String): String {
    // Define the input format (how the date is stored in your data)
    val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
    // Define the output format (how you want to display it)
    val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm",
        java.util.Locale("es", "ES")
    )
    return try {
        // Parse the string into a LocalDateTime
        val dateTime = LocalDateTime.parse(dateString, inputFormatter)
        // Format the LocalDateTime into a string
        dateTime.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        // Handle parsing errors (e.g., invalid format)
        "Invalid Date"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventBox(
    event: Event,
    onEventClick: (Event) -> Unit
) {
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale by remember { mutableStateOf(CurrentSession.language) }
    var showMenu by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PurpleGrey80, RoundedCornerShape(8.dp))
            .clickable { showMenu = true }
    ) {
        Row {
            //Sustituir por foto
            AsyncImage(
                model = getEventImageUrl(event.id),
                contentDescription = "Event Image",
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
                    .background(com.example.culturunya.ui.theme.PurpleGrey80, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.logo_retallat),
                placeholder = painterResource(R.drawable.logo_retallat)
            )
            //

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = getString(context, R.string.fromPrice, currentLocale) + event.price+"€",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 10.sp
                    ),
                    color = Purple40,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatDateString(event.date_start) ,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 10.sp
                    ),
                    color = Purple40,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.location.city,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 10.sp
                    ),
                    color = Purple40,
                )

            }
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Detalles del Evento") },
                onClick = {
                    onEventClick(event)
                    showMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Guardar Evento") },
                onClick = { showMenu = false }
            )
        }
    }
}