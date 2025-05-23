package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.users.UserInfo
import com.example.culturunya.dataclasses.settings.ChangePasswordRequest
import com.example.culturunya.dataclasses.settings.ChangeUsernameRequest
import com.example.culturunya.dataclasses.settings.UpdateLanguageRequest
import com.example.culturunya.dataclasses.quiz.SetQuizPointsRequest
import retrofit2.HttpException
import okhttp3.MultipartBody

/**
 * @class UserRepository
 * @brief Clase que gestiona la información del usuario y las operaciones relacionadas.
 *
 * Proporciona métodos para obtener información del perfil, cerrar sesión, eliminar cuenta,
 * cambiar contraseña, cambiar nombre de usuario, subir foto de perfil y actualizar idioma.
 */
class UserRepository(private val api: Api) {
    /**
     * @brief Obtiene la información del perfil del usuario.
     *
     * @param token Token de autenticación del usuario.
     * @return UserInfo
     */
    suspend fun getProfileInfo(token: String): UserInfo {
        return try{
            api.getProfileInfo(token)
        } catch (e: Exception){
            throw e
        }
    }

    /**
     * @brief Cierra la sesión del usuario.
     *
     * @param token Token de autenticación del usuario.
     * @return Result<Unit>
     */
    suspend fun logout(token: String): Result<Unit> {
        return try {
            val response = api.logout(token)
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
     * @brief Elimina la cuenta del usuario.
     *
     * @param token Token de autenticación del usuario.
     * @return Result<Unit>
     */
    suspend fun deleteAccount(token: String): Result<Unit> {
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

    /**
     * @brief Cambia la contraseña del usuario.
     *
     * @param token Token de autenticación del usuario.
     * @param request Objeto que contiene la nueva contraseña.
     * @return Result<Unit>
     */
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

    /**
     * @brief Cambia el nombre de usuario del usuario.
     *
     * @param token Token de autenticación del usuario.
     * @param request Objeto que contiene el nuevo nombre de usuario.
     * @return Result<Unit>
     */
    suspend fun changeUsername(token: String, request: ChangeUsernameRequest): Result<Unit> {
        return try {
            val response = api.changeUsername(token, request)
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
     * @brief Sube la foto de perfil del usuario.
     *
     * @param token Token de autenticación del usuario.
     * @param profilePic Foto de perfil en formato MultipartBody.Part.
     * @return Result<Unit>
     */
    suspend fun uploadProfilePic(token: String, profilePic: MultipartBody.Part): Result<Unit> {
        return try {
            val response = api.uploadProfilePic(token, profilePic)
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
     * @brief Actualiza el idioma del usuario.
     *
     * @param token Token de autenticación del usuario.
     * @param language Nuevo idioma.
     * @return Result<Unit>
     */
    suspend fun updateLangugage(token: String, language: String): Result<Unit> {
        return try {
            val response = api.updateLanguage(token, UpdateLanguageRequest(language))
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
     * @brief Establece los puntos del cuestionario.
     *
     * @param token Token de autenticación del usuario.
     * @param points Puntos a establecer.
     * @return Result<Unit>
     */
    suspend fun setQuizPoints(token: String, points: Int): Result<Unit> {
        return try {
            val response = api.setQuizPoints(token, SetQuizPointsRequest(points))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}