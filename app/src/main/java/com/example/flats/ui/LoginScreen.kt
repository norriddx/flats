package com.example.flats.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.flats.data.AuthRepository
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.TextField
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Вход",
                style = Typography.headlineLarge,
                color = Dark
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Почта",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Пароль",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    color = Blue,
                    style = Typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                text = "Войти",
                onClick = {
                    isLoading = true
                    error = null
                    scope.launch {
                        try {
                            AuthRepository.signIn(
                                email = email.trim(),
                                password = password
                            )
                            onLoginSuccess()
                        } catch (e: Exception) {
                            error = e.message ?: "Ошибка входа"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Нет аккаунта?",
                style = Typography.bodySmall,
                color = Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Зарегистрироваться",
                style = Typography.bodySmall,
                color = Blue,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )

            Spacer(modifier = Modifier.weight(0.6f))
        }
    }
}