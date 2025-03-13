package com.example.culturunya.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.culturunya.EventsScreen
import com.example.culturunya.LeaderboardScreen
import com.example.culturunya.QuizScreen
import com.example.culturunya.SettingsScreen

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
                .weight(1f) // la caixa ocupa tot l'espai disponible fins al footer
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Aquí mostrem el contingut segons la pantalla seleccionada
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