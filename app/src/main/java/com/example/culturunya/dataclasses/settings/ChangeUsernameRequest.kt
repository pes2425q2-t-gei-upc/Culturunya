package com.example.culturunya.dataclasses.settings

import com.squareup.moshi.Json

data class ChangeUsernameRequest(
    @Json(name="username")
    val username: String
) 