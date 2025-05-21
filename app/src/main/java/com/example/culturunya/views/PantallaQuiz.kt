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
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.ui.theme.Morat

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
        // Botó enrere
        IconButton(
            onClick = {
                viewModel.savePoints()
                navController.popBackStack()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        // Contingut principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Puntuació
            Text(
                text = context.getString(R.string.quizScore, state.currentPoints),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Morat
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pregunta actual
            if (state.isLoading) {
                CircularProgressIndicator(color = Morat)
                Text(
                    text = context.getString(R.string.quizLoading),
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                state.currentQuestion?.let { question ->
                    // Contenidor de la pregunta amb animació
                    AnimatedVisibility(
                        visible = !state.showCorrectAnimation && !state.showIncorrectAnimation,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = question.question,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Opcions
                                question.options.forEachIndexed { index, option ->
                                    Button(
                                        onClick = { viewModel.checkAnswer(index) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Morat)
                                    ) {
                                        Text(option)
                                    }
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
                                text = context.getString(R.string.quizCorrect),
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
                                text = context.getString(R.string.quizIncorrect),
                                fontSize = 24.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Mostrar error si n'hi ha
            state.error?.let { error ->
                Text(
                    text = context.getString(R.string.quizError, error),
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
} 