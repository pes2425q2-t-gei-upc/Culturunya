package com.example.culturunya.controllers

import retrofit2.Response
import com.example.culturunya.models.register.RegisterRequest
import com.example.culturunya.models.register.RegisterResponse

class AuthRepository(private val api: Api) {
    suspend fun registerUser(username: String, email: String, password: String): Response<RegisterResponse> {
        return api.registerUser(RegisterRequest(username, email, password))
    }
}