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

        // Propiedades de la sesión
        var token: String = ""
            private set

        var googleIdToken: String = ""
            private set

        var username: String = ""

        var password: String = ""
            private set

        var language: String = Locale.getDefault().language
            private set

        var is_admin: Boolean = false
            private set

        var email: String = ""
            private set

        var profile_pic: String = ""
            private set

        var rank_quiz: String = ""
            private set

        var rank_event: String = ""
            private set

        var total_quiz_points: Int = 0

        /**
         * @brief Devuelve la instancia única de CurrentSession.
         *
         * @return CurrentSession
         */
        var total_event_points: Int = 0
            private set

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
         * OUTDATED
         * @brief Establece los datos del usuario.
         *
         * @param username Nombre de usuario.
         * @param email Correo electrónico del usuario.
         * @param profile_pic URL de la imagen de perfil del usuario.
         * @param language Idioma preferido del usuario.
         */
        fun setUserData(
            username: String,
            email: String,
            profile_pic: String,
            language: String,
            rank_quiz: String,
            rank_event: String,
            total_quiz_points: Int,
            total_event_points: Int,
            is_admin: Boolean
        ) {

            Companion.username = username
            Companion.email = email
            Companion.profile_pic = profile_pic
            Companion.language = language
            Companion.rank_quiz = rank_quiz
            Companion.rank_event = rank_event
            Companion.total_quiz_points = total_quiz_points
            Companion.total_event_points = total_event_points
            Companion.is_admin = is_admin
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
        fun isAdmin(): Boolean {
            return is_admin
        }

        fun setAdmin(isAdmin: Boolean) {
            is_admin = isAdmin

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
            email = ""
            profile_pic = ""
            is_admin = false
            rank_quiz = ""
            rank_event = ""
            total_quiz_points = 0
            total_event_points = 0
            is_admin = false
        }


        /**
         * OUTDATED
         * @brief Verifica si hay una sesión activa.
         * @return Boolean Verdadero si hay una sesión activa, falso en caso contrario.
         */
        fun hasActiveSession(): Boolean = token.isNotEmpty()


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
            try {
                Log.d("CurrentSession", "Intentando recuperar sesión del DataStore...")
                val sessionData = sessionManager.sessionData.first()

                token = sessionData.token
                username = sessionData.username
                email = sessionData.email
                profile_pic = sessionData.profilePic
                is_admin = sessionData.isAdmin
                rank_quiz = sessionData.rankQuiz
                rank_event = sessionData.rankEvents
                total_quiz_points = sessionData.totalQuizPoints
                total_event_points = sessionData.totalEventsPoints

                Log.d("CurrentSession", "Sesión cargada exitosamente: $sessionData")
            } catch (e: Exception) {
                Log.e("CurrentSession", "Error al cargar sesión del DataStore", e)
            }
        }

        /**
         * @brief Guarda la sesión actual en DataStore.
         * @param sessionManager Instancia de SessionManager para guardar los datos.
         */
        suspend fun saveToDataStore(sessionManager: SessionManager) {
            try {
                Log.d("CurrentSession", "Guardando sesión en DataStore...")
                sessionManager.saveSession(
                    token = token,
                    username = username,
                    email = email,
                    profilePic = profile_pic,
                    isAdmin = is_admin,
                    rankQuiz = rank_quiz,
                    rankEvents = rank_event,
                    totalQuizPoints = total_quiz_points,
                    totalEventsPoints = total_event_points,
                    is_admin = is_admin
                )
                Log.d("CurrentSession", "Sesión guardada exitosamente")
            } catch (e: Exception) {
                Log.e("CurrentSession", "Error al guardar sesión en DataStore", e)
            }
        }
    }
}