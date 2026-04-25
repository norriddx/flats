package com.example.flats.ui.screens.comparison

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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flats.data.CardRepository
import com.example.flats.data.model.Card
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.CardItemCompact
import com.example.flats.ui.components.TopBar
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.Typography

private const val MAX_SELECTION = 4

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
fun CardSelectionScreen(
    initialSelectedIds: List<Long>,
    onBack: () -> Unit,
    onSave: (List<Long>) -> Unit
) {
    var cards by remember { mutableStateOf<List<Card>?>(null) }
    var selectedIds by remember { mutableStateOf(initialSelectedIds.toSet()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            cards = CardRepository.getCards().filter { !it.isDraft }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Ошибка загрузки", Toast.LENGTH_SHORT).show()
            cards = emptyList()
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
        ) {
            TopBar(onBack = onBack)

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
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp)
                    ) {
                        items(3) {
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
                                        .background(shimmerBrush)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.4f)
                                        .height(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(shimmerBrush)
                                )
                            }
                        }
                    }
                }

                cards!!.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет доступных карточек",
                            style = Typography.bodyLarge,
                            color = Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp)
                    ) {
                        items(cards!!, key = { it.cardId }) { card ->
                            val isSelected = card.cardId in selectedIds
                            CardItemCompact(
                                card = card,
                                selected = isSelected,
                                onClick = {
                                    selectedIds = if (isSelected) {
                                        selectedIds - card.cardId
                                    } else {
                                        if (selectedIds.size < MAX_SELECTION) {
                                            selectedIds + card.cardId
                                        } else {
                                            selectedIds
                                        }
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }
            }

            // footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .topShadow()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    text = "Сохранить",
                    onClick = {
                        // preserve initial order, append new selections in list order
                        val ordered = mutableListOf<Long>()
                        initialSelectedIds.forEach { id ->
                            if (id in selectedIds) ordered.add(id)
                        }
                        cards?.forEach { c ->
                            if (c.cardId in selectedIds && c.cardId !in ordered) {
                                ordered.add(c.cardId)
                            }
                        }
                        onSave(ordered)
                    },
                    enabled = selectedIds.isNotEmpty()
                )
            }
        }
    }
}