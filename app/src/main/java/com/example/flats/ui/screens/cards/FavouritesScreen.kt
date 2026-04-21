package com.example.flats.ui.screens.cards

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flats.data.CardRepository
import com.example.flats.data.model.Card
import com.example.flats.ui.components.BottomBar
import com.example.flats.ui.components.BottomNavItem
import com.example.flats.ui.components.CardItem
import com.example.flats.ui.components.TopBar
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.Typography

@Composable
private fun ShimmerCardItem(brush: Brush) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(brush)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
    }
}

@Composable
fun FavouritesScreen(
    onBack: () -> Unit,
    onNavigateToComparison: () -> Unit,
    onNavigateToViewCard: (Long) -> Unit
) {
    var cards by remember { mutableStateOf<List<Card>?>(null) }
    var toggleCardId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        try {
            cards = CardRepository.getCards()
        } catch (_: Exception) {
            cards = emptyList()
        }
    }

    LaunchedEffect(toggleCardId) {
        val cardId = toggleCardId ?: return@LaunchedEffect
        val currentCards = cards ?: return@LaunchedEffect
        val card = currentCards.find { it.cardId == cardId } ?: return@LaunchedEffect
        cards = currentCards.map {
            if (it.cardId == cardId) it.copy(isFavourite = !it.isFavourite) else it
        }
        try {
            CardRepository.toggleFavourite(cardId, card.isFavourite)
        } catch (_: Exception) {
            cards = currentCards
        }
        toggleCardId = null
    }

    val favouriteCards = cards?.filter { it.isFavourite }

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
            TopBar(
                title = "Избранное",
                onBack = onBack
            )

            when {
                cards == null -> {
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
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 120.dp)
                    ) {
                        items(3) {
                            ShimmerCardItem(brush = shimmerBrush)
                        }
                    }
                }

                favouriteCards.isNullOrEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Пусто :(",
                            style = Typography.bodyLarge,
                            color = Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 120.dp)
                    ) {
                        items(favouriteCards, key = { it.cardId }) { card ->
                            CardItem(
                                card = card,
                                onFavouriteClick = { toggleCardId = card.cardId },
                                onClick = { onNavigateToViewCard(card.cardId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }
        }

        BottomBar(
            currentRoute = BottomNavItem.Home.route,
            onItemClick = { route ->
                when (route) {
                    BottomNavItem.Home.route -> onBack()
                    BottomNavItem.Comparison.route -> onNavigateToComparison()
                    BottomNavItem.Settings.route -> { /* TODO */ }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}