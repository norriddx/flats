package com.example.flats.ui.screens.cards

import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.flats.R
import com.example.flats.data.CardRepository
import com.example.flats.data.model.Card
import com.example.flats.data.model.CardCriteriaScore
import com.example.flats.data.model.Criteria
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.PageIndicator
import com.example.flats.ui.components.SecondaryButton
import com.example.flats.ui.components.StepSlider
import com.example.flats.ui.theme.Blue
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
private fun BackButton(onBack: () -> Unit) {
    Box(
        modifier = Modifier
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
}

@Composable
private fun ShimmerContent(onBack: () -> Unit) {
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

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(shimmerBrush)
            )
            Box(modifier = Modifier.align(Alignment.TopStart)) {
                BackButton(onBack)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )
        }
    }
}

@Composable
fun ViewCardScreen(
    cardId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    var card by remember { mutableStateOf<Card?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    var isArchiving by remember { mutableStateOf(false) }
    var isRestoring by remember { mutableStateOf(false) }
    var shouldNavigateBack by remember { mutableStateOf(false) }
    var criteria by remember { mutableStateOf<List<Criteria>>(emptyList()) }
    var scores by remember { mutableStateOf<List<CardCriteriaScore>>(emptyList()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(cardId) {
        try {
            val loadedCard = CardRepository.getCardById(cardId)
            val loadedCriteria = CardRepository.getCriteriaForCard(cardId)
            val loadedScores = CardRepository.getAllScores().filter { it.cardId == cardId }
            criteria = loadedCriteria
            scores = loadedScores
            card = loadedCard
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

                        // name
                        Text(
                            text = currentCard.name,
                            style = Typography.headlineLarge,
                            color = Dark
                        )

                        // address
                        if (currentCard.address != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location),
                                    contentDescription = null,
                                    tint = Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentCard.address,
                                    style = Typography.bodyMedium,
                                    color = Gray
                                )
                            }
                        }

                        // desc
                        if (currentCard.description != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = currentCard.description,
                                style = Typography.bodyLarge,
                                color = Dark
                            )
                        }

                        // square
                        if (currentCard.square != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "${currentCard.square.toInt()} кв. м.",
                                style = Typography.bodyLarge,
                                color = Dark
                            )
                        }

                        // price
                        if (currentCard.price != null) {
                            val periodLabel = when (currentCard.pricePeriod) {
                                "day"   -> "/день"
                                "month" -> "/месяц"
                                "year"  -> "/год"
                                else    -> ""
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(Typography.bodyLarge.toSpanStyle().copy(color = Dark)) {
                                            append("${currentCard.price.toInt()}")
                                        }
                                        if (periodLabel.isNotEmpty()) {
                                            append(" ")
                                            withStyle(Typography.bodyMedium.toSpanStyle().copy(color = Gray)) {
                                                append(periodLabel)
                                            }
                                        }
                                    }
                                )
                                if (currentCard.utilitiesIncluded) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(LightBlue, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "+ ЖКУ",
                                            style = Typography.bodyMedium,
                                            color = Gray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // check list
                        val checklistCriteria = criteria.filter { it.type == "checklist" }
                        val checkedIds = scores
                            .filter { it.value == 1.0 }
                            .map { it.criteriaId }
                            .toSet()

                        if (checklistCriteria.isNotEmpty()) {
                            Text(
                                text = "Чек-лист",
                                style = Typography.headlineSmall,
                                color = Dark
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val left = checklistCriteria.filterIndexed { i, _ -> i % 2 == 0 }
                            val right = checklistCriteria.filterIndexed { i, _ -> i % 2 == 1 }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    left.forEach { item ->
                                        ChecklistItemReadOnly(
                                            item = item,
                                            checked = item.criteriaId in checkedIds
                                        )
                                    }
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    right.forEach { item ->
                                        ChecklistItemReadOnly(
                                            item = item,
                                            checked = item.criteriaId in checkedIds
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // criteria
                        val scoreCriteria = criteria.filter { it.type == "score" }

                        if (scoreCriteria.isNotEmpty()) {
                            Text(
                                text = "Критерии оценки",
                                style = Typography.headlineSmall,
                                color = Dark
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                scoreCriteria.forEach { item ->
                                    val score = scores.find { it.criteriaId == item.criteriaId }
                                    val value = (score?.value?.toInt() ?: 1) - 1
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = item.name,
                                            style = Typography.bodyLarge,
                                            color = Dark,
                                            modifier = Modifier.weight(1f)
                                        )
                                        StepSlider(
                                            value = value,
                                            onValueChange = {},
                                            enabled = false,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // contacts
                        if (!currentCard.contact.isNullOrBlank()) {
                            Text(
                                text = "Контакты",
                                style = Typography.headlineSmall,
                                color = Dark
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentCard.contact,
                                    style = Typography.bodyLarge,
                                    color = Dark
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_copy),
                                    contentDescription = null,
                                    tint = Dark,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                                                    as android.content.ClipboardManager
                                            clipboard.setPrimaryClip(
                                                android.content.ClipData.newPlainText("contact", currentCard.contact)
                                            )
                                            Toast.makeText(context, "Скопировано", Toast.LENGTH_SHORT).show()
                                        }
                                )
                            }
                        }

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
                    if (currentCard.isArchived) {
                        Button(
                            text = "Восстановить",
                            onClick = {
                                if (isRestoring) return@Button
                                isRestoring = true
                                scope.launch {
                                    try {
                                        CardRepository.unarchiveCard(cardId)
                                        CardRepository.recalculateWeights()
                                        shouldNavigateBack = true
                                    } catch (e: kotlinx.coroutines.CancellationException) {
                                    } catch (e: Exception) {
                                        isRestoring = false
                                        Toast.makeText(context, e.message ?: "Ошибка восстановления", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = !isRestoring,
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SecondaryButton(
                                text = "В архив",
                                onClick = {
                                    if (isArchiving) return@SecondaryButton
                                    isArchiving = true
                                    scope.launch {
                                        try {
                                            CardRepository.archiveCard(cardId)
                                            CardRepository.recalculateWeights()
                                            shouldNavigateBack = true
                                        } catch (e: kotlinx.coroutines.CancellationException) {
                                        } catch (e: Exception) {
                                            isArchiving = false
                                            Toast.makeText(context, e.message ?: "Ошибка архивации", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                enabled = !isArchiving,
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                text = "Редактировать",
                                onClick = onEdit,
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.align(Alignment.TopStart)) {
                BackButton(onBack)
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
                                CardRepository.recalculateWeights()
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
        } else {
            ShimmerContent(onBack = onBack)
        }
    }
}

@Composable
private fun ChecklistItemReadOnly(
    item: Criteria,
    checked: Boolean
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(
                id = if (checked) R.drawable.ic_check_box
                else R.drawable.ic_check_box_empty
            ),
            contentDescription = null,
            tint = Blue,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.name,
            style = Typography.bodyLarge,
            color = Dark,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}