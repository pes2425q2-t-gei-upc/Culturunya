package com.example.culturunya.models.currentSession

import java.util.*

class CurrentSession private constructor() {
    companion object {

        @Volatile
        private var instance: CurrentSession? = null

        var token: String = ""

        var username: String = ""

        var password: String = ""

        var language: String = Locale.getDefault().language

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CurrentSession().also { instance = it }
            }

        fun setUserData(token: String, username: String, password: String) {
            this.token = token
            this.username = username
            this.password = password
        }

        fun changeLanguage(lang: String) {
            language = lang
        }

        fun getAuthHeader(): String = "Token $token"

    }
}