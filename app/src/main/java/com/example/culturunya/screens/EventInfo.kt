package com.example.culturunya.screens.events

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.culturunya.R
import com.example.culturunya.endpoints.events.Event
import com.example.culturunya.endpoints.ratings.RatingViewModel
import com.example.culturunya.endpoints.users.UserViewModel
import com.example.culturunya.screens.RatingListScreen

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = event.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        fontSize = 22.sp,  // Tamaño aumentado
                        fontWeight = FontWeight.Bold  // Texto más grueso
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)  // Icono más grande
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
                            fontSize = 18.sp,  // Tamaño aumentado
                            color = Color(0xFF7B1FA2)
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = event.categories.joinToString(", "),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp  // Tamaño aumentado
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

                // Descripción (mantenemos el tamaño original)
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
            iconRes?.let {  // "let" en minúsculas
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A),  // Corregido formato del color
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