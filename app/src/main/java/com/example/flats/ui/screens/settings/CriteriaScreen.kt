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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CriteriaScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isSaving by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    var checklistItems by remember { mutableStateOf(listOf<Pair<Long?, String>>()) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var creatingChecklist by remember { mutableStateOf(false) }

    var scoreItems by remember { mutableStateOf(listOf<Pair<Long?, String>>()) }
    var editingScoreIndex by remember { mutableStateOf<Int?>(null) }
    var creatingScore by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val all = CardRepository.getCriteria()
            val checklist = all.filter { it.type == "checklist" }
                .map { it.criteriaId as Long? to it.name }
            checklistItems = checklist
            val score = all.filter { it.type == "score" }
                .map { it.criteriaId as Long? to it.name }
            scoreItems = score
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
            TopBar(title = "Критерии оценки", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
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
                            onDelete = {
                                checklistItems = checklistItems.toMutableList().also {
                                    it.removeAt(index)
                                }
                                hasChanges = true
                            }
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
                            onDelete = {
                                scoreItems = scoreItems.toMutableList().also {
                                    it.removeAt(index)
                                }
                                hasChanges = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
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
                        onClick = { /* TODO */ },
                        enabled = hasChanges && !isSaving && isLoaded,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        text = "Сохранить",
                        onClick = { /* TODO */ },
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
    }
}