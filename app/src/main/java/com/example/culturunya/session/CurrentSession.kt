package com.example.culturunya.session

import SessionManager
import android.util.Log
import kotlinx.coroutines.flow.first
import java.util.*

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

        var current_quiz_points: Int = 0

        var current_event_points: Int = 0
            private set

        // Singleton
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CurrentSession().also { instance = it }
            }

        // Métodos para manejar la sesión
        fun setTokenAndPassword(token: String, password: String) {
            Companion.token = token
            Companion.password = password
        }

        fun setUserData(
            username: String,
            email: String,
            profile_pic: String,
            language: String,
            rank_quiz: String,
            rank_event: String,
            current_quiz_points: Int,
            current_event_points: Int,
            is_admin: Boolean
        ) {
            Companion.username = username
            Companion.email = email
            Companion.profile_pic = profile_pic
            Companion.language = language
            Companion.rank_quiz = rank_quiz
            Companion.rank_event = rank_event
            Companion.current_quiz_points = current_quiz_points
            Companion.current_event_points = current_event_points
            Companion.is_admin = is_admin
        }

        fun setGoogleToken(idToken: String) {
            googleIdToken = idToken
        }

        fun getGoogleToken(): String = googleIdToken

        fun isAdmin(): Boolean {
            return is_admin
        }

        fun changeLanguage(lang: String) {
            language = lang
        }

        fun getAuthHeader(): String = "Token $token"

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
            current_quiz_points = 0
            current_event_points = 0
            is_admin = false
        }

        fun hasActiveSession(): Boolean = token.isNotEmpty()

        fun addRegisterData(userName: String, newPassword: String, mail: String) {
            username = userName
            password = newPassword
            email = mail
        }

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
                current_quiz_points = sessionData.currentQuizPoints
                current_event_points = sessionData.currentEventsPoints

                Log.d("CurrentSession", "Sesión cargada exitosamente: $sessionData")
            } catch (e: Exception) {
                Log.e("CurrentSession", "Error al cargar sesión del DataStore", e)
            }
        }

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
                    currentQuizPoints = current_quiz_points,
                    currentEventsPoints = current_event_points
                )
                Log.d("CurrentSession", "Sesión guardada exitosamente")
            } catch (e: Exception) {
                Log.e("CurrentSession", "Error al guardar sesión en DataStore", e)
            }
        }
    }
}