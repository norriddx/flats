package com.example.flats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.example.flats.data.OnboardingPreferences
import com.example.flats.ui.navigation.NavGraph
import com.example.flats.ui.theme.FlatsTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val onboardingCompleted = runBlocking {
            OnboardingPreferences.isCompleted(applicationContext).first()
        }

        setContent {
            FlatsTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    onboardingCompleted = onboardingCompleted
                )
            }
        }
        window.decorView.post {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        }
    }
}