package com.example.culturunya.models.login

data class LoginResponse(
    val email: String,
    val token: String,
    val user_id: Int,
    val username: String
)