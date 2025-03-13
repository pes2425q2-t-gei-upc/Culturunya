package com.example.culturunya.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.culturunya.screens.ComposablePrincipal
import com.example.culturunya.screens.MainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.PrimeraPantalla.route) {
        composable(route = AppScreens.PrimeraPantalla.route) {
            ComposablePrincipal(navController)
        }
        composable(route = AppScreens.SegonaPantalla.route) {
            MainScreen(navController)
        }
    }
}