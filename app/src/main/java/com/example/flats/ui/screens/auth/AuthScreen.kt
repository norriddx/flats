package com.example.flats.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.flats.ui.LoginScreen
import com.example.flats.ui.RegisterScreen

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    var showRegister by remember { mutableStateOf(false) }

    if (showRegister) {
        RegisterScreen(
            onNavigateToLogin = { showRegister = false },
            onRegisterSuccess = onAuthSuccess
        )
    } else {
        LoginScreen(
            onNavigateToRegister = { showRegister = true },
            onLoginSuccess = onAuthSuccess
        )
    }
}