package com.example.flats.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.flats.data.SupabaseClient
import com.example.flats.ui.screens.auth.AuthScreen
import com.example.flats.ui.screens.cards.CardsScreen
import com.example.flats.ui.screens.comparison.ComparisonScreen
import io.github.jan.supabase.auth.auth

@Composable
fun NavGraph(navController: NavHostController) {
    val startDestination = remember {
        if (SupabaseClient.client.auth.currentSessionOrNull() != null) Routes.CARDS else Routes.AUTH
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Routes.CARDS) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CARDS) {
            CardsScreen(
                onNavigateToComparison = { navController.navigate(Routes.COMPARISON) },
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.COMPARISON) {
            ComparisonScreen(onBack = { navController.popBackStack() })
        }
    }
}