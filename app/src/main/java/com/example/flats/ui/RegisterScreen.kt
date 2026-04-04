package com.example.flats.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.data.AuthRepository
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.TextField
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.Red
import com.example.flats.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAgreed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun validateAndSubmit() {
        usernameError = null
        emailError = null
        passwordError = null
        generalError = null

        val trimmedUsername = username.trim()
        val trimmedEmail = email.trim()
        var hasError = false

        if (trimmedUsername.length < 2) {
            usernameError = "Минимум 2 символа"
            hasError = true
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            emailError = "Некорректный формат почты"
            hasError = true
        }

        if (password.length < 6) {
            passwordError = "Минимум 6 символов"
            hasError = true
        }

        if (!isAgreed) {
            generalError = "Необходимо принять условия использования"
            hasError = true
        }

        if (hasError) return

        isLoading = true
        scope.launch {
            try {
                AuthRepository.signUp(
                    email = trimmedEmail,
                    password = password,
                    username = trimmedUsername
                )
                onRegisterSuccess()
            } catch (e: Exception) {
                val message = e.message?.lowercase() ?: ""
                generalError = when {
                    "already registered" in message || "already been registered" in message -> "Эта почта уже зарегистрирована"
                    "email not confirmed" in message -> "Почта не подтверждена"
                    "too many requests" in message -> "Слишком много попыток. Попробуйте позже"
                    "network" in message || "unable to resolve host" in message -> "Нет подключения к интернету"
                    "timeout" in message -> "Сервер не отвечает. Попробуйте позже"
                    else -> "Ошибка регистрации: ${e.message}"
                }
            } finally {
                isLoading = false
            }
        }
    }

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
                text = "Регистрация",
                style = Typography.headlineLarge,
                color = Dark
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = null
                    generalError = null
                },
                placeholder = "Имя"
            )

            if (usernameError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = usernameError!!,
                    style = Typography.bodySmall,
                    color = Red,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                    generalError = null
                },
                placeholder = "Почта",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            if (emailError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = emailError!!,
                    style = Typography.bodySmall,
                    color = Red,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                    generalError = null
                },
                placeholder = "Пароль",
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible) R.drawable.ic_eye_open
                            else R.drawable.ic_eye_closed
                        ),
                        contentDescription = null,
                        tint = Dark,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { passwordVisible = !passwordVisible }
                    )
                }
            )

            if (passwordError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = passwordError!!,
                    style = Typography.bodySmall,
                    color = Red,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isAgreed = !isAgreed },
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isAgreed) R.drawable.ic_check_box else R.drawable.ic_check_box_empty
                    ),
                    contentDescription = null,
                    tint = Blue,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Я соглашаюсь с условиями использования и политикой конфиденциальности",
                    style = Typography.bodySmall,
                    color = Gray
                )
            }

            if (generalError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = generalError!!,
                    style = Typography.bodySmall,
                    color = Red,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                text = "Зарегистрироваться",
                onClick = { validateAndSubmit() },
                enabled = !isLoading && username.isNotBlank() && email.isNotBlank()
                        && password.isNotBlank() && isAgreed
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Есть аккаунт?",
                style = Typography.bodySmall,
                color = Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Войти",
                style = Typography.bodySmall,
                color = Blue,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNavigateToLogin() }
            )

            Spacer(modifier = Modifier.weight(0.6f))
        }
    }
}