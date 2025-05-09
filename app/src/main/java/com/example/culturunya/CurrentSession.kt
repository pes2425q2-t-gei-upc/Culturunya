package com.example.culturunya

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

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CurrentSession().also { instance = it }
            }

        fun setTokenAndPassword(token: String, password: String) {
            Companion.token = token
            Companion.password = password
        }

        fun setUserData(username: String, email: String, profile_pic: String) {
            Companion.username = username
            Companion.email = email
            Companion.profile_pic = profile_pic
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
    }
}