package com.example.culturunya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.culturunya.ui.theme.CulturunyaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CulturunyaTheme {
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
    var currentScreen by remember { mutableStateOf("Events") }

    Column(modifier = Modifier.fillMaxSize()) {
        // HEADER
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

        // FOOTER amb 4 botons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            // Distribueix uniformement els botons al llarg de l'espai horitzontal
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomButtonItem(
                screenName = "Events",
                iconRes = R.drawable.ic_events,
                isSelected = currentScreen == "Events"
            ) { currentScreen = "Events" }

            BottomButtonItem(
                screenName = "Quiz",
                iconRes = R.drawable.ic_quiz,
                isSelected = currentScreen == "Quiz"
            ) { currentScreen = "Quiz" }

            BottomButtonItem(
                screenName = "Leaderboard",
                iconRes = R.drawable.ic_leaderboard,
                isSelected = currentScreen == "Leaderboard"
            ) { currentScreen = "Leaderboard" }

            BottomButtonItem(
                screenName = "Settings",
                iconRes = R.drawable.ic_settings,
                isSelected = currentScreen == "Settings"
            ) { currentScreen = "Settings" }
        }
    }
}

@Composable
fun BottomButtonItem(
    screenName: String,
    @DrawableRes iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val buttonColors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    // Ajusta l'ample mínim o màxim del botó, si cal, per assegurar que hi càpiguen tots.
    // Aquí fem servir un Button sense cap amplada fixa; si el text és molt llarg,
    // potser hauràs d'ajustar-ho a conveniència.
    Button(
        onClick = onClick,
        colors = buttonColors,
        modifier = Modifier
            .wrapContentSize() // El botó creixerà només el necessari
    ) {
        // Col·loquem la icona a sobre del text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = screenName
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = screenName)
        }
    }
}

@Composable
fun EventsScreen() {
    Text(text = "Aquesta serà la pantalla d'Events")
}

@Composable
fun QuizScreen() {
    Text(text = "Aquesta serà la pantalla de Quiz")
}

@Composable
fun LeaderboardScreen() {
    Text(text = "Aquesta serà la pantalla de Leaderboard")
}

@Composable
fun SettingsScreen() {
    Text(text = "Aquesta serà la pantalla de Settings")
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    CulturunyaTheme {
        MainScreen()
    }
}
