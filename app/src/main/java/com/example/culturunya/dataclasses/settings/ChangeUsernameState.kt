package com.example.culturunya.dataclasses.settings

data class ChangeUsernameState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val newUsername: String = ""
)