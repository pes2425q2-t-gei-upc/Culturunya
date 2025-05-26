package com.example.culturunya.navigation

import android.app.Activity
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.culturunya.viewmodels.EventViewModel
import com.example.culturunya.views.*
import com.example.culturunya.views.ComposableIniciSessio
import com.example.culturunya.views.MainScreen
import com.example.culturunya.views.PantallaRegistre
import com.example.culturunya.views.PantallaCanviContrasenya
import com.example.culturunya.views.SettingsScreen
import com.example.culturunya.views.PantallaReport
import androidx.navigation.compose.*
import com.example.culturunya.R
import com.example.culturunya.session.CurrentSession

@RequiresApi(Build.VERSION_CODES.O)
    @Composable
fun AppNavigation(isLoggedIn: Boolean, onLogout: () -> Unit) {
    val eventViewModel: EventViewModel = viewModel()
    val navController = rememberNavController()
    var lastBackPressTime by remember { mutableStateOf(0L) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language

    BackHandler {
        if (navController.previousBackStackEntry == null) {
            val now = SystemClock.elapsedRealtime()
            if (now - lastBackPressTime < 2000) {
                (context as? Activity)?.finish()
            } else {
                lastBackPressTime = now
                Toast.makeText(context, getString(context, R.string.pressAgainExit, currentLocale), Toast.LENGTH_SHORT).show()
            }
        }
        else {
            navController.popBackStack()
        }
    }

    NavHost(navController = navController, startDestination = if (isLoggedIn) AppScreens.MainScreen.createRoute("Events") else AppScreens.IniciSessio.route) {
        composable(route = AppScreens.IniciSessio.route) {
            ComposableIniciSessio(navController)
        }
        composable(
            route = AppScreens.MainScreen.route,
            arguments = listOf(
                navArgument("initialScreen") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val initialScreen = backStackEntry.arguments?.getString("initialScreen")!!
            MainScreen(navController, eventViewModel, initialScreen)
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
        composable(route = AppScreens.ChangeUsername.route) {
            PantallaCanviNom(navController)
        }
        composable(route = AppScreens.ChangeProfilePic.route) {
            PantallaCanviFotoPerfil(navController)
        }
        composable(route = AppScreens.Quiz.route) {
            PantallaQuiz(navController)
        }
        composable(route = "event_map_screen") {
            EventMapScreen()
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
        composable(route = AppScreens.Reports.route){
            PantallaReport(navController)
        }
        composable(route = AppScreens.ListReports.route) {
            ReportsListScreen(navController)
        }
    }
}