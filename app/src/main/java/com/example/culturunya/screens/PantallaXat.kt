package com.example.culturunya.screens

import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.modifier.ModifierLocalReadScope
import androidx.navigation.NavController
import com.example.culturunya.ui.theme.GrisMoltFluix
import com.example.culturunya.ui.theme.Morat
import com.example.culturunya.ui.theme.Purple80
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding

data class Message(
    val text: String,
    val isFromUser: Boolean
)

@Composable
fun MessageBubble(message: Message) {
    val backgroundColor = if (message.isFromUser) Morat else GrisMoltFluix
    val textColor = if (message.isFromUser) Color.White else Color.Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor, shape = CircleShape)
                .padding(12.dp)
        ) {
            Text(text = message.text, color = textColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaXat(navController: NavController, username: String) {
    var messageText by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                Message("Hola, ¿cómo estás?", false),
                Message("¡Bien! ¿Y tú?", true),
                Message("Todo bien, gracias por preguntar.", false)
            )
        )
    }
    Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Morat)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .imePadding(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message)
                Spacer(modifier = Modifier.height(13.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f),
                placeholder = { Text("Escribe un mensaje...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    textColor = Color.Black,
                    placeholderColor = Color.Gray,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        val nuevoMensaje = Message(messageText, true)
                        //enviar a back
                        messages = messages.toMutableList().apply {
                            add(nuevoMensaje)
                        }
                        messageText = ""
                    }
                },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Enviar",
                    modifier = Modifier
                        .background(color = Morat, shape = CircleShape)
                        .padding(10.dp),
                    tint = Color.White)
            }
        }
    }
}
