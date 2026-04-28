package com.example.flats.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.flats.ui.components.TopBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.components.NotificationSheet
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Red
import com.example.flats.ui.theme.Typography
import androidx.core.net.toUri
import com.example.flats.data.AuthRepository
import com.example.flats.data.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToCriteria: () -> Unit
) {
    val context = LocalContext.current
    var showResetSheet by remember { mutableStateOf(false) }
    var showLogoutSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showDeleteSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            TopBar(title = "Настройки")

            Spacer(modifier = Modifier.height(16.dp))

            SettingsRow(text = "Аккаунт", onClick = { onNavigateToAccount() })
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(text = "Критерии оценки", onClick = { onNavigateToCriteria() })
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(text = "Сброс весов критериев", onClick = { showResetSheet = true })
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(text = "Связь с поддержкой", onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:norriddx@gmail.com".toUri()
                }
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {}
            })

            Spacer(modifier = Modifier.height(32.dp))

            SettingsActionRow(
                iconRes = R.drawable.ic_exit,
                text = "Выйти из аккаунта",
                color = Dark,
                onClick = { showLogoutSheet = true }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsActionRow(
                iconRes = R.drawable.ic_bin,
                text = "Удалить аккаунт",
                color = Red,
                onClick = { showDeleteSheet = true }
            )
        }

        if (showResetSheet) {
            NotificationSheet(
                title = "Сброс весов критериев",
                text = "Сбросить веса критериев? Все критерии получат одинаковый вес — каждый будет влиять на результат сравнения равнозначно. Используй, если накопленные веса исказили картину или хочешь обнулить их влияние.",
                buttonText = "Сбросить",
                onButtonClick = { showResetSheet = false },
                onDismiss = { showResetSheet = false }
            )
        }

        if (showLogoutSheet) {
            NotificationSheet(
                title = "Выход из аккаунта",
                text = "Выйти из аккаунта? Все несинхронизированные данные могут быть утеряны.",
                buttonText = "Выйти",
                onButtonClick = {
                    showLogoutSheet = false
                    scope.launch {
                        try {
                            SupabaseClient.client.auth.signOut()
                        } catch (_: Exception) {}
                        onLogout()
                    }
                },
                onDismiss = { showLogoutSheet = false }
            )
        }

        if (showDeleteSheet) {
            NotificationSheet(
                title = "Удаление аккаунта",
                text = "Удалить аккаунт? Все данные будут утеряны.",
                buttonText = "Удалить",
                onButtonClick = {
                    showDeleteSheet = false
                    scope.launch {
                        try {
                            AuthRepository.deleteAccount()
                            SupabaseClient.client.auth.signOut()
                        } catch (_: Exception) {}
                        onLogout()
                    }
                },
                onDismiss = { showDeleteSheet = false }
            )
        }
    }
}

@Composable
private fun SettingsRow(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = Typography.headlineSmall,
            color = Dark,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = null,
            tint = Dark,
            modifier = Modifier
                .size(24.dp)
                .rotate(180f)
        )
    }
}

@Composable
private fun SettingsActionRow(
    iconRes: Int,
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = Typography.headlineSmall,
            color = color
        )
    }
}