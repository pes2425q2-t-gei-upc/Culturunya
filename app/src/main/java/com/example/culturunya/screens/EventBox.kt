package com.example.culturunya.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culturunya.endpoints.events.Event
import com.example.culturunya.ui.theme.Purple40

@Composable
fun EventBox(
    event: Event,
    onEventClick: (Event) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Purple40, RoundedCornerShape(8.dp))
            .clickable { showMenu = true }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Start
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Detalles del Evento") },
                onClick = {
                    onEventClick(event)
                    showMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Guardar Evento") },
                onClick = { showMenu = false }
            )
        }
    }
}