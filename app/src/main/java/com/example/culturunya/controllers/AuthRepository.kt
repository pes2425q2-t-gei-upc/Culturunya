package com.example.culturunya.controllers

import android.util.Log
import com.example.culturunya.models.Message
import retrofit2.Response
import com.example.culturunya.models.RegisterRequest
import com.example.culturunya.models.RegisterResponse
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.changePassword.ChangePasswordResponse
import com.example.culturunya.models.deleteAccount.DeleteAccountRequest
import com.example.culturunya.models.deleteAccount.DeleteAccountResponse
import com.example.culturunya.models.getChats.ChatInfo
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import com.example.culturunya.models.sendMessage.SendMessageRequest
import retrofit2.HttpException

class AuthRepository(private val api: Api) {
    suspend fun registerUser(username: String, email: String, password: String): Response<RegisterResponse> {
        return api.registerUser(RegisterRequest(username, email, password))
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return api.login(request)
    }

    suspend fun deleteAccount(token: String, request: DeleteAccountRequest): DeleteAccountResponse {
        return api.deleteAccount(token)
    }

    suspend fun changePassword(token: String, request: ChangePasswordRequest): ChangePasswordResponse {
        return api.changePassword(token, request)
    }

    suspend fun getChats(token: String): Result<List<ChatInfo>> {
        return try {
            val response = api.getChats(token)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChatWithAdmin(token: String): Result<List<Message>> {
        return try {
            val response = api.getChatWithAdmin(token)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessageToAdmin(token: String, message: String): Result<Unit> {
        return try {
            val response = api.sendMessageToAdmin(token, SendMessageRequest(message))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}