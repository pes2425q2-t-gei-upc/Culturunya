package com.example.culturunya.views

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.ui.theme.*
import com.example.culturunya.viewmodels.EventViewModel
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavController, viewModel: EventViewModel, initialScreen: String) {
    // Estat per a la pantalla principal
    // Estat per als sub-botons d'Events (Map, Calendar, List)
    // Només s'usa si la pantalla principal seleccionada és "Events".
    var currentScreen by remember { mutableStateOf(initialScreen) }
    if (currentScreen != "Events" && currentScreen != "Quiz" && currentScreen != "Leaderboard" && currentScreen != "Settings") currentScreen = "Events"
    var currentEventsSubScreen by remember { mutableStateOf("Map") }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)) {
        // HEADER
        Text(
            text = "Culturunya",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        // SEGONA FILA (només visible si "Events" està seleccionat)
        if (currentScreen == "Events") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botó MAP
                TopButtonItem(
                    subScreenName = "Map",
                    iconRes = R.drawable.ic_map,
                    isSelected = (currentEventsSubScreen == "Map")
                ) {
                    currentEventsSubScreen = "Map"
                }

                // Botó CALENDAR
                TopButtonItem(
                    subScreenName = "Calendar",
                    iconRes = R.drawable.ic_calendar,
                    isSelected = (currentEventsSubScreen == "Calendar")
                ) {
                    currentEventsSubScreen = "Calendar"
                }

                // Botó LIST
                TopButtonItem(
                    subScreenName = "List",
                    iconRes = R.drawable.ic_list,
                    isSelected = (currentEventsSubScreen == "List")
                ) {
                    currentEventsSubScreen = "List"
                }
            }
        }

        // CONTENIDOR PRINCIPAL
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (currentScreen) {
                "Events" -> {
                    // Depenent de l'estat subScreen, mostrem una pantalla d'Events o altra
                    when (currentEventsSubScreen) {
                        "Map" -> EventMapScreen()
                        "Calendar" -> EventCalendarScreen(viewModel)
                        "List" -> EventListScreen(viewModel)
                    }
                }

                "Quiz" -> QuizScreen()
                "Leaderboard" -> LeaderboardScreen()
                "Settings" -> SettingsScreen(navController)
            }
        }

        // FOOTER amb 4 botons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomButtonItem(
                screenName = "Events",
                iconRes = R.drawable.ic_events,
                isSelected = currentScreen == "Events"
            ) {
                currentScreen = "Events"
                // Si canvia a Events, assegurem que Map quedi seleccionat per defecte
                currentEventsSubScreen = "Map"
            }

            BottomButtonItem(
                screenName = "Quiz",
                iconRes = R.drawable.ic_quiz,
                isSelected = currentScreen == "Quiz"
            ) {
                currentScreen = "Quiz"
            }

            BottomButtonItem(
                screenName = "Leaderboard",
                iconRes = R.drawable.ic_leaderboard,
                isSelected = currentScreen == "Leaderboard"
            ) {
                currentScreen = "Leaderboard"
            }

            BottomButtonItem(
                screenName = "Settings",
                iconRes = R.drawable.ic_settings,
                isSelected = currentScreen == "Settings",

                ) {

                currentScreen = "Settings"
            }
        }
    }
}

/** Composable genèric per als botons de la fila de dalt (Map, Calendar, List). */
@Composable
fun TopButtonItem(
    subScreenName: String,
    @DrawableRes iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val buttonColors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Morat
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    }

    Button(
        onClick = onClick,
        colors = buttonColors,
        modifier = Modifier.wrapContentSize(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = subScreenName
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subScreenName)
        }
    }
}

/** Composable genèric per als botons del footer. */
@Composable
fun BottomButtonItem(
    screenName: String,
    @DrawableRes iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val buttonColors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Morat
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    }

    Button(
        onClick = onClick,
        colors = buttonColors,
        modifier = Modifier.wrapContentSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = screenName
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = screenName, fontSize = 11.sp)
        }
    }
}

/** PANTALLES D’ESDEVENIMENTS (SUB-SCREENS) */


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventCalendarScreen(viewModel: EventViewModel) {
    CalendarScreen(viewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventListScreen(viewModel: EventViewModel) {
    EventListScreen(
        viewModel = viewModel,
        onEventSelected = { event -> }
    )
}


/** ALTRES PANTALLES PRINCIPALS */
@Composable
fun QuizScreen() {
    Text(text = "Aquesta serà la pantalla de Quiz")
}

@Composable
fun LeaderboardScreen() {
    Text(text = "Aquesta serà la pantalla de Leaderboard")
}