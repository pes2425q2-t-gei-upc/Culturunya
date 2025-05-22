package com.example.culturunya.views

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.viewmodels.QuizViewModel
import com.example.culturunya.CurrentSession
import com.example.culturunya.ui.theme.Morat
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaQuiz(navController: NavController) {
    val viewModel: QuizViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language

    LaunchedEffect(Unit) {
        viewModel.setContext(context)
        viewModel.loadNewQuestion()
    }

    // Guardar punts quan es tanca la pantalla
    DisposableEffect(Unit) {
        onDispose {
            viewModel.savePoints()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Capçalera amb fletxa i títol
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.savePoints()
                    navController.popBackStack()
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Contingut principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Puntuació
            Text(
                text = getString(context, R.string.quizScore, currentLocale).format(state.currentPoints),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Morat,
                modifier = Modifier.padding(top = 0.dp, bottom = 8.dp)
            )

            // Pregunta i opcions amb scroll si cal
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.weight(1f, fill = false)) {
                androidx.compose.foundation.rememberScrollState().let { scrollState ->
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Morat)
                            Text(
                                text = getString(context, R.string.quizLoading, currentLocale),
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        } else {
                            state.currentQuestion?.let { currentQuestion ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Mostra la imatge si existeix
                                        if (currentQuestion.image != null) {
                                            val baseUrl = "http://nattech.fib.upc.edu:40369"
                                            val imageUrl = if (currentQuestion.image.startsWith("http")) currentQuestion.image else baseUrl + currentQuestion.image
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = "Quiz image",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(240.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color.LightGray)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                        Text(
                                            text = currentQuestion.question,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        // Opcions
                                        currentQuestion.options.forEachIndexed { index, option ->
                                            val isCorrect = index == currentQuestion.correctAnswer
                                            val isSelected = state.selectedOption == index
                                            val showAnim = state.showCorrectAnimation || state.showIncorrectAnimation
                                            val backgroundColor = when {
                                                showAnim && isCorrect -> Color(0xFFB9F6CA) // Verd clar
                                                showAnim && isSelected && !isCorrect -> Color(0xFFFF8A80) // Vermell clar
                                                else -> Morat
                                            }
                                            Button(
                                                onClick = { if (!showAnim) viewModel.checkAnswer(index) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 2.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                                                enabled = true
                                            ) {
                                                Text(option)
                                            }
                                        }
                                    }
                                }
                                // Animació de resposta correcta
                                AnimatedVisibility(
                                    visible = state.showCorrectAnimation,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .background(Color.Green.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = getString(context, R.string.quizCorrect, currentLocale),
                                            fontSize = 24.sp,
                                            color = Color.Green,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Animació de resposta incorrecta
                                AnimatedVisibility(
                                    visible = state.showIncorrectAnimation,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .background(Color.Red.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = getString(context, R.string.quizIncorrect, currentLocale),
                                            fontSize = 24.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Mostrar error si n'hi ha
            state.error?.let { error ->
                Text(
                    text = getString(context, R.string.quizError, currentLocale).format(error),
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
} 