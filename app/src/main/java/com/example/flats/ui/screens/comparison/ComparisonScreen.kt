package com.example.flats.ui.screens.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.flats.R
import com.example.flats.data.CardRepository
import com.example.flats.data.model.Card
import com.example.flats.ui.components.BottomBar
import com.example.flats.ui.components.BottomNavItem
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.TopBar
import com.example.flats.ui.components.TopBarAction
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.LightGray
import com.example.flats.ui.theme.Typography

private const val MAX_SLOTS = 4

@Composable
fun ComparisonScreen(
    selectedIds: List<Long>,
    onSelectedIdsChange: (List<Long>) -> Unit,
    onNavigateToSelection: () -> Unit,
    onNavigateToResult: () -> Unit,
    onNavigateToCards: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var loadedCards by remember { mutableStateOf<List<Card>>(emptyList()) }

    // load cards by selected ids preserving order
    LaunchedEffect(selectedIds) {
        if (selectedIds.isEmpty()) {
            loadedCards = emptyList()
            return@LaunchedEffect
        }
        try {
            val all = CardRepository.getCards()
            loadedCards = selectedIds.mapNotNull { id -> all.find { it.cardId == id } }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (_: Exception) {
        }
    }

    val canCompare = selectedIds.size >= 2

    // 4 slots: first N filled, rest empty
    val slots: List<Card?> = (0 until MAX_SLOTS).map { loadedCards.getOrNull(it) }

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
                title = "Сравнить",
                actions = listOf(
                    TopBarAction(R.drawable.ic_reset) {
                        onSelectedIdsChange(emptyList())
                    }
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Выбери от 2 до 4\nквартир или домов",
                    style = Typography.bodyLarge,
                    color = Dark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2x2 grid of slots
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ComparisonSlot(
                            card = slots[0],
                            onClick = { onNavigateToSelection() },
                            onDelete = { onSelectedIdsChange(selectedIds.filterIndexed { i, _ -> i != 0 }) }
                        )
                        ComparisonSlot(
                            card = slots[1],
                            onClick = { onNavigateToSelection() },
                            onDelete = { onSelectedIdsChange(selectedIds.filterIndexed { i, _ -> i != 1 }) }
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ComparisonSlot(
                            card = slots[2],
                            onClick = { onNavigateToSelection() },
                            onDelete = { onSelectedIdsChange(selectedIds.filterIndexed { i, _ -> i != 2 }) }
                        )
                        ComparisonSlot(
                            card = slots[3],
                            onClick = { onNavigateToSelection() },
                            onDelete = { onSelectedIdsChange(selectedIds.filterIndexed { i, _ -> i != 3 }) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.width(256.dp)) {
                    Button(
                        text = "Сравнить",
                        onClick = { onNavigateToResult() },
                        enabled = canCompare
                    )
                }
            }
        }

        BottomBar(
            currentRoute = BottomNavItem.Comparison.route,
            onItemClick = { route ->
                when (route) {
                    BottomNavItem.Home.route -> onNavigateToCards()
                    BottomNavItem.Settings.route -> onNavigateToSettings()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ComparisonSlot(
    card: Card?,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val imageUrl = card?.imageUrls?.firstOrNull()
    Box(
        modifier = Modifier
            .size(120.dp)
            .background(LightBlue, RoundedCornerShape(10.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUrl != null -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
            card != null -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_house),
                    contentDescription = null,
                    tint = LightGray,
                    modifier = Modifier.size(32.dp)
                )
            }
            else -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    tint = Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (card != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.5f), CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bin),
                    contentDescription = null,
                    tint = Dark,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}