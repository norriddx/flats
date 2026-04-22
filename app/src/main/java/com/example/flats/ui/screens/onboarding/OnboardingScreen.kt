package com.example.flats.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.data.OnboardingPreferences
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.PageIndicator
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Typography
import com.example.flats.ui.theme.White
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String,
    val buttonText: String
)

private val pages = listOf(
    OnboardingPage(
        imageRes    = R.drawable.onboarding_1,
        title       = "Добро пожаловать!",
        description = "Flats — это твой помощник в поиске квартиры. Сохраняй впечатления от просмотров и принимай решения",
        buttonText  = "Продолжить"
    ),
    OnboardingPage(
        imageRes    = R.drawable.onboarding_2,
        title       = "Добавляй квартиры и дома",
        description = "Фиксируй всё, что важно: адрес, фото, удобства и личные заметки",
        buttonText  = "Продолжить"
    ),
    OnboardingPage(
        imageRes    = R.drawable.onboarding_3,
        title       = "Сравнивай и выбирай",
        description = "Сравнивай варианты по критериям и выбирай лучший без лишних сомнений",
        buttonText  = "Начать"
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pageIndex by remember { mutableIntStateOf(0) }

    val finish: () -> Unit = {
        scope.launch {
            OnboardingPreferences.setCompleted(context)
            onFinish()
        }
        Unit
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = pageIndex,
            transitionSpec = {
                slideInHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth } togetherWith
                        slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth }
            },
            label = "onboardingImage",
            modifier = Modifier.fillMaxSize()
        ) { index ->
            Image(
                painter = painterResource(id = pages[index].imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (pageIndex < pages.size - 1) {
            Text(
                text = "Пропустить",
                style = Typography.bodyMedium,
                color = White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 74.dp, end = 20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = finish
                    )
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(White)
                .padding(top = 32.dp, start = 20.dp, end = 20.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = pages[pageIndex].title,
                style = Typography.headlineMedium,
                color = Dark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = pages[pageIndex].description,
                style = Typography.bodyLarge,
                color = Dark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            PageIndicator(
                count = pages.size,
                selected = pageIndex
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                text = pages[pageIndex].buttonText,
                onClick = {
                    if (pageIndex < pages.size - 1) pageIndex++ else finish()
                }
            )
        }
    }
}