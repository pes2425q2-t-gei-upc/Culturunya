package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.chats.Message
import com.example.culturunya.dataclasses.chats.ChatInfo
import com.example.culturunya.dataclasses.chats.SendMessageToAdminRequest
import com.example.culturunya.dataclasses.chats.SendMessageToUserRequest
import retrofit2.HttpException

class ChatRepository(private val api: Api) {
    /**
     * @brief Obtiene la lista de chats del usuario.
     *
     * @param token Token de autenticación del usuario.
     * @return Result<List<ChatInfo>>
     */
    suspend fun getChats(token: String): Result<List<ChatInfo>> {
        return try {
            val response = api.getChats(token)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * @brief Obtiene el chat con el administrador.
     *
     * @param token Token de autenticación del usuario.
     * @return Result<List<Message>>
     */
    suspend fun getChatWithAdmin(token: String): Result<List<Message>> {
        return try {
            val response = api.getChatWithAdmin(token)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * @brief Obtiene el chat con un usuario específico.
     *
     * @param token Token de autenticación del usuario.
     * @param userId ID del usuario con el que se desea obtener el chat.
     * @return Result<List<Message>>
     */
    suspend fun getChatWithUser(token: String, userId: String): Result<List<Message>> {
        return try {
            val response = api.getChatWithUser(token, userId)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * @brief Envía un mensaje al administrador.
     *
     * @param token Token de autenticación del usuario.
     * @param message Mensaje a enviar.
     * @return Result<Unit>
     */
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

    /**
     * @brief Envía un mensaje a un usuario específico.
     *
     * @param token Token de autenticación del usuario.
     * @param userId ID del usuario al que se desea enviar el mensaje.
     * @param message Mensaje a enviar.
     * @return Result<Unit>
     */
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