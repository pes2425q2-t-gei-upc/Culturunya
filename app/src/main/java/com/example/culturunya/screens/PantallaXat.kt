package com.example.culturunya.screens

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import com.example.culturunya.ui.theme.GrisMoltFluix
import com.example.culturunya.ui.theme.Morat
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturunya.R
import com.example.culturunya.endpoints.getChatWithAdmin.GetChatWithAdminViewModel
import com.example.culturunya.endpoints.getChatWithUserViewModel.GetChatWithUserViewModel
import com.example.culturunya.endpoints.sendMessageToAdmin.SendMessageToAdminViewModel
import com.example.culturunya.endpoints.sendMessageToUser.SendMessageToUserViewModel
import com.example.culturunya.models.Message
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageBubble(message: Message, imAdmin: Boolean, username: String) {
    val iveSent = iveSentIt(message, imAdmin, username)
    val backgroundColor = if (iveSent) Morat else GrisMoltFluix
    val textColor = if (iveSent) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = if (iveSent) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = message.text, color = textColor)
        }
        Text(
            text = formatTimestamp(message.date),
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun MessageWithDate(
    messages: List<Message>,
    imAdmin: Boolean,
    username: String?,
    lazyListState: LazyListState  // Añade este parámetro
) {
    if (messages.isEmpty()) return

    val messagesByDate = messages.groupBy { formatDate(it.date) }

    LazyColumn(
        state = lazyListState,  // Usa el estado proporcionado
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        messagesByDate.forEach { (date, messagesForDate) ->
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 4.dp)
                    )
                }
            }

            items(messagesForDate) { message ->
                MessageBubble(message, imAdmin, username ?: "")
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}


fun formatTimestamp(timestamp: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(timestamp)
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault()) // Solo la hora
        formatter.format(date!!)
    } catch (e: Exception) {
        "Hora inválida"
    }
}

fun formatDate(timestamp: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(timestamp)
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Solo la fecha
        formatter.format(date!!)
    } catch (e: Exception) {
        "Fecha inválida"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaXat(navController: NavController, userId: Int?, username: String?, imageUrl: String?) {
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language
    var imAdmin = false
    if (userId != -1 && username != null) imAdmin = true
    var messageText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    val loadMessagesViewModel = if (imAdmin) {
        viewModel<GetChatWithUserViewModel>()
    } else {
        viewModel<GetChatWithAdminViewModel>()
    }
    val sendMessageViewModel = if (imAdmin) {
        viewModel<SendMessageToUserViewModel>()
    } else {
        viewModel<SendMessageToAdminViewModel>()
    }
    val messages by if (imAdmin) {
        (loadMessagesViewModel as GetChatWithUserViewModel).getChatWithUserResponse.collectAsState(initial = emptyList())
    } else {
        (loadMessagesViewModel as GetChatWithAdminViewModel).getChatWithAdminResponse.collectAsState(initial = emptyList())
    }
    val sendMessageStatus by if (imAdmin) {
        (sendMessageViewModel as SendMessageToUserViewModel).sendMessageToUserStatus.collectAsState()
    } else {
        (sendMessageViewModel as SendMessageToAdminViewModel).sendMessageToAdminStatus.collectAsState()
    }

    LaunchedEffect(messages) {
        if (messages?.isNotEmpty() == true) {
            // Usa animateScrollToItem para un efecto suave
            lazyListState.animateScrollToItem(messages!!.size)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            if (imAdmin) {
                (loadMessagesViewModel as GetChatWithUserViewModel).getChatWithUser(userId!!.toString())
            } else {
                (loadMessagesViewModel as GetChatWithAdminViewModel).getChatWithAdmin()
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
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
                    text = if (imAdmin) username!! else getString(context, R.string.administrator, currentLocale),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Divider(
                color = Color.Gray,
                thickness = 1.dp,
            )

            // Mensaje área - ahora ocupa solo el espacio disponible, no fuerza un tamaño fijo
            Box(
                modifier = Modifier
                    .weight(1f)  // Usa weight para permitir que se ajuste al espacio disponible
                    .fillMaxWidth()
            ) {
                // Usa MessageWithDate aquí
                MessageWithDate(messages = messages.orEmpty(), imAdmin = imAdmin, username = username, lazyListState = lazyListState )
            }

            // Input área - siempre visible en la parte inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .imePadding(),  // Asegura que el teclado no oculte el input
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
                                if (imAdmin) {
                                    (sendMessageViewModel as SendMessageToUserViewModel).sendMessageToUser(userId!!, messageText)
                                } else {
                                    (sendMessageViewModel as SendMessageToAdminViewModel).sendMessageToAdmin(messageText)
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
    }
}

fun iveSentIt(message: Message, imAdmin: Boolean, username: String): Boolean {
    return (message.from != username && imAdmin) || (message.from != "Administrador" && !imAdmin)
}
