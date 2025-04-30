package com.example.culturunya.models.changePassword

import com.squareup.moshi.Json

data class ChangePasswordRequest(
    @Json(name="old_password")
    val old_password: String,
    @Json(name="new_password")
    val new_password: String
)