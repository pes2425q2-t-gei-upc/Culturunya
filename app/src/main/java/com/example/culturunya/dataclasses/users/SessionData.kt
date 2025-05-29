package com.example.culturunya.dataclasses.users

data class SessionData(
    val token: String,
    val username: String,
    val email: String,
    val profilePic: String,
    val isAdmin: Boolean,
    val rankQuiz: String,
    val rankEvents: String,
    val totalQuizPoints: Int,
    val totalEventsPoints: Int,
    val is_admin: Boolean
)