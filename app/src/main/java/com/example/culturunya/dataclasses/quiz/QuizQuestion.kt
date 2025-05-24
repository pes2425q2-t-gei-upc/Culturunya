package com.example.culturunya.dataclasses.quiz

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int, // Índex de la resposta correcta
    val image: String? = null,
    val points: Int = 1 // Punts per defecte si no s'especifica
) 