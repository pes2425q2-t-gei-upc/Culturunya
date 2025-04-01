package com.example.culturunya.screens

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.*
import com.example.culturunya.controllers.*
import com.example.culturunya.endpoints.events.Event
import com.example.culturunya.endpoints.events.EventViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pantalla() {
    Scaffold {
        //MainScreen()
    }
}

@Composable
fun MainScreen(navController: NavController, viewModel: EventViewModel) {
    // Estat per a la pantalla principal
    var currentScreen by remember { mutableStateOf("Events") }
    // Estat per als sub-botons d'Events (Map, Calendar, List)
    // Només s'usa si la pantalla principal seleccionada és "Events".
    var currentEventsSubScreen by remember { mutableStateOf("Map") }

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
                        "Calendar" -> EventCalendarScreen()
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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
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
            Text(text = screenName)
        }
    }
}

/** PANTALLES D’ESDEVENIMENTS (SUB-SCREENS) */
@Composable
fun EventMapScreen() {
    Text("Això és la pantalla Map d'Events")
}

@Composable
fun EventCalendarScreen() {
    CalendarScreen()
}

@Composable
fun EventListScreen(viewModel: EventViewModel) {
    var selecetedEvent by remember { mutableStateOf<Event?>(null) }
    val events by viewModel.events.collectAsState()
    if(selecetedEvent == null){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            if(events.isEmpty()){
                Text("No hay eventos", color = Color.Black)
            }
            else{
                LazyColumn {
                    items(events) { event ->
                        EventBox(event) {selecetedEvent = event}
                    }
                }
            }
        }
    }
    else{
        EventInfo(event = selecetedEvent!!){
            selecetedEvent = null
        }
    }
}

@Composable
fun EventInfo(event: Event, onBack: () -> Unit) {

    LazyColumn(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        item( key = event.id) {
        Text(text = "Info del evento: ${event.name}",modifier = Modifier
                .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
            textAlign = TextAlign.Center)
        Text(text = "Ubicacion del evento: ${event.location.address}",modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
            textAlign = TextAlign.Center)
        Text("Descripcion del evento: ${event.description}",modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
            textAlign = TextAlign.Center)
        Text("Fecha de inicio: ${event.date_start}",modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
            textAlign = TextAlign.Center)
        Text("Fecho de fin: ${event.date_end}",modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
            textAlign = TextAlign.Center)
        Button(onClick = onBack, modifier = Modifier.padding(8.dp), colors = ButtonDefaults.buttonColors(), shape = RoundedCornerShape(8.dp)) {
            Text("Volver")
        }
        }
    }
}

@Composable
fun EventBox(event: Event, onEventClick: (Event) -> Unit) {
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
                text = event.name,
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
                    onEventClick(event) // Call the callback with the selected event
                    showMenu = false
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
/** ALTRES PANTALLES PRINCIPALS */
@Composable
fun QuizScreen() {
    Text(text = "Aquesta serà la pantalla de Quiz")
}

@Composable
fun LeaderboardScreen() {
    Text(text = "Aquesta serà la pantalla de Leaderboard")
}