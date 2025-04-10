package com.example.culturunya.models.currentUser

class User private constructor() {
    companion object {

        @Volatile
        private var instance: User? = null

        var token: String = ""

        var username: String = ""

        var password: String = ""

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: User().also { instance = it }
            }

        fun setUserData(token: String, username: String, password: String) {
            this.token = token
            this.username = username
            this.password = password
        }
    }
}