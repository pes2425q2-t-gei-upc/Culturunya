package com.example.culturunya.dataclasses.settings

data class ChangeProfilePicState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)