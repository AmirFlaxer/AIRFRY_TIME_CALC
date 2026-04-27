package com.example.airfrycalc.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Library : Screen("library")
    object Session : Screen("session")
}
