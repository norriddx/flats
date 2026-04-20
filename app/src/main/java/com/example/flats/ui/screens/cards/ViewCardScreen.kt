package com.example.flats.ui.screens.cards

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.flats.R
import com.example.flats.data.CardRepository
import com.example.flats.data.model.Card
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.PageIndicator
import com.example.flats.ui.components.SecondaryButton
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.LightGray
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
fun ViewCardScreen(
    cardId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    var card by remember { mutableStateOf<Card?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    var shouldNavigateBack by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(cardId) {
        try {
            card = CardRepository.getCardById(cardId)
        } catch (_: kotlinx.coroutines.CancellationException) {
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Ошибка загрузки", Toast.LENGTH_SHORT).show()
            shouldNavigateBack = true
        }
    }

    LaunchedEffect(shouldNavigateBack) {
        if (shouldNavigateBack) onBack()
    }

    val currentCard = card

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (currentCard != null) {
            val images = currentCard.imageUrls
            val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(1) })

            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) {
                        if (images.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(LightBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_house),
                                    contentDescription = null,
                                    tint = LightGray,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        } else {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                AsyncImage(
                                    model = images[page],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        if (images.size > 1) {
                            Spacer(modifier = Modifier.height(20.dp))
                            PageIndicator(
                                count = images.size,
                                selected = pagerState.currentPage,
                                activeColor = Gray,
                                inactiveColor = LightGray,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        } else {
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        Text(
                            text = currentCard.name,
                            style = Typography.headlineLarge,
                            color = Dark
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .topShadow()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SecondaryButton(
                            text = "В архив",
                            onClick = { /* TODO архив */ },
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            text = "Редактировать",
                            onClick = onEdit,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 68.dp, start = 20.dp)
                .size(44.dp)
                .background(Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow),
                contentDescription = null,
                tint = Dark,
                modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 68.dp, end = 20.dp)
                .size(44.dp)
                .background(Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (isDeleting) return@clickable
                    isDeleting = true
                    scope.launch {
                        try {
                            CardRepository.deleteCard(cardId)
                            shouldNavigateBack = true
                        } catch (e: kotlinx.coroutines.CancellationException) {
                        } catch (e: Exception) {
                            isDeleting = false
                            Toast.makeText(context, e.message ?: "Ошибка удаления", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
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