package com.example.culturunya.navigation

sealed class AppScreens(val route: String) {
    object PantallaRegistre: AppScreens("register_screen")
    object IniciSessio: AppScreens("inici_sessio")
    object MainScreen: AppScreens("main_screen")
    object SettingsScreen: AppScreens("settings_screen")
    object CanviContrasenya: AppScreens("changePassword_screen")
}