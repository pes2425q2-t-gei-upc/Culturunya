package com.example.culturunya.dataclasses.login

import com.squareup.moshi.Json

data class GoogleTokenRequest (
    @Json(name = "id_token") val id_token: String
)