package com.example.culturunya.navigation

sealed class AppScreens(val route: String) {
    object PantallaRegistre: AppScreens("register_screen")
    object IniciSessio: AppScreens("inici_sessio")
    object MainScreen: AppScreens("main_screen")
    object SettingsScreen: AppScreens("settings_screen")
    object CanviContrasenya: AppScreens("changePassword_screen")
    object Xat : AppScreens(
        "chat_screen?userId={userId}&username={username}&imageUrl={imageUrl}"
    ) {
        fun createRoute(
            userId: Int? = null,
            username: String? = null,
            imageUrl: String? = null
        ): String {
            val params = listOfNotNull(
                userId   ?.let { "userId=$it" },
                username ?.let { "username=$it" },
                imageUrl ?.let { "imageUrl=${it.toString()}" }
            ).joinToString("&")
            return "chat_screen${if (params.isNotEmpty()) "?$params" else ""}"
        }
    }
    object LlistaXats: AppScreens("chatList_screen")
    object EventMapScreen: AppScreens("event_map_screen")
}