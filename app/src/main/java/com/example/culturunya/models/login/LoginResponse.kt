package com.example.culturunya.models.login

data class LoginResponse(
    val token: String,
    val user_id: Int,
    val email: String
)