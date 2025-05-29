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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.culturunya.screens.LeaderboardScreen
import androidx.compose.ui.platform.LocalContext
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.views.getString
import androidx.compose.ui.platform.LocalContext
import com.example.culturunya.views.getString

@RequiresApi(Build.VERSION_CODES.O)
@Composable
/**
 * @brief Pantalla principal de la aplicación con menú inferior.
 * @param navController Controlador de navegación para cambiar de pantalla.
 * @param viewModel ViewModel para gestionar el estado de los eventos.
 * @param initialScreen Pantalla inicial seleccionada.
 */
fun MainScreen(navController: NavController, viewModel: EventViewModel, initialScreen: String) {
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language

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
                    subScreenName = getString(context, R.string.nav_map, currentLocale),
                    icon = Icons.Default.Map,
                    isSelected = (currentEventsSubScreen == "Map")
                ) {
                    currentEventsSubScreen = "Map"
                }

                // Botó CALENDAR
                TopButtonItem(
                    subScreenName = getString(context, R.string.nav_calendar, currentLocale),
                    icon = Icons.Default.CalendarMonth,
                    isSelected = (currentEventsSubScreen == "Calendar")
                ) {
                    currentEventsSubScreen = "Calendar"
                }

                // Botó LIST
                TopButtonItem(
                    getString(context, R.string.nav_list, currentLocale),
                    icon = Icons.Default.List,
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

                "Quiz" -> QuizScreen(navController)
                "Leaderboard" -> LeaderboardScreen(navController)
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
                screenName = getString(context, R.string.nav_events, currentLocale),
                iconRes = R.drawable.ic_events,
                isSelected = currentScreen == "Events"
            ) {
                currentScreen = "Events"
                currentEventsSubScreen = "Map"
            }

            BottomButtonItem(
                screenName = getString(context, R.string.nav_quiz, currentLocale),
                iconRes = R.drawable.ic_quiz,
                isSelected = currentScreen == "Quiz"
            ) {
                currentScreen = "Quiz"
            }

            BottomButtonItem(
                screenName = getString(context, R.string.nav_leaderboard, currentLocale),
                iconRes = R.drawable.ic_leaderboard,
                isSelected = currentScreen == "Leaderboard"
            ) {
                currentScreen = "Leaderboard"
            }

            BottomButtonItem(
                screenName = getString(context, R.string.nav_settings, currentLocale),
                iconRes = R.drawable.ic_settings,
                isSelected = currentScreen == "Settings"
            ) {
                currentScreen = "Settings"
            }
        }
    }
}

/** Composable genèric per als botons de la fila de dalt (Map, Calendar, List). */
@Composable
/**
 * @brief Composable per a un botó de la fila superior.
 * @param subScreenName Nom de la subpantalla.
 * @param iconRes Recurso drawable de l'icona.
 * @param isSelected Indica si el botó està seleccionat.
 * @param onClick Funció a executar quan es fa clic al botó.
 */
fun TopButtonItem(
    subScreenName: String,
    icon: ImageVector,
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
                imageVector = icon,
                contentDescription = subScreenName
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subScreenName)
        }
    }
}

/** Composable genèric per als botons del footer. */
@Composable
/**
 * @brief Composable per a un botó del peu de pàgina.
 * @param screenName Nom de la pantalla.
 * @param iconRes Recurso drawable de l'icona.
 * @param isSelected Indica si el botó està seleccionat.
 * @param onClick Funció a executar quan es fa clic al botó.
 */
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

/** PANTALLES D'ESDEVENIMENTS (SUB-SCREENS) */


@RequiresApi(Build.VERSION_CODES.O)
@Composable
/**
 * @brief Pantalla de mapa d'esdeveniments.
 * @param viewModel ViewModel per gestionar l'estat dels esdeveniments.
 */
fun EventCalendarScreen(viewModel: EventViewModel) {
    CalendarScreen(viewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
/**
 * @brief Pantalla de mapa d'esdeveniments.
 * @param viewModel ViewModel per gestionar l'estat dels esdeveniments.
 */
fun EventListScreen(viewModel: EventViewModel) {
    EventListScreen(
        viewModel = viewModel,
        onEventSelected = { event -> }
    )
}


/** ALTRES PANTALLES PRINCIPALS */
@Composable
/**
 * @brief Pantalla de configuració.
 * @param navController Controlador de navegació per canviar de pantalla.
 */
fun QuizScreen(navController: NavController) {
    PantallaQuiz(navController)

@Composable
/**
 * @brief Pantalla de configuració. Encara no està implementada.
 */
fun LeaderboardScreen() {
    Text(text = "Aquesta serà la pantalla de Leaderboard")

}