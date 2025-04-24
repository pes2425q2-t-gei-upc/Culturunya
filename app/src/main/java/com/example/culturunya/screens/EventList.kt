package com.example.culturunya.screens.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.culturunya.endpoints.events.Event
import com.example.culturunya.endpoints.events.EventViewModel

@Composable
fun EventListScreen(
    viewModel: EventViewModel,
    onEventSelected: (Event) -> Unit
) {
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    val events by viewModel.events.collectAsState()

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

@Composable
private fun EventListView(
    events: List<Event>,
    onEventSelected: (Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (events.isEmpty()) {
            Text("No hay eventos", color = Color.Black)
        } else {
            LazyColumn {
                items(events) { event ->
                    EventBox(
                        event = event,
                        onEventClick = { onEventSelected(event) }
                    )
                }
            }
        }
    }
}