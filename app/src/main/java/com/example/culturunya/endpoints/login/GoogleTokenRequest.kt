package com.example.culturunya.endpoints.login

import com.example.culturunya.endpoints.users.UserInfo
import com.squareup.moshi.Json

data class GoogleTokenRequest (
    @Json(name = "id_token") val id_token: String
)