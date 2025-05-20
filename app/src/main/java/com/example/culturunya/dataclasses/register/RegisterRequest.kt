package com.example.culturunya.dataclasses.register

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
