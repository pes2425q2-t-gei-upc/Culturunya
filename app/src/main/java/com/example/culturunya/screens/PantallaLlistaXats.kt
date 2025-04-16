import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.culturunya.navigation.AppNavigation
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.Morat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


data class Chat(
    val name: String,
    val lastMessage: String,
    val avatar: Color,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLlistaXats(navController: NavController) {
    val chats = listOf(
        Chat("Juan Pérez", "Nos vemos mañana 👋", Morat),
        Chat("Mamá", "Te dejé la comida 🍲", Morat),
        Chat("Laura Gómez", "¿Puedes llamarme?", Morat),
        Chat("Grupo Trabajo", "Archivo subido ✅", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat),
        Chat("Bot Bancario", "Tu estado de cuenta está listo", Morat)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Chats",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Divider(
            color = Color.Black,
            thickness = 1.dp,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = 16.dp)
        ) {
            items(chats) { chat ->
                ChatItem(chat, navController)
                Divider()
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(AppScreens.Xat.createRoute(chat.name)) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .background(color = chat.avatar)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = chat.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                text = chat.lastMessage,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 1
            )
        }
    }
}