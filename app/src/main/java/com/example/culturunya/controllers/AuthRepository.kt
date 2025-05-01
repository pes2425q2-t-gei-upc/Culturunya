package com.example.culturunya.controllers

import retrofit2.Response
import com.example.culturunya.models.RegisterRequest
import com.example.culturunya.models.RegisterResponse
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.deleteAccount.DeleteAccountRequest
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import com.squareup.moshi.Json
import retrofit2.HttpException

class AuthRepository(private val api: Api) {
    suspend fun registerUser(username: String, email: String, password: String): Response<RegisterResponse> {
        return api.registerUser(RegisterRequest(username, email, password))
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

    suspend fun deleteAccount(token: String, request: DeleteAccountRequest): Result<Unit> {
        return try {
            val response = api.deleteAccount(token)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(token: String, request: ChangePasswordRequest): Result<Unit> {
        return try {
            val response = api.changePassword(token, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun googleLogin(idToken: String): Result<LoginResponse> {
        return try {
            val response = api.loginGoogle(idToken)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}