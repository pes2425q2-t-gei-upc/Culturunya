    package com.example.culturunya.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.culturunya.screens.*

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
        composable(route = AppScreens.CanviContrasenya.route) {
            PantallaCanviContrasenya(navController)
        }
    }
}