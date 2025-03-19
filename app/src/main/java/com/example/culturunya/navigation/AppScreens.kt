package com.example.culturunya.navigation

sealed class AppScreens(val route: String) {
    object IniciSessio: AppScreens("inici_sessio")
    object MainScreen: AppScreens("main_screen")
    object SettingsScreen: AppScreens("settings_screen")
}