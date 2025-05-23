package com.example.culturunya.session

import SessionManager
import android.util.Log
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * @class CurrentSession
 * @brief Clase singleton que gestiona la sesión actual del usuario en la aplicación.
 *
 * Almacena y administra los datos de autenticación, preferencias y estado de la sesión.
 */
class CurrentSession private constructor() {
    companion object {

        @Volatile
        private var instance: CurrentSession? = null

        var token: String = ""

        var googleIdToken: String = ""

        var username: String = ""

        var password: String = ""

        var language: String = Locale.getDefault().language

        var is_admin: Boolean = false

        var email: String = ""

        var profile_pic: String = ""

        var current_quiz_points: Int = 0

        /**
         * @brief Devuelve la instancia única de CurrentSession.
         *
         * @return CurrentSession
         */
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CurrentSession().also { instance = it }
            }

        /**
         * @brief Establece el token y la contraseña del usuario.
         *
         * @param token Token de autenticación del usuario.
         * @param password Contraseña del usuario.
         */
        fun setTokenAndPassword(token: String, password: String) {
            Companion.token = token
            Companion.password = password
        }

        /**
         * @brief Establece los datos del usuario.
         *
         * @param username Nombre de usuario.
         * @param email Correo electrónico del usuario.
         * @param profile_pic URL de la imagen de perfil del usuario.
         * @param language Idioma preferido del usuario.
         */
        fun setUserData(username: String, email: String, profile_pic: String, language: String) {
            Companion.username = username
            Companion.email = email
            Companion.profile_pic = profile_pic
            Companion.language = language
        }

        /**
         * @brief Establece el token de Google.
         *
         * @param idToken Token de Google del usuario.
         */
        fun setGoogleToken(idToken: String) {
            googleIdToken = idToken
        }

        /**
         * @brief Devuelve el token de autenticación del usuario en google
         */
        fun getGoogleToken(): String = googleIdToken

        /**
         * @brief Marca al usuario como administrador.
         */
        fun isAdmin() {
            is_admin = true
        }

        /**
         * @brief Cambia el idioma de la sesión.
         * @param lang Nuevo idioma.
         */
        fun changeLanguage(lang: String) {
            language = lang
        }

        /**
         * @brief Devuelve el encabezado de autenticación.
         * @return String Encabezado con el token.
         */
        fun getAuthHeader(): String = "Token $token"

        /**
         * @brief Limpia todos los datos de la sesión actual.
         */
        fun clearSession() {
            token = ""
            googleIdToken = ""
            username = ""
            password = ""
        }

        /**
         * @brief Verifica si hay una sesión activa.
         * @return Boolean Verdadero si hay una sesión activa, falso en caso contrario.
         */
        fun hasActiveSession(): Boolean {
            return token.isNotEmpty()
        }

        /**
         * @brief Establece los puntos del cuestionario actual.
         * @param points Puntos obtenidos en el cuestionario.
         */
        fun addRegisterData(userName: String, newPassword: String, mail: String) {
            username = userName
            password = newPassword
            email = mail
        }

        /**
         * @brief Establece los puntos del cuestionario actual.
         * @param points Puntos obtenidos en el cuestionario.
         */
        suspend fun loadFromDataStore(sessionManager: SessionManager) {
            Log.d("CurrentSession", "Intentando recuperar sesión del DataStore...")

            val sessionData = sessionManager.sessionData.first()

            Log.d("CurrentSession", "Datos recuperados: $sessionData")

            token = sessionData.token
            username = sessionData.username
            email = sessionData.email
            profile_pic = sessionData.profilePic
            is_admin = sessionData.isAdmin
        }

        /**
         * @brief Guarda la sesión actual en DataStore.
         * @param sessionManager Instancia de SessionManager para guardar los datos.
         */
        suspend fun saveToDataStore(sessionManager: SessionManager) {
            sessionManager.saveSession(
                token = token,
                username = username,
                email = email,
                profilePic = profile_pic,
                isAdmin = is_admin
            )
        }
    }
}