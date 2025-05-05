package com.example.culturunya.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.culturunya.R
import com.example.culturunya.endpoints.events.Event
import com.example.culturunya.endpoints.events.EventViewModel
import com.example.culturunya.models.currentSession.CurrentSession
import java.text.Collator
import java.util.Locale
import com.example.culturunya.ui.theme.Purple40

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventListScreen(
    viewModel: EventViewModel,
    onEventSelected: (Event) -> Unit
) {
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    val events by viewModel.allEvents.collectAsState()

    if (selectedEvent == null) {
        EventListView(events, onEventSelected = { event ->
            selectedEvent = event
        })
    } else {
        EventInfo(
            event = selectedEvent!!,
            onBack = { selectedEvent = null }
        )
    }
}

fun cleanPunctuation(text: String): String {
    // Regular expression to remove punctuation marks from the start and end,
    // but preserve accented vowels and ñ/Ñ.
    return text.replace(Regex("^[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]+|[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]+$"), "")
}

enum class SortCriteria {
    NAME, DATE
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventListView(
    events: List<Event>,
    onEventSelected: (Event) -> Unit,
) {
    var currentSortCriteria by remember { mutableStateOf(SortCriteria.NAME) }
    var isAscending by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }

    // Create a Collator for Spanish (es_ES)
    val spanishCollator = Collator.getInstance(Locale("es", "ES"))
    spanishCollator.strength = Collator.PRIMARY // To ignore case differences

    val sortedEvents = when (currentSortCriteria) {
        SortCriteria.DATE -> {
            if (isAscending) {
                events.sortedBy { it.date_start }
            } else {
                events.sortedByDescending { it.date_start }
            }
        }

        SortCriteria.NAME -> {
            if (isAscending) {
                events.sortedWith(compareBy(spanishCollator) { cleanPunctuation(it.name) })
            } else {
                events.sortedWith(compareByDescending(spanishCollator) { cleanPunctuation(it.name) })
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 8.dp)) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
                ,
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp)
                    .background(Purple40)
            ) {
                TextField(
                    // We will modify the text to use a stringResource
                    readOnly = true,
                    value = when (currentSortCriteria) {
                        SortCriteria.DATE -> getString(context, R.string.orderByDate, currentLocale)
                        SortCriteria.NAME -> getString(context, R.string.orderByName, currentLocale)
                    },
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .background(Purple40),
                    colors = androidx . compose . material3 . TextFieldDefaults.textFieldColors(
                            containerColor = Purple40,
                        textColor = Color.White,
                        focusedTrailingIconColor = Color.White,
                        unfocusedTrailingIconColor = Color.White,
                        disabledTrailingIconColor = Color.White,
                        disabledTextColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .exposedDropdownSize()
                        .background(Purple40)
                ) {
                    DropdownMenuItem(
                        text = { Text(text = getString(context, R.string.orderByName, currentLocale), color = Color.White) },
                        onClick = {
                            currentSortCriteria = SortCriteria.NAME
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = getString(context, R.string.orderByDate, currentLocale), color = Color.White) },
                        onClick = {
                            currentSortCriteria = SortCriteria.DATE
                            expanded = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { isAscending = !isAscending },
                colors = ButtonDefaults.buttonColors(containerColor = Purple40)
            ) {
                Text(
                    text = "↑/↓",
                    color = Color.White
                )
            }
        }
        if (events.isEmpty()) {
            Text("No hay eventos", color = Color.Black)
        } else {
            LazyColumn {
                items(sortedEvents) { event ->
                    EventBox(
                        event = event,
                        onEventClick = { onEventSelected(event) }
                    )
                }
            }
        }
    }
}