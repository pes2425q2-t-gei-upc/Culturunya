package com.example.culturunya.models.login

import com.squareup.moshi.Json

data class LoginResponse(
    @Json(name = "token")
    val token: String,
    @Json(name = "user_id")
    val user_id: Int,
    @Json (name = "email")
    val email: String
)