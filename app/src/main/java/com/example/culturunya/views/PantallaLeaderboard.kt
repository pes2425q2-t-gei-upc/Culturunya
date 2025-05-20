package com.example.culturunya.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.example.culturunya.ui.theme.Morat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen() {
    val leaderboard = listOf(
        Triple(1, "Username", 1470),
        Triple(2, "Username", 1470),
        Triple(3, "Username", 1470),
        Triple(4, "Username", 1470),
        Triple(5, "Username", 1470),
        Triple(6, "Username", 1470),
        Triple(7, "Username", 1460),
        Triple(8, "Username", 1001),
        Triple(9, "You", 969),
        Triple(10, "Username", 600),
        Triple(11, "Username", 515),
        Triple(12, "Username", 470)
    )

    var currentSubScreen = "Quizz"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TopButtonItem(
                subScreenName = "Quizz",
                icon = Icons.Default.CheckCircle,
                isSelected = (currentSubScreen == "Quizz"),
                onClick = { currentSubScreen = "Quizz" }
            )

            TopButtonItem(
                subScreenName = "Geolocalization",
                icon = Icons.Default.LocationOn,
                isSelected = (currentSubScreen == "Geolocalization"),
                onClick = { currentSubScreen = "Geolocalization" }
            )
        }
        Spacer(modifier = Modifier.height(40.dp))

        Icon(
            imageVector = Icons.Default.BarChart,
            contentDescription = null,
            tint = Morat,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Monthly Ranking",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )

        LazyColumn {
            items(leaderboard) { (rank, name, points) ->
                val isCurrentUser = name == "You"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            if (isCurrentUser) Color(0xFFDBD8FF) else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#$rank", fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(name, modifier = Modifier.weight(1f))
                    Text("${points} pts", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
