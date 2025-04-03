package com.example.culturunya.controllers

import retrofit2.Response
import com.example.culturunya.models.RegisterRequest
import com.example.culturunya.models.RegisterResponse

class AuthRepository(private val api: Api) {
    suspend fun registerUser(username: String, email: String, password: String): Response<RegisterResponse> {
        return api.registerUser(RegisterRequest(username, email, password))
    }
}
