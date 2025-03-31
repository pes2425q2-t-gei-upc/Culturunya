    package com.example.culturunya.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.culturunya.endpoints.events.EventViewModel
import com.example.culturunya.screens.*

    @Composable
fun AppNavigation() {
    val eventViewModel: EventViewModel = viewModel()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.IniciSessio.route) {
        composable(route = AppScreens.IniciSessio.route) {
            ComposableIniciSessio(navController)
        }
        composable(route = AppScreens.MainScreen.route) {
            MainScreen(navController, eventViewModel)
        }
        composable(route = AppScreens.PantallaRegistre.route){
            PantallaRegistre(navController)
        }
        composable(route = AppScreens.SettingsScreen.route) {
            SettingsScreen(navController)
        }
        composable(route = AppScreens.CanviContrasenya.route) {
            PantallaCanviContrasenya(navController)
        }
    }
}