package com.example.culturunya.models.changePassword

data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String
)