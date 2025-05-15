package com.example.culturunya.models.quiz

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int // Índex de la resposta correcta
) 