package com.example.culturunya.navigation

sealed class AppScreens(val route: String) {
    object PrimeraPantalla: AppScreens("first_screen")
    object SegonaPantalla: AppScreens("second_screen")
    object PantallaRegistre: AppScreens("register_screen")
    object IniciSessio: AppScreens("inici_sessio")
    object MainScreen: AppScreens("main_screen")
    object SettingsScreen: AppScreens("settings_screen")
}