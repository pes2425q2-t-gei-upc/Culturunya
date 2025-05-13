package com.example.culturunya.models.changeUsername

import com.squareup.moshi.Json

data class ChangeUsernameRequest(
    @Json(name="username")
    val username: String
) 