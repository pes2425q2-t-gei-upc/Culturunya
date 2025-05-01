    package com.example.culturunya.navigation

import PantallaLlistaXats
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        composable(
            route = AppScreens.Xat.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                    nullable = false
                    defaultValue = -1
                },
                navArgument("username") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("imageUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.get("userId") as? Int
            val username = backStackEntry.arguments?.getString("username")
            val imageUrl = backStackEntry.arguments?.getString("imageUrl")

            PantallaXat(navController, userId, username, imageUrl)
        }
        composable(route = AppScreens.LlistaXats.route) {
            PantallaLlistaXats(navController)
        }
    }
}