package com.example.culturunya.dataclasses.login;

data class SessionData(
    val token: String,
    val username: String,
    val email: String,
    val profilePic: String,
    val isAdmin: Boolean
)
