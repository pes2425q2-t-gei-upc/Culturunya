package com.example.culturunya.dataclasses.quiz

data class QuizState(
    val currentQuestion: QuizQuestion? = null,
    val currentPoints: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCorrectAnimation: Boolean = false,
    val showIncorrectAnimation: Boolean = false,
    val selectedOption: Int? = null
)