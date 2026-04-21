package com.example.flats.ui.screens.cards

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.data.CardRepository
import com.example.flats.data.model.Card
import com.example.flats.data.model.CardCriteriaScore
import com.example.flats.data.model.Criteria
import com.example.flats.ui.components.BottomBar
import com.example.flats.ui.components.BottomNavItem
import com.example.flats.ui.components.CardItem
import com.example.flats.ui.components.FAB
import com.example.flats.ui.components.FilterSheet
import com.example.flats.ui.components.FilterState
import com.example.flats.ui.components.TextField
import com.example.flats.ui.components.TopBar
import com.example.flats.ui.components.TopBarAction
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.Typography

private fun Modifier.bottomShadow(
    height: Dp = 12.dp,
    color: Color = Color(0x0A000000)
): Modifier = this.then(
    Modifier.drawBehind {
        val shadowPx = height.toPx()
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(color, Color.Transparent),
                startY = size.height,
                endY = size.height + shadowPx
            ),
            topLeft = Offset(0f, size.height),
            size = size.copy(height = shadowPx)
        )
    }
)

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardsScreen(
    onNavigateToComparison: () -> Unit,
    onNavigateToCreateCard: () -> Unit,
    onNavigateToViewCard: (Long) -> Unit,
    onNavigateToFavourites: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onLogout: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var cards by remember { mutableStateOf<List<Card>?>(null) }
    var toggleCardId by remember { mutableStateOf<Long?>(null) }
    var filter by remember { mutableStateOf(FilterState()) }
    var appliedFilter by remember { mutableStateOf(FilterState()) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var checklistCriteria by remember { mutableStateOf<List<Criteria>>(emptyList()) }
    var allScores by remember { mutableStateOf<List<CardCriteriaScore>>(emptyList()) }

    val savedIndex = rememberSaveable { mutableStateOf(0) }
    val savedOffset = rememberSaveable { mutableStateOf(0) }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = savedIndex.value,
        initialFirstVisibleItemScrollOffset = savedOffset.value
    )

    DisposableEffect(Unit) {
        onDispose {
            savedIndex.value = listState.firstVisibleItemIndex
            savedOffset.value = listState.firstVisibleItemScrollOffset
        }
    }

    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val focusManager = LocalFocusManager.current
    val imeVisible = WindowInsets.isImeVisible
    var scoreCriteria by remember { mutableStateOf<List<Criteria>>(emptyList()) }

    LaunchedEffect(imeVisible) {
        if (!imeVisible) focusManager.clearFocus()
    }

    LaunchedEffect(Unit) {
        try {
            cards = CardRepository.getCards()
        } catch (_: Exception) {
            cards = emptyList()
        }
    }

    LaunchedEffect(Unit) {
        try {
            val all = CardRepository.getCriteria()
            checklistCriteria = all.filter { it.type == "checklist" }
            scoreCriteria = all.filter { it.type == "score" }
        } catch (_: Exception) {}
    }

    LaunchedEffect(Unit) {
        try {
            allScores = CardRepository.getAllScores()
        } catch (_: Exception) {}
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

    var isFirstRun by remember { mutableStateOf(true) }
    LaunchedEffect(appliedFilter, searchQuery) {
        if (isFirstRun) {
            isFirstRun = false
        } else {
            listState.scrollToItem(0)
        }
    }

    val priceMin = cards?.mapNotNull { it.price }?.minOrNull()
    val priceMax = cards?.mapNotNull { it.price }?.maxOrNull()
    val squareMin = cards?.mapNotNull { it.square }?.minOrNull()
    val squareMax = cards?.mapNotNull { it.square }?.maxOrNull()

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
                title = "Мои просмотры",
                actions = listOf(
                    TopBarAction(R.drawable.ic_archive) { onNavigateToArchive() },
                    TopBarAction(R.drawable.ic_favourite) { onNavigateToFavourites() }
                )
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
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 120.dp)
                    ) {
                        items(3) {
                            ShimmerCardItem(brush = shimmerBrush)
                        }
                    }
                }

                cards!!.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 80.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "Найти",
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Gray
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_filter),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Gray
                                )
                            }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Пусто :(\nЧтобы добавить первую\n квартиру, нажми +",
                                style = Typography.bodyLarge,
                                color = Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isScrolled) Modifier.bottomShadow() else Modifier
                            )
                            .background(Color.White)
                            .padding(horizontal = 20.dp)
                            .padding(top = 16.dp, bottom = 16.dp)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "Найти",
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Gray
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_filter),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            filter = appliedFilter
                                            showFilterSheet = true
                                        },
                                    tint = Gray
                                )
                            }
                        )
                    }
                    val filteredCards = cards!!.filter { card ->
                        val matchesSearch = searchQuery.isBlank() ||
                                card.name.contains(searchQuery, ignoreCase = true)

                        val priceFilterActive = appliedFilter.priceStart > 0f || appliedFilter.priceEnd < 1f
                        val squareFilterActive = appliedFilter.squareStart > 0f || appliedFilter.squareEnd < 1f

                        val matchesPrice = if (!priceFilterActive || priceMin == null || priceMax == null || priceMin >= priceMax) {
                            true
                        } else if (card.price == null) {
                            false
                        } else {
                            val norm = ((card.price - priceMin) / (priceMax - priceMin)).toFloat()
                            norm in appliedFilter.priceStart..appliedFilter.priceEnd
                        }

                        val matchesSquare = if (!squareFilterActive || squareMin == null || squareMax == null || squareMin >= squareMax) {
                            true
                        } else if (card.square == null) {
                            false
                        } else {
                            val norm = ((card.square - squareMin) / (squareMax - squareMin)).toFloat()
                            norm in appliedFilter.squareStart..appliedFilter.squareEnd
                        }

                        val matchesChecklist = if (appliedFilter.checkedCriteriaIds.isEmpty()) {
                            true
                        } else {
                            val cardCriteriaIds = allScores
                                .filter { it.cardId == card.cardId && it.value == 1.0 }
                                .map { it.criteriaId }
                                .toSet()
                            appliedFilter.checkedCriteriaIds.all { it in cardCriteriaIds }
                        }

                        val matchesScores = if (appliedFilter.scoreValues.isEmpty()) {
                            true
                        } else {
                            appliedFilter.scoreValues.all { (criteriaId, minValue) ->
                                val score = allScores.find { it.cardId == card.cardId && it.criteriaId == criteriaId }
                                score != null && score.value >= (minValue + 1).toDouble()
                            }
                        }

                        matchesSearch && matchesPrice && matchesSquare && matchesChecklist && matchesScores
                    }
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 120.dp)
                    ) {
                        items(filteredCards, key = { it.cardId }) { card ->
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

        FAB(
            onClick = onNavigateToCreateCard,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        )

        BottomBar(
            currentRoute = BottomNavItem.Home.route,
            onItemClick = { route ->
                when (route) {
                    BottomNavItem.Comparison.route -> onNavigateToComparison()
                    BottomNavItem.Settings.route -> { /* TODO */ }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )

        if (showFilterSheet) {
            FilterSheet(
                filter = filter,
                priceMin = priceMin,
                priceMax = priceMax,
                squareMin = squareMin,
                squareMax = squareMax,
                checklistCriteria = checklistCriteria,
                scoreCriteria = scoreCriteria,
                onFilterChange = { filter = it },
                onApply = {
                    appliedFilter = filter
                    showFilterSheet = false
                },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}