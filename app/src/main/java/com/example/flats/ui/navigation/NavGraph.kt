package com.example.flats.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.flats.data.SupabaseClient
import com.example.flats.ui.screens.auth.AuthScreen
import com.example.flats.ui.screens.cards.ArchiveScreen
import com.example.flats.ui.screens.cards.CardsScreen
import com.example.flats.ui.screens.cards.CreateCardScreen
import com.example.flats.ui.screens.cards.FavouritesScreen
import com.example.flats.ui.screens.cards.ViewCardScreen
import com.example.flats.ui.screens.comparison.ComparisonScreen
import com.example.flats.ui.screens.onboarding.OnboardingScreen
import io.github.jan.supabase.auth.auth

@Composable
fun NavGraph(
    navController: NavHostController,
    onboardingCompleted: Boolean
) {
    val startDestination = remember {
        when {
            !onboardingCompleted -> Routes.ONBOARDING
            SupabaseClient.client.auth.currentSessionOrNull() != null -> Routes.CARDS
            else -> Routes.AUTH
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(150)) },
        exitTransition = { fadeOut(animationSpec = tween(150)) },
        popEnterTransition = { fadeIn(animationSpec = tween(150)) },
        popExitTransition = { fadeOut(animationSpec = tween(150)) }
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

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
                onNavigateToCreateCard = { navController.navigate(Routes.CREATE_CARD) },
                onNavigateToViewCard = { cardId ->
                    navController.navigate(Routes.viewCard(cardId))
                },
                onNavigateToFavourites = { navController.navigate(Routes.FAVOURITES) },
                onNavigateToArchive = { navController.navigate(Routes.ARCHIVE) },
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CREATE_CARD) {
            CreateCardScreen(
                cardId = null,
                onBack = { navController.popBackStack() },
                onDelete = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_CARD,
            arguments = listOf(navArgument("cardId") { type = NavType.LongType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong("cardId") ?: 0L
            CreateCardScreen(
                cardId = cardId,
                onBack = { navController.popBackStack() },
                onDelete = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.VIEW_CARD,
            arguments = listOf(navArgument("cardId") { type = NavType.LongType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong("cardId") ?: 0L
            ViewCardScreen(
                cardId = cardId,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Routes.editCard(cardId)) }
            )
        }

        composable(Routes.FAVOURITES) {
            FavouritesScreen(
                onBack = { navController.popBackStack() },
                onNavigateToComparison = { navController.navigate(Routes.COMPARISON) },
                onNavigateToViewCard = { cardId ->
                    navController.navigate(Routes.viewCard(cardId))
                }
            )
        }

        composable(Routes.ARCHIVE) {
            ArchiveScreen(
                onBack = { navController.popBackStack() },
                onNavigateToComparison = { navController.navigate(Routes.COMPARISON) },
                onNavigateToViewCard = { cardId ->
                    navController.navigate(Routes.viewCard(cardId))
                }
            )
        }

        composable(Routes.COMPARISON) {
            ComparisonScreen(onBack = { navController.popBackStack() })
        }
    }
}