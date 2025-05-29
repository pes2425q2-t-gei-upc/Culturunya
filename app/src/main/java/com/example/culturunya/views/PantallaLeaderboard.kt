package com.example.culturunya.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.views.TopButtonItem
import com.example.culturunya.views.functions.getString
import com.example.culturunya.R
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.*
import com.example.culturunya.viewmodels.GetLeaderboardEventsViewModel
import com.example.culturunya.viewmodels.GetLeaderboardQuizViewModel
import com.example.culturunya.views.popUpError

@Composable
fun LeaderboardScreen(navController: NavController) {
    var currentSubScreen by remember { mutableStateOf("Quiz") }

    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language

    val colorQuiz = Morat
    val colorSelectedQuiz = MoratFluix
    val colorEvents = Color.Blue
    val colorSelectedEvents = BlauClar

    var showRankInfoDialog by remember { mutableStateOf(false) }

    val getLeaderboardViewModel = if (currentSubScreen == "Quiz") {
        viewModel<GetLeaderboardQuizViewModel>()
    } else {
        viewModel<GetLeaderboardEventsViewModel>()
    }

    val getLeaderboardStatus by if (currentSubScreen == "Quiz") {
        (getLeaderboardViewModel as GetLeaderboardQuizViewModel).getLeaderboardQuizError.collectAsState()
    } else {
        (getLeaderboardViewModel as GetLeaderboardEventsViewModel).getLeaderboardEventsError.collectAsState()
    }

    val leaderboard by if (currentSubScreen == "Quiz") {
        (getLeaderboardViewModel as GetLeaderboardQuizViewModel).getLeaderboardQuizResponse.collectAsState(initial = emptyList())
    } else {
        (getLeaderboardViewModel as GetLeaderboardEventsViewModel).getLeaderboardEventsResponse.collectAsState(initial = emptyList())
    }

    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(getLeaderboardStatus) {
        if (getLeaderboardStatus != 200 && getLeaderboardStatus != null) showErrorDialog = true
    }

    LaunchedEffect(currentSubScreen) {
        if (currentSubScreen == "Quiz") {
            (getLeaderboardViewModel as GetLeaderboardQuizViewModel).getLeaderboardQuiz()
        } else {
            (getLeaderboardViewModel as GetLeaderboardEventsViewModel).getLeaderboardEvents()
        }
    }

    if (showErrorDialog) {
        popUpError(getString(context, R.string.unknownErrorRanking, currentLocale), onClick = {
            showErrorDialog = false
            navController.navigate(AppScreens.MainScreen.createRoute("Events"))
        })
    }

    LaunchedEffect(Unit) {
        if (currentSubScreen == "Quiz") {
            (getLeaderboardViewModel as GetLeaderboardQuizViewModel).getLeaderboardQuiz()
        } else {
            (getLeaderboardViewModel as GetLeaderboardEventsViewModel).getLeaderboardEvents()
        }
    }

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
                subScreenName = getString(context, R.string.quiz, currentLocale),
                icon = Icons.Default.CheckCircle,
                isSelected = (currentSubScreen == "Quiz"),
                onClick = { currentSubScreen = "Quiz" }
            )

            TopButtonItem(
                subScreenName = getString(context, R.string.eventAssistance, currentLocale),
                icon = Icons.Default.LocationOn,
                isSelected = (currentSubScreen == "Events"),
                onClick = { currentSubScreen = "Events" }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = if (currentSubScreen == "Quiz") colorQuiz else colorEvents,
                        modifier = Modifier
                            .size(48.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                            .clickable { showRankInfoDialog = true }
                    )
                }
            }

            item {
                Text(
                    text = getString(context, R.string.monthlyRanking, currentLocale),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                )
            }

            itemsIndexed(leaderboard ?: emptyList()) { pos, item ->
                val isCurrentUser = item.username == CurrentSession.username
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            if (isCurrentUser) if (currentSubScreen == "Quiz") colorSelectedQuiz else colorSelectedEvents else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#${pos + 1}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(40.dp),
                        color = Color.Black)
                    if (item.profile_picture == null) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil default",
                            tint = if (currentSubScreen == "Quiz") colorQuiz else colorEvents,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                    else {
                        val baseUrl = "http://nattech.fib.upc.edu:40369"
                        val urlFinal = baseUrl + item.profile_picture
                        AsyncImage(
                            model = urlFinal,
                            contentDescription = "Perfil Image",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(48.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(item.username,
                        modifier = Modifier.weight(1f),
                        color = Color.Black)

                    if (item.rank != "Unranked") RankIcon(item.rank)

                    Spacer(modifier = Modifier.width(10.dp))

                    Text("${item.points} pts",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black)
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        if (showRankInfoDialog) {
            AlertDialog(
                onDismissRequest = { showRankInfoDialog = false },
                confirmButton = {
                    Text(
                        text = "OK",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { showRankInfoDialog = false },
                        color = Morat
                    )
                },
                title = {
                    Text(getString(context, R.string.rankingSystem, currentLocale), fontWeight = FontWeight.Bold, color = Color.Black)
                },
                text = {
                    Column {
                        RankInfoItem(getString(context, R.string.unranked, currentLocale), getString(context, R.string.unrankedDescription, currentLocale), icon = Icons.Default.Remove, color = Color.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                        RankInfoItem(getString(context, R.string.bronze, currentLocale), getString(context, R.string.bronzeDescription, currentLocale), icon = Icons.Default.Diamond, color = Marro)
                        Spacer(modifier = Modifier.height(10.dp))
                        RankInfoItem(getString(context, R.string.silver, currentLocale), getString(context, R.string.silverDescription, currentLocale), icon = Icons.Default.Diamond, color = Color.LightGray)
                        Spacer(modifier = Modifier.height(10.dp))
                        RankInfoItem(getString(context, R.string.gold, currentLocale), getString(context, R.string.goldDescription, currentLocale), icon = Icons.Default.Diamond, color = Dorat)
                        Spacer(modifier = Modifier.height(10.dp))
                        RankInfoItem(getString(context, R.string.ramonllull, currentLocale), getString(context, R.string.ramonllullDescription, currentLocale), imageRes = R.drawable.llullvermell)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun RankInfoItem(title: String, description: String, icon: ImageVector? = null, color: Color = Color.Gray, imageRes: Int? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        } else if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = description, fontSize = 12.sp, color = Color.Black)
        }
    }
}

@Composable
fun RankIcon(rank: String, modifier: Modifier = Modifier) {
    when (rank) {
        "RamonLlull" -> {
            Image(
                painter = painterResource(id = R.drawable.llullvermell),
                contentDescription = "Llull",
                modifier = modifier.size(30.dp)
            )
        }

        "Gold", "Silver", "Bronze" -> {
            Icon(
                imageVector = Icons.Filled.Diamond,
                contentDescription = "Rank Icon",
                tint = when (rank) {
                    "Gold" -> Dorat
                    "Silver" -> Color.LightGray
                    "Bronze" -> Marro
                    else -> Color.Unspecified
                },
                modifier = modifier
            )
        }

        else -> {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Rank Icon",
                tint = Color.Black,
                modifier = modifier
            )
        }
    }
}
