package com.example.culturunya.controllers

import com.example.culturunya.endpoints.login.GoogleTokenRequest
import retrofit2.Response
import com.example.culturunya.models.login.LoginResponse
import com.example.culturunya.models.register.RegisterRequest
import com.example.culturunya.models.register.RegisterResponse

class AuthRepository(private val api: Api) {
    suspend fun registerUser(username: String, email: String, password: String): Response<RegisterResponse> {
        return api.registerUser(RegisterRequest(username, email, password))
    }

    suspend fun googleLogin(idToken: String): Result<LoginResponse> {
        return try {
            val temp = GoogleTokenRequest(idToken)
            val response = api.loginGoogle(temp)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}