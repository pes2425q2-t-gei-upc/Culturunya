package com.example.culturunya.dataclasses.users;

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = true
)