    package com.example.culturunya.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.culturunya.screens.ComposableCanviContrasenya
import com.example.culturunya.screens.ComposableIniciSessio
import com.example.culturunya.screens.MainScreen
import com.example.culturunya.screens.PantallaRegistre
import com.example.culturunya.screens.SettingsScreen

    @Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.IniciSessio.route) {
        composable(route = AppScreens.IniciSessio.route) {
            ComposableIniciSessio(navController)
        }
        composable(route = AppScreens.MainScreen.route) {
            MainScreen(navController)
        }
        composable(route = AppScreens.PantallaRegistre.route){
            PantallaRegistre(navController)
        }
        composable(route = AppScreens.SettingsScreen.route) {
            SettingsScreen(navController)
        }
    }
}