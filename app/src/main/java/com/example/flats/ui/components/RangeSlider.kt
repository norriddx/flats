package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.LightGray
import kotlin.math.roundToInt

private val ThumbSize = 17.dp
private val TrackHeight = 2.dp

@Composable
fun RangeSlider(
    valueStart: Float,
    valueEnd: Float,
    onValueChange: (start: Float, end: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val thumbSizePx = with(density) { ThumbSize.toPx() }
    var trackWidthPx by remember { mutableFloatStateOf(0f) }

    val currentStart by rememberUpdatedState(valueStart)
    val currentEnd by rememberUpdatedState(valueEnd)
    val currentOnChange by rememberUpdatedState(onValueChange)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ThumbSize)
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TrackHeight)
                .align(Alignment.Center)
                .drawBehind {
                    val usable = trackWidthPx - thumbSizePx
                    if (usable <= 0f) return@drawBehind
                    val startX = thumbSizePx / 2 + valueStart * usable
                    val endX = thumbSizePx / 2 + valueEnd * usable

                    drawLine(LightGray, Offset(thumbSizePx / 2, size.height / 2), Offset(startX, size.height / 2), strokeWidth = size.height)
                    drawLine(Blue, Offset(startX, size.height / 2), Offset(endX, size.height / 2), strokeWidth = size.height)
                    drawLine(LightGray, Offset(endX, size.height / 2), Offset(trackWidthPx - thumbSizePx / 2, size.height / 2), strokeWidth = size.height)
                }
        )

        Box(
            modifier = Modifier
                .offset {
                    val usable = trackWidthPx - thumbSizePx
                    IntOffset((valueStart * usable).roundToInt(), 0)
                }
                .size(ThumbSize)
                .background(Blue, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val usable = trackWidthPx - thumbSizePx
                        if (usable <= 0f) return@detectDragGestures
                        val newStart = (currentStart + dragAmount.x / usable).coerceIn(0f, currentEnd)
                        currentOnChange(newStart, currentEnd)
                    }
                }
        )

        Box(
            modifier = Modifier
                .offset {
                    val usable = trackWidthPx - thumbSizePx
                    IntOffset((valueEnd * usable).roundToInt(), 0)
                }
                .size(ThumbSize)
                .background(Blue, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val usable = trackWidthPx - thumbSizePx
                        if (usable <= 0f) return@detectDragGestures
                        val newEnd = (currentEnd + dragAmount.x / usable).coerceIn(currentStart, 1f)
                        currentOnChange(currentStart, newEnd)
                    }
                }
        )
    }
}