package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.Typography

data class FilterState(
    val priceStart: Float = 0f,
    val priceEnd: Float = 1f,
    val squareStart: Float = 0f,
    val squareEnd: Float = 1f
) {
    val isActive: Boolean
        get() = priceStart > 0f || priceEnd < 1f || squareStart > 0f || squareEnd < 1f
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    filter: FilterState,
    priceMin: Double?,
    priceMax: Double?,
    squareMin: Double?,
    squareMax: Double?,
    onFilterChange: (FilterState) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null
    ) {
        Column {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Фильтр",
                        style = Typography.headlineMedium,
                        color = Dark,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_reset),
                        contentDescription = null,
                        tint = Dark,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onFilterChange(FilterState()) }
                    )
                }

                // cost
                if (priceMin != null && priceMax != null && priceMin < priceMax) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Стоимость",
                            style = Typography.headlineSmall,
                            color = Dark,
                            modifier = Modifier.weight(1f)
                        )
                        val currentStart = (priceMin + filter.priceStart * (priceMax - priceMin)).toInt()
                        val currentEnd = (priceMin + filter.priceEnd * (priceMax - priceMin)).toInt()
                        Text(
                            text = "$currentStart - $currentEnd",
                            style = Typography.bodyMedium,
                            color = Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    RangeSlider(
                        valueStart = filter.priceStart,
                        valueEnd = filter.priceEnd,
                        onValueChange = { s, e ->
                            onFilterChange(filter.copy(priceStart = s, priceEnd = e))
                        }
                    )
                }

                // square
                if (squareMin != null && squareMax != null && squareMin < squareMax) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Площадь",
                            style = Typography.headlineSmall,
                            color = Dark,
                            modifier = Modifier.weight(1f)
                        )
                        val currentStart = (squareMin + filter.squareStart * (squareMax - squareMin)).toInt()
                        val currentEnd = (squareMin + filter.squareEnd * (squareMax - squareMin)).toInt()
                        Text(
                            text = "$currentStart - $currentEnd",
                            style = Typography.bodyMedium,
                            color = Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    RangeSlider(
                        valueStart = filter.squareStart,
                        valueEnd = filter.squareEnd,
                        onValueChange = { s, e ->
                            onFilterChange(filter.copy(squareStart = s, squareEnd = e))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .topShadow()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    text = "Применить",
                    onClick = onApply
                )
            }
        }
    }
}