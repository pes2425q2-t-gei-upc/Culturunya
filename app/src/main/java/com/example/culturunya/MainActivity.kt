package com.example.culturunya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.culturunya.ui.theme.CulturunyaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CulturunyaTheme {
                // Arrel de la nostra UI
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
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

@Composable
fun EventsScreen() {
    Text("Aquesta serà la pantalla d'Events")
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CulturunyaTheme {
        MainScreen()
    }
}