package com.example.culturunya.navigation

sealed class AppScreens(val route: String) {
    object PrimeraPantalla: AppScreens("first_screen")
    object SegonaPantalla: AppScreens("second_screen")
}