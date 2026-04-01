package com.example.flats.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.flats.ui.LoginScreen

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    var showRegister by remember { mutableStateOf(false) }

    if (showRegister) {
        // add later
    } else {
        LoginScreen(
            onNavigateToRegister = { showRegister = true },
            onLoginSuccess = onAuthSuccess
        )
    }
}