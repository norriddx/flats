package com.example.flats.ui.screens.settings

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
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Red
import com.example.flats.ui.theme.Typography

@Composable
fun SettingsScreen() {
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

            SettingsRow(text = "Аккаунт", onClick = { /* TODO */ })
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(text = "Критерии оценки", onClick = { /* TODO */ })
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(text = "Сброс весов критериев", onClick = { /* TODO */ })
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(text = "Связь с поддержкой", onClick = { /* TODO */ })

            Spacer(modifier = Modifier.height(32.dp))

            SettingsActionRow(
                iconRes = R.drawable.ic_exit,
                text = "Выйти из аккаунта",
                color = Dark,
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsActionRow(
                iconRes = R.drawable.ic_bin,
                text = "Удалить аккаунт",
                color = Red,
                onClick = { /* TODO */ }
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