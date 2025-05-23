package com.example.culturunya.session

import SessionManager
import android.util.Log
import kotlinx.coroutines.flow.first
import java.util.*

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

        var rank_quiz: String = ""

        var rank_event: String = ""

        var current_quiz_points: Int = 0

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CurrentSession().also { instance = it }
            }

        fun setTokenAndPassword(token: String, password: String) {
            Companion.token = token
            Companion.password = password
        }

        fun setUserData(username: String, email: String, profile_pic: String, language: String, rank_quiz: String, rank_event: String) {
            Companion.username = username
            Companion.email = email
            Companion.profile_pic = profile_pic
            Companion.language = language
            Companion.rank_quiz = rank_quiz
            Companion.rank_event = rank_event
        }

        fun setGoogleToken(idToken: String) {
            googleIdToken = idToken
        }

        fun getGoogleToken(): String = googleIdToken

        fun isAdmin() {
            is_admin = true
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
        }

        fun hasActiveSession(): Boolean {
            return token.isNotEmpty()
        }

        fun addRegisterData(userName: String, newPassword: String, mail: String) {
            username = userName
            password = newPassword
            email = mail
        }

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