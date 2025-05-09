package com.example.culturunya.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.culturunya.controllers.AppScreens
import com.example.culturunya.ui.theme.Morat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.culturunya.R
import com.example.culturunya.viewmodels.GetChatsViewModel
import com.example.culturunya.CurrentSession
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


data class Chat(
    val username: String,
    val lastMessage: String,
    val avatar: String?,
    val lastMessageDate: String,
    val userId: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLlistaXats(
    navController: NavController,
    getChatsViewModel: GetChatsViewModel = viewModel()
) {
    val response by getChatsViewModel.getChatsResponse.collectAsState(initial = null)
    val error by getChatsViewModel.getChatsError.collectAsState()
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language

    LaunchedEffect(Unit) {
        getChatsViewModel.getChats()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.navigate(AppScreens.MainScreen.createRoute("Settings"))
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Text(
                text = "Chats",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.Black
            )
        }

        Divider(color = Color.Black, thickness = 1.dp)

        when {
            response != null -> {
                val chats = response!!.map {
                    val parsedDate = ZonedDateTime.parse(it.last_message_date)
                    val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("HH:mm dd/MM"))

                    Chat(
                        username = it.username ?: "",
                        lastMessage = it.last_message_text ?: "",
                        avatar = it.profile_pic,
                        lastMessageDate = formattedDate,
                        userId = it.user_id
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                ) {
                    items(chats) { chat ->
                        ChatItem(chat, navController)
                        Divider()
                    }
                }
            }

            error != null -> {
                Text(
                    text = getString(context, R.string.unexpectedErrorLoadingChat, currentLocale),
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}


@Composable
fun ChatItem(chat: Chat, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(AppScreens.Xat.createRoute(userId = chat.userId, username = chat.username, imageUrl = chat.avatar)) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (chat.avatar == null) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Perfil default",
                tint = Morat,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
        else {
            val baseUrl = "http://nattech.fib.upc.edu:40369"
            val imageUrl = baseUrl + chat.avatar
            AsyncImage(
                model = imageUrl,
                contentDescription = "Perfil Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = chat.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                    color = Color.Black
                )
                Text(
                    text = chat.lastMessageDate,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Text(
                text = chat.lastMessage,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 1
            )
        }
    }

}