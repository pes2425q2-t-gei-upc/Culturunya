package com.example.culturunya.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.culturunya.ui.theme.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pantalla() {
    Scaffold {
        //MainScreen()
    }
}

@Composable
fun MainScreen(navController: NavController) {
    // Variable d'estat que guarda quina "pantalla" està seleccionada
    var currentScreen by remember { mutableStateOf("Events") }

    // Distribuïm la pantalla en una columna:
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // HEADER (logo, títol, etc.)
        Text(
            text = "Culturunya",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        // CONTENIDOR PRINCIPAL
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (currentScreen) {
                "Events" -> EventsScreen()
                "Quiz" -> QuizScreen()
                "Leaderboard" -> LeaderboardScreen()
                "Settings" -> SettingsScreen()
            }
        }

        // FOOTER: 4 botons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { currentScreen = "Events" }) {
                Text("Events")
            }
            Button(onClick = { currentScreen = "Quiz" }) {
                Text("Quiz")
            }
            Button(onClick = { currentScreen = "Leaderboard" }) {
                Text("Leaderboard")
            }
            Button(onClick = { currentScreen = "Settings" }) {
                Text("Settings")
            }
        }
    }
}

@Composable
fun EventsScreen() {
    val events = listOf("Evento 1", "Evento 2", "Evento 3", "Evento 4")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        events.forEach { event ->
            EventBox(event)
        }
    }
}

@Composable
fun EventBox(event: String) {
    var expanded by remember { mutableStateOf(false) }  // Para controlar el estado del menú desplegable
    var showMenu by remember { mutableStateOf(false) }  // Para controlar si mostrar el menú

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Purple40, RoundedCornerShape(8.dp))
            .clickable { showMenu = true }  // Hacer clic en el evento para mostrar el menú
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        // Menú desplegable
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }  // Cerrar el menú cuando se toca fuera de él
        ) {
            DropdownMenuItem(
                text = { Text("Detalles del Evento") },
                onClick = {
                    showMenu = false  // Cerrar el menú
                    // Acción para ver los detalles del evento
                }
            )
            DropdownMenuItem(
                text = { Text("Guardar Evento") },
                onClick = {
                    showMenu = false  // Cerrar el menú
                    // Acción para guardar el Evento en el calendario personal
                }
            )

        }
    }
}


@Composable
fun QuizScreen() {
    Text("Aquesta serà la pantalla de Quiz")
}

@Composable
fun LeaderboardScreen() {
    Text("Aquesta serà la pantalla de Leaderboard")
}

@Composable
fun SettingsScreen() {
    Text("Aquesta serà la pantalla de Settings")
}

// Mantens la funció Greeting si la vols reutilitzar
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
