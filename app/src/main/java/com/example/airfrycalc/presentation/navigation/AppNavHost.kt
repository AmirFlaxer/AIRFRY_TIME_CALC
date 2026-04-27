package com.example.airfrycalc.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.airfrycalc.presentation.home.HomeScreen
import com.example.airfrycalc.presentation.home.HomeViewModel
import com.example.airfrycalc.presentation.library.LibraryScreen
import com.example.airfrycalc.presentation.library.LibraryViewModel
import com.example.airfrycalc.presentation.session.SessionScreen
import com.example.airfrycalc.presentation.session.SessionViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            val vm: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = vm,
                onStartSession = { navController.navigate(Screen.Session.route) },
                onOpenLibrary = { navController.navigate(Screen.Library.route) }
            )
        }

        composable(Screen.Library.route) {
            val vm: LibraryViewModel = hiltViewModel()
            LibraryScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Session.route) {
            val vm: SessionViewModel = hiltViewModel()
            SessionScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
