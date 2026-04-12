package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.LightGray

private val DotSize = 12.dp
private val LineHeight = 2.dp
private val Steps = 5

@Composable
fun StepSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    fun resolveStep(x: Float, width: Int): Int {
        return ((x / width) * (Steps - 1)).toInt().coerceIn(0, Steps - 1)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(DotSize)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onValueChange(resolveStep(offset.x, size.width))
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    onValueChange(resolveStep(change.position.x, size.width))
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(LineHeight)
                .align(Alignment.Center)
                .drawBehind {
                    val dotPx = DotSize.toPx()
                    val segmentWidth = (size.width - dotPx) / (Steps - 1)
                    for (i in 0 until Steps - 1) {
                        val startX = dotPx / 2 + i * segmentWidth
                        val endX = startX + segmentWidth
                        val color = if (i < value) Blue else LightGray
                        drawLine(
                            color = color,
                            start = Offset(startX, size.height / 2),
                            end = Offset(endX, size.height / 2),
                            strokeWidth = size.height
                        )
                    }
                }
        )

        Layout(
            content = {
                repeat(Steps) { index ->
                    Box(
                        modifier = Modifier
                            .size(DotSize)
                            .background(
                                color = if (index <= value) Blue else LightGray,
                                shape = CircleShape
                            )
                    )
                }
            }
        ) { measurables, constraints ->
            val dotSizePx = DotSize.roundToPx()
            val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
            val totalWidth = constraints.maxWidth
            val step = (totalWidth - dotSizePx) / (Steps - 1)
            layout(totalWidth, dotSizePx) {
                placeables.forEachIndexed { index, placeable ->
                    placeable.placeRelative(x = index * step, y = 0)
                }
            }
        }
    }
}