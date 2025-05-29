package com.example.culturunya.navigation

/**
 * @brief Definició de les pantalles de l'aplicació.
 *
 * Aquesta classe defineix les diferents pantalles de l'aplicació i les seves rutes associades.
 * Cada pantalla és representada com un objecte que hereta de la classe AppScreens.
 *
 * @param route La ruta associada a la pantalla.
 */
sealed class AppScreens(val route: String) {
    open fun createRoute() = route
    object PantallaRegistre: AppScreens("register_screen")
    object IniciSessio: AppScreens("inici_sessio")
    object MainScreen : AppScreens("main_screen/{initialScreen}") {
        fun createRoute(initialScreen: String): String {
            return "main_screen/$initialScreen"
        }
    }
    object SettingsScreen: AppScreens("settings_screen")
    object CanviContrasenya: AppScreens("changePassword_screen")
    object ChangeUsername : AppScreens("change_username")
    object Xat : AppScreens(
        "chat_screen?userId={userId}&username={username}&imageUrl={imageUrl}"
    ) {
        /**
         * Crea una ruta per a la pantalla de xat amb els paràmetres opcionals.
         *
         * @param userId L'ID de l'usuari (opcional).
         * @param username El nom d'usuari (opcional).
         * @param imageUrl L'URL de la imatge (opcional).
         * @return La ruta creada amb els paràmetres especificats.
         */
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
    object ChangeProfilePic : AppScreens("changeProfilePic") {
        override fun createRoute() = "changeProfilePic"
    }
    object Quiz : AppScreens("quiz") {
        override fun createRoute() = "quiz"
    }
    object Reports : AppScreens("report_screen")
    object ListReports: AppScreens("reports_screen")
}