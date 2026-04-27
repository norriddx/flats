package com.example.flats.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.flats.data.OnboardingPreferences
import com.example.flats.data.SupabaseClient
import com.example.flats.ui.screens.auth.AuthScreen
import com.example.flats.ui.screens.cards.ArchiveScreen
import com.example.flats.ui.screens.cards.CardsScreen
import com.example.flats.ui.screens.cards.CreateCardScreen
import com.example.flats.ui.screens.cards.FavouritesScreen
import com.example.flats.ui.screens.cards.ViewCardScreen
import com.example.flats.ui.screens.comparison.CardSelectionScreen
import com.example.flats.ui.screens.comparison.ComparisonResultScreen
import com.example.flats.ui.screens.comparison.ComparisonScreen
import com.example.flats.ui.screens.onboarding.OnboardingScreen
import com.example.flats.ui.screens.settings.SettingsScreen
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val onboardingFlow = remember { OnboardingPreferences.isCompleted(context) }
    val onboardingCompleted by onboardingFlow.collectAsState(initial = null)
    val sessionStatus by SupabaseClient.client.auth.sessionStatus.collectAsState()

    var startDestination by remember { mutableStateOf<String?>(null) }

    var comparisonSelectedIds by rememberSaveable { mutableStateOf<List<Long>>(emptyList()) }

    LaunchedEffect(onboardingCompleted, sessionStatus) {
        if (startDestination != null) return@LaunchedEffect
        if (onboardingCompleted == null) return@LaunchedEffect
        if (sessionStatus is SessionStatus.Initializing) return@LaunchedEffect

        startDestination = when {
            onboardingCompleted == false -> Routes.ONBOARDING
            sessionStatus is SessionStatus.Authenticated -> Routes.CARDS
            else -> Routes.AUTH
        }
    }

    val destination = startDestination
    if (destination == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White))
        return
    }

    NavHost(
        navController = navController,
        startDestination = destination,
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
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
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
            ComparisonScreen(
                selectedIds = comparisonSelectedIds,
                onSelectedIdsChange = { comparisonSelectedIds = it },
                onNavigateToSelection = { navController.navigate(Routes.CARD_SELECTION) },
                onNavigateToResult = { navController.navigate(Routes.COMPARISON_RESULT) },
                onNavigateToCards = {
                    navController.popBackStack(Routes.CARDS, inclusive = false)
                },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.COMPARISON_RESULT) {
            ComparisonResultScreen(
                selectedIds = comparisonSelectedIds,
                onBack = {
                    comparisonSelectedIds = emptyList()
                    navController.popBackStack()
                },
                onNavigateToViewCard = { cardId ->
                    navController.navigate(Routes.viewCard(cardId))
                },
                onNavigateToCards = {
                    navController.popBackStack(Routes.CARDS, inclusive = false)
                },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.CARD_SELECTION) {
            CardSelectionScreen(
                initialSelectedIds = comparisonSelectedIds,
                onBack = { navController.popBackStack() },
                onSave = { ids ->
                    comparisonSelectedIds = ids
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateToCards = {
                    navController.popBackStack(Routes.CARDS, inclusive = false)
                },
                onNavigateToComparison = {
                    navController.navigate(Routes.COMPARISON)
                }
            )
        }
    }
}