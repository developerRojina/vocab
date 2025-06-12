package com.vocable

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Login : Screen("login")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object Quiz : Screen("quiz")
    object Splash : Screen("splash")
    object Home : Screen("home")
    object WordDetail : Screen("wordDetail/{wordId}") {
        fun createRoute(wordId: String) = "wordDetail/$wordId"
    }
}