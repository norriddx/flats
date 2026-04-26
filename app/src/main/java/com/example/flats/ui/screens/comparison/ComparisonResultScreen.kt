package com.example.flats.ui.screens.comparison

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.flats.R
import com.example.flats.data.CardRepository
import com.example.flats.data.model.Card
import com.example.flats.data.model.CardCriteriaScore
import com.example.flats.data.model.Criteria
import com.example.flats.ui.components.BottomBar
import com.example.flats.ui.components.BottomNavItem
import com.example.flats.ui.components.TopBar
import com.example.flats.ui.components.TopBarAction
import com.example.flats.ui.theme.BodyLargeBold
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.Green
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.LightGray
import com.example.flats.ui.theme.Red
import com.example.flats.ui.theme.Typography

private val ImageSize = 64.dp
private val ImageSpacing = 16.dp
private val LeftColumnWidth = 140.dp
private val OuterPadding = 20.dp
private val GapBeforeImages = 16.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComparisonResultScreen(
    selectedIds: List<Long>,
    onBack: () -> Unit,
    onNavigateToViewCard: (Long) -> Unit,
    onNavigateToCards: () -> Unit
) {
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    var criteria by remember { mutableStateOf<List<Criteria>>(emptyList()) }
    var scores by remember { mutableStateOf<List<CardCriteriaScore>>(emptyList()) }

    LaunchedEffect(selectedIds) {
        try {
            val all = CardRepository.getCards()
            cards = selectedIds.mapNotNull { id -> all.find { it.cardId == id } }
            criteria = CardRepository.getCriteria()
            scores = CardRepository.getAllScores()
                .filter { it.cardId in selectedIds }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (_: Exception) {
        }
    }

    val checklistCriteria = criteria.filter { it.type == "checklist" }

    val checkedTotalByCard: Map<Long, Int> = cards.associate { card ->
        card.cardId to checklistCriteria.count { c ->
            scores.any { it.cardId == card.cardId && it.criteriaId == c.criteriaId && it.value == 1.0 }
        }
    }

    val horizontalScroll = rememberScrollState()

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
                    TopBarAction(R.drawable.ic_info) { /* TODO */ },
                    TopBarAction(R.drawable.ic_reset) { onBack() }
                )
            )

            CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 80.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(OuterPadding + LeftColumnWidth + GapBeforeImages))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clipToBounds()
                                .horizontalScroll(horizontalScroll)
                                .padding(end = OuterPadding)
                        ) {
                            cards.forEachIndexed { index, card ->
                                if (index > 0) Spacer(modifier = Modifier.width(ImageSpacing))
                                Box(
                                    modifier = Modifier
                                        .size(ImageSize)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LightBlue)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) { onNavigateToViewCard(card.cardId) }
                                ) {
                                    val firstImage = card.imageUrls.firstOrNull()
                                    if (firstImage != null) {
                                        AsyncImage(
                                            model = firstImage,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.matchParentSize()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.matchParentSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_house),
                                                contentDescription = null,
                                                tint = LightGray,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .padding(start = OuterPadding)
                                .width(LeftColumnWidth)
                        ) {
                            Text(
                                text = "Чек-лист",
                                style = BodyLargeBold,
                                color = Dark
                            )
                        }
                        Spacer(modifier = Modifier.width(GapBeforeImages))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clipToBounds()
                                .horizontalScroll(horizontalScroll)
                                .padding(end = OuterPadding)
                        ) {
                            cards.forEachIndexed { index, card ->
                                if (index > 0) Spacer(modifier = Modifier.width(ImageSpacing))
                                Box(
                                    modifier = Modifier.width(ImageSize),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${checkedTotalByCard[card.cardId] ?: 0}/${checklistCriteria.size}",
                                        style = Typography.bodyMedium,
                                        color = Gray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    checklistCriteria.forEachIndexed { rowIndex, c ->
                        if (rowIndex > 0) Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = OuterPadding)
                                    .width(LeftColumnWidth)
                            ) {
                                Text(
                                    text = c.name,
                                    style = Typography.bodyMedium,
                                    color = Dark
                                )
                            }
                            Spacer(modifier = Modifier.width(GapBeforeImages))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clipToBounds()
                                    .horizontalScroll(horizontalScroll)
                                    .padding(end = OuterPadding),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                cards.forEachIndexed { index, card ->
                                    if (index > 0) Spacer(modifier = Modifier.width(ImageSpacing))
                                    val checked = scores.any {
                                        it.cardId == card.cardId &&
                                                it.criteriaId == c.criteriaId &&
                                                it.value == 1.0
                                    }
                                    Box(
                                        modifier = Modifier.width(ImageSize),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (checked) R.drawable.ic_check_box
                                                else R.drawable.ic_closw
                                            ),
                                            contentDescription = null,
                                            tint = if (checked) Green else Red,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        BottomBar(
            currentRoute = BottomNavItem.Comparison.route,
            onItemClick = { route ->
                when (route) {
                    BottomNavItem.Home.route -> onNavigateToCards()
                    BottomNavItem.Comparison.route -> onBack()
                    BottomNavItem.Settings.route -> { /* TODO */ }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}