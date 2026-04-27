package com.example.flats.ui.screens.settings

import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flats.data.AuthRepository
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.SecondaryButton
import com.example.flats.ui.components.TextField
import com.example.flats.ui.components.TopBar
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.Red
import com.example.flats.ui.theme.Typography
import kotlinx.coroutines.launch

private fun Modifier.topShadow(
    height: Dp = 12.dp,
    color: Color = Color(0x0A000000)
): Modifier = this.then(
    Modifier.drawBehind {
        val shadowPx = height.toPx()
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, color),
                startY = -shadowPx,
                endY = 0f
            ),
            topLeft = Offset(0f, -shadowPx),
            size = size.copy(height = shadowPx)
        )
    }
)

@Composable
fun AccountScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var initialName by remember { mutableStateOf("") }
    var initialEmail by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isLoaded by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val user = AuthRepository.getCurrentUser()
            initialName = user.username
            initialEmail = user.email
            name = user.username
            email = user.email
            isLoaded = true
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Ошибка загрузки", Toast.LENGTH_SHORT).show()
        }
    }

    val hasChanges = name != initialName || email.trim() != initialEmail

    fun validateAndSave() {
        nameError = null
        emailError = null
        var hasError = false

        if (name.isBlank()) {
            nameError = "Имя не может быть пустым"
            hasError = true
        }

        val trimmedEmail = email.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            emailError = "Некорректный формат почты"
            hasError = true
        }

        if (hasError) return

        isSaving = true
        scope.launch {
            try {
                if (name != initialName) {
                    AuthRepository.updateUsername(name)
                }
                if (trimmedEmail != initialEmail) {
                    // TODO: подтверждение почты
                    Toast.makeText(
                        context,
                        "Письмо для подтверждения отправлено на новую почту",
                        Toast.LENGTH_LONG
                    ).show()
                }
                onBack()
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                isSaving = false
                Toast.makeText(context, e.message ?: "Ошибка сохранения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .imePadding()
        ) {
            TopBar(title = "Аккаунт", onBack = onBack)

            if (!isLoaded) {
                Box(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        AccountShimmer()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    // name
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Имя", style = Typography.headlineSmall, color = Dark)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = null
                        },
                        placeholder = "Имя"
                    )
                    if (nameError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = nameError!!,
                            style = Typography.bodySmall,
                            color = Red,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // email
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Почта", style = Typography.headlineSmall, color = Dark)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
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

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .topShadow()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecondaryButton(
                        text = "Отменить изменения",
                        onClick = {
                            name = initialName
                            email = initialEmail
                            nameError = null
                            emailError = null
                        },
                        enabled = hasChanges && !isSaving && isLoaded,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        text = "Сохранить",
                        onClick = { validateAndSave() },
                        enabled = hasChanges && !isSaving && isLoaded,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountShimmer() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(LightBlue, Color(0xFFD8E0EE), LightBlue),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(0.dp))

        repeat(2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(shimmerBrush)
            )
        }
    }
}