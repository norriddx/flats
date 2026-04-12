package com.example.flats.ui.screens.cards

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
import com.example.flats.R
import com.example.flats.data.CardRepository
import com.example.flats.data.model.Card
import com.example.flats.data.model.CardCriteriaScore
import com.example.flats.data.model.Criteria
import com.example.flats.ui.components.Button
import com.example.flats.ui.components.PeriodDropdown
import com.example.flats.ui.components.PhotoPicker
import com.example.flats.ui.components.SecondaryButton
import com.example.flats.ui.components.StepSlider
import com.example.flats.ui.components.TextField
import com.example.flats.ui.components.TopBar
import com.example.flats.ui.components.TopBarAction
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.Dark
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
fun CreateCardScreen(
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var square by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var pricePeriod by remember { mutableStateOf("month") }
    var utilitiesIncluded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var scoreValues by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }
    var contact by remember { mutableStateOf("") }

    var shouldNavigateBack by remember { mutableStateOf(false) }

    LaunchedEffect(shouldNavigateBack) {
        if (shouldNavigateBack) onBack()
    }

    var criteria by remember { mutableStateOf<List<Criteria>>(emptyList()) }
    var checkedCriteriaIds by remember { mutableStateOf<Set<Long>>(emptySet()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            criteria = CardRepository.getCriteria()
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (_: Exception) {}
    }

    fun saveCard(isDraft: Boolean) {
        if (isSaving) return
        isSaving = true

        scope.launch {
            try {
                val imageUrl = images.firstOrNull()?.let { uri ->
                    CardRepository.uploadImage(context, uri)
                }

                val card = Card(
                    userId = CardRepository.currentUserId(),
                    name = name.ifBlank { "Без названия" },
                    address = address.ifBlank { null },
                    price = price.toDoubleOrNull(),
                    square = square.toDoubleOrNull(),
                    description = description.ifBlank { null },
                    contact = contact.ifBlank { null },
                    pricePeriod = if (price.isNotBlank()) pricePeriod else null,
                    utilitiesIncluded = utilitiesIncluded,
                    isDraft = isDraft,
                    imageUrl = imageUrl
                )

                val savedCard = CardRepository.insertCard(card)

                val checklistScores = checkedCriteriaIds.map { criteriaId ->
                    CardCriteriaScore(
                        cardId = savedCard.cardId,
                        criteriaId = criteriaId,
                        value = 1.0
                    )
                }

                val sliderScores = scoreValues.map { (criteriaId, value) ->
                    CardCriteriaScore(
                        cardId = savedCard.cardId,
                        criteriaId = criteriaId,
                        value = (value + 1).toDouble()
                    )
                }

                CardRepository.insertCardCriteriaScores(checklistScores + sliderScores)

                shouldNavigateBack = true
            } catch (e: kotlinx.coroutines.CancellationException) {
            } catch (e: Exception) {
                isSaving = false
                Toast.makeText(context, e.message ?: "Ошибка сохранения", Toast.LENGTH_SHORT).show()
            }
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
                title = "Создать просмотр",
                onBack = onBack,
                actions = listOf(TopBarAction(R.drawable.ic_bin) { onDelete() })
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                PhotoPicker(
                    images = images,
                    onAdd = { uris -> images = images + uris },
                    onDelete = { uri -> images = images - uri }
                )

                // name
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Название", style = Typography.headlineSmall, color = Dark)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Например, «Квартира 1»"
                )

                // address
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Адрес", style = Typography.headlineSmall, color = Dark)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = "Город, улица, дом, квартира"
                )

                // square
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Площадь", style = Typography.headlineSmall, color = Dark)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = square,
                    onValueChange = { square = it },
                    placeholder = "30 кв. м."
                )

                // cost
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "Стоимость", style = Typography.headlineSmall, color = Dark)
                    PeriodDropdown(
                        selected = pricePeriod,
                        onSelect = { pricePeriod = it }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    placeholder = "Например, «20 000»"
                )

                // utilities included
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (utilitiesIncluded) R.drawable.ic_check_box
                            else R.drawable.ic_check_box_empty
                        ),
                        contentDescription = null,
                        tint = Blue,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { utilitiesIncluded = !utilitiesIncluded }
                    )
                    Text(text = "Включены ЖКУ", style = Typography.bodyLarge, color = Dark)
                }

                // description
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Описание", style = Typography.headlineSmall, color = Dark)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Кратко опиши свои впечатления и мысли от просмотра",
                    singleLine = false,
                    height = 100.dp
                )

                // check list
                val checklistCriteria = criteria.filter { it.type == "checklist" }
                if (checklistCriteria.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Чек-лист", style = Typography.headlineSmall, color = Dark)
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
                                ChecklistItem(
                                    item = item,
                                    checked = item.criteriaId in checkedCriteriaIds,
                                    onToggle = {
                                        checkedCriteriaIds = if (item.criteriaId in checkedCriteriaIds) {
                                            checkedCriteriaIds - item.criteriaId
                                        } else {
                                            checkedCriteriaIds + item.criteriaId
                                        }
                                    }
                                )
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            right.forEach { item ->
                                ChecklistItem(
                                    item = item,
                                    checked = item.criteriaId in checkedCriteriaIds,
                                    onToggle = {
                                        checkedCriteriaIds = if (item.criteriaId in checkedCriteriaIds) {
                                            checkedCriteriaIds - item.criteriaId
                                        } else {
                                            checkedCriteriaIds + item.criteriaId
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // criteria
                val scoreCriteria = criteria.filter { it.type == "score" }
                if (scoreCriteria.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Критерии оценки", style = Typography.headlineSmall, color = Dark)
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        scoreCriteria.forEach { item ->
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
                                    value = scoreValues[item.criteriaId] ?: 0,
                                    onValueChange = { scoreValues = scoreValues + (item.criteriaId to it) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // contacts
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Контакты", style = Typography.headlineSmall, color = Dark)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = contact,
                    onValueChange = { contact = it },
                    placeholder = "Номер или ссылка на владельца"
                )

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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        text = "Сохранить как черновик",
                        onClick = { saveCard(isDraft = true) },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        text = "Сохранить",
                        onClick = { saveCard(isDraft = false) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChecklistItem(
    item: Criteria,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onToggle() }
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