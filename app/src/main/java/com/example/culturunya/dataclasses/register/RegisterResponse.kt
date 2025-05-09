package com.example.culturunya.dataclasses.register

data class RegisterResponse(
    val message: String,  // Missatge retornat per l'API
    val username: String?, // Camp opcional per evitar errors si no el retorna l'API
    val email: String? // Camp opcional
)

