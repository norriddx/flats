package com.example.flats.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.data.CardRepository
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.CriteriaEditChip
import com.example.flats.ui.components.EditDialogSheet
import com.example.flats.ui.components.SecondaryButton
import com.example.flats.ui.components.TopBar
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Typography
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.example.flats.ui.components.NotificationSheet
import com.example.flats.ui.theme.LightBlue
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
private fun CriteriaShimmer() {
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
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0.4f, 0.5f, 0.35f, 0.45f, 0.55f).forEach { fraction ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(shimmerBrush)
                    )
                }
            }
        }
    }
}

private suspend fun saveCriteria(
    initialChecklist: List<Pair<Long?, String>>? = null,
    currentChecklist: List<Pair<Long?, String>>? = null,
    initialScore: List<Pair<Long?, String>>? = null,
    currentScore: List<Pair<Long?, String>>? = null,
    type: String
) {
    val initial = if (type == "checklist") initialChecklist!! else initialScore!!
    val current = if (type == "checklist") currentChecklist!! else currentScore!!

    val initialIds = initial.mapNotNull { it.first }.toSet()
    val currentIds = current.mapNotNull { it.first }.toSet()

    // deactivate removed
    val removedIds = initialIds - currentIds
    removedIds.forEach { id ->
        CardRepository.deactivateCriteria(id)
    }

    // process new and renamed
    current.forEach { (id, name) ->
        if (id == null) {
            // new — try to find existing by name+type
            val existing = CardRepository.findCriteriaByNameAndType(name, type)
            if (existing != null) {
                if (!existing.isActive) {
                    CardRepository.activateCriteria(existing.criteriaId)
                }
            } else {
                CardRepository.insertCriteria(name, type)
            }
        } else {
            // existing — check if name changed
            val initialName = initial.find { it.first == id }?.second
            if (initialName != null && initialName != name) {
                CardRepository.updateCriteriaName(id, name)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CriteriaScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var isSaving by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    var initialChecklist by remember { mutableStateOf(listOf<Pair<Long?, String>>()) }
    var initialScore by remember { mutableStateOf(listOf<Pair<Long?, String>>()) }

    var checklistItems by remember { mutableStateOf(listOf<Pair<Long?, String>>()) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var creatingChecklist by remember { mutableStateOf(false) }

    var scoreItems by remember { mutableStateOf(listOf<Pair<Long?, String>>()) }
    var editingScoreIndex by remember { mutableStateOf<Int?>(null) }
    var creatingScore by remember { mutableStateOf(false) }

    var deletingChecklistIndex by remember { mutableStateOf<Int?>(null) }
    var deletingScoreIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        try {
            val all = CardRepository.getCriteria()
            val checklist = all.filter { it.type == "checklist" }
                .map { it.criteriaId as Long? to it.name }
            checklistItems = checklist
            initialChecklist = checklist
            val score = all.filter { it.type == "score" }
                .map { it.criteriaId as Long? to it.name }
            scoreItems = score
            initialScore = score
            isLoaded = true
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Ошибка загрузки", Toast.LENGTH_SHORT).show()
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
            TopBar(
                title = "Критерии оценки",
                onBack = onBack,
                modifier = if (scrollState.value > 0) Modifier.bottomShadow() else Modifier
            )

            if (!isLoaded) {
                Box(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        CriteriaShimmer()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // checklist
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Чек-лист",
                            style = Typography.headlineSmall,
                            color = Dark,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plus),
                            contentDescription = null,
                            tint = Dark,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { creatingChecklist = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        checklistItems.forEachIndexed { index, item ->
                            CriteriaEditChip(
                                value = item.second,
                                onEdit = { editingIndex = index },
                                onDelete = { deletingChecklistIndex = index }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // score
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Критерии оценки",
                            style = Typography.headlineSmall,
                            color = Dark,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plus),
                            contentDescription = null,
                            tint = Dark,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { creatingScore = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        scoreItems.forEachIndexed { index, item ->
                            CriteriaEditChip(
                                value = item.second,
                                onEdit = { editingScoreIndex = index },
                                onDelete = { deletingScoreIndex = index }
                            )
                        }
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
                            checklistItems = initialChecklist
                            scoreItems = initialScore
                            hasChanges = false
                        },
                        enabled = hasChanges && !isSaving && isLoaded,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        text = "Сохранить",
                        onClick = {
                            isSaving = true
                            scope.launch {
                                try {
                                    saveCriteria(
                                        initialChecklist = initialChecklist,
                                        currentChecklist = checklistItems,
                                        type = "checklist"
                                    )
                                    saveCriteria(
                                        initialScore = initialScore,
                                        currentScore = scoreItems,
                                        type = "score"
                                    )
                                    CardRepository.recalculateWeights()
                                    val all = CardRepository.getCriteria()
                                    val newChecklist = all.filter { it.type == "checklist" }
                                        .map { it.criteriaId as Long? to it.name }
                                    val newScore = all.filter { it.type == "score" }
                                        .map { it.criteriaId as Long? to it.name }
                                    checklistItems = newChecklist
                                    initialChecklist = newChecklist
                                    scoreItems = newScore
                                    initialScore = newScore
                                    hasChanges = false
                                } catch (e: kotlinx.coroutines.CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    Toast.makeText(context, e.message ?: "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        enabled = hasChanges && !isSaving && isLoaded,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        editingIndex?.let { index ->
            EditDialogSheet(
                title = "Редактирование",
                initialValue = checklistItems[index].second,
                buttonText = "Сохранить",
                onSave = { newValue ->
                    checklistItems = checklistItems.toMutableList().also {
                        it[index] = it[index].first to newValue
                    }
                    hasChanges = true
                    editingIndex = null
                },
                onDismiss = { editingIndex = null }
            )
        }

        if (creatingChecklist) {
            EditDialogSheet(
                title = "Создание",
                initialValue = "",
                buttonText = "Сохранить",
                onSave = { newValue ->
                    if (newValue.isNotBlank()) {
                        checklistItems = checklistItems + (null to newValue)
                        hasChanges = true
                    }
                    creatingChecklist = false
                },
                onDismiss = { creatingChecklist = false }
            )
        }

        editingScoreIndex?.let { index ->
            EditDialogSheet(
                title = "Редактирование",
                initialValue = scoreItems[index].second,
                buttonText = "Сохранить",
                onSave = { newValue ->
                    scoreItems = scoreItems.toMutableList().also {
                        it[index] = it[index].first to newValue
                    }
                    hasChanges = true
                    editingScoreIndex = null
                },
                onDismiss = { editingScoreIndex = null }
            )
        }

        if (creatingScore) {
            EditDialogSheet(
                title = "Создание",
                initialValue = "",
                buttonText = "Сохранить",
                onSave = { newValue ->
                    if (newValue.isNotBlank()) {
                        scoreItems = scoreItems + (null to newValue)
                        hasChanges = true
                    }
                    creatingScore = false
                },
                onDismiss = { creatingScore = false }
            )
        }

        deletingChecklistIndex?.let { index ->
            NotificationSheet(
                title = "Удаление критерия",
                text = "Удалить критерий? Критерий скроется из интерфейса, но останется в базе — сохранятся его вес и оценки в старых карточках. Если создашь критерий с тем же названием, он восстановится с сохранёнными данными. Новые карточки не будут содержать этот критерий.",
                buttonText = "Удалить",
                onButtonClick = {
                    checklistItems = checklistItems.toMutableList().also {
                        it.removeAt(index)
                    }
                    hasChanges = true
                    deletingChecklistIndex = null
                },
                onDismiss = { deletingChecklistIndex = null }
            )
        }

        deletingScoreIndex?.let { index ->
            NotificationSheet(
                title = "Удаление критерия",
                text = "Удалить критерий? Критерий скроется из интерфейса, но останется в базе — сохранятся его вес и оценки в старых карточках. Если создашь критерий с тем же названием, он восстановится с сохранёнными данными. Новые карточки не будут содержать этот критерий.",
                buttonText = "Удалить",
                onButtonClick = {
                    scoreItems = scoreItems.toMutableList().also {
                        it.removeAt(index)
                    }
                    hasChanges = true
                    deletingScoreIndex = null
                },
                onDismiss = { deletingScoreIndex = null }
            )
        }
    }
}