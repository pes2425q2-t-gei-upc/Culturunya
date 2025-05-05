package com.example.culturunya.controllers

import android.util.Log
import com.example.culturunya.models.Message
import com.example.culturunya.models.getChats.ChatInfo
import com.example.culturunya.models.sendMessage.SendMessageToAdminRequest
import com.example.culturunya.models.sendMessage.SendMessageToUserRequest
import retrofit2.HttpException

class ChatRepository(private val api: Api) {
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

    suspend fun getChatWithUser(token: String, userId: String): Result<List<Message>> {
        return try {
            val response = api.getChatWithUser(token, userId)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessageToAdmin(token: String, message: String): Result<Unit> {
        return try {
            val response = api.sendMessageToAdmin(token, SendMessageToAdminRequest(message))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessageToUser(token: String, userId: Int, message: String): Result<Unit> {
        return try {
            val response = api.sendMessageToUser(token, SendMessageToUserRequest(userId, message))
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