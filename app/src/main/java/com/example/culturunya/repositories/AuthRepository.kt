package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.login.GoogleTokenRequest
import com.example.culturunya.dataclasses.login.LoginRequest
import retrofit2.Response
import com.example.culturunya.dataclasses.login.LoginResponse
import com.example.culturunya.dataclasses.register.RegisterRequest
import com.example.culturunya.dataclasses.register.RegisterResponse

/**
 * @class AuthRepository
 * @brief Clase que gestiona la autenticación de usuarios.
 *
 * Proporciona métodos para registrar y autenticar usuarios, así como para iniciar sesión con Google.
 */
class AuthRepository(private val api: Api) {
    /**
     * @brief Registra un nuevo usuario.
     *
     * @param username Nombre de usuario.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return Response<RegisterResponse>
     */
    suspend fun registerUser(username: String, email: String, password: String): Response<RegisterResponse> {
        return api.registerUser(RegisterRequest(username, email, password))
    }

    /**
     * @brief Inicia sesión con Google.
     *
     * @param idToken Token de autenticación de Google.
     * @return Result<LoginResponse>
     */
    suspend fun googleLogin(idToken: String): Result<LoginResponse> {
        return try {
            val temp = GoogleTokenRequest(idToken)
            val response = api.loginGoogle(temp)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * @brief Inicia sesión con correo electrónico y contraseña.
     *
     * @param request Objeto que contiene el correo electrónico y la contraseña del usuario.
     * @return Result<LoginResponse>
     */
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