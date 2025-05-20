package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.login.GoogleTokenRequest
import com.example.culturunya.dataclasses.login.LoginRequest
import retrofit2.Response
import com.example.culturunya.dataclasses.login.LoginResponse
import com.example.culturunya.dataclasses.register.RegisterRequest
import com.example.culturunya.dataclasses.register.RegisterResponse

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

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = api.login(request)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }
}