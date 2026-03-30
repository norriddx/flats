package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.LightGray
import kotlin.math.roundToInt

private val ThumbSize = 12.dp
private val TrackHeight = 2.dp

@Composable
fun RangeSlider(
    valueStart: Float, // 0f..1f
    valueEnd: Float,   // 0f..1f
    onValueChange: (start: Float, end: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var trackWidthPx by remember { mutableFloatStateOf(0f) }
    val thumbSizePx = ThumbSize.value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ThumbSize)
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
    ) {
        // line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TrackHeight)
                .align(Alignment.Center)
                .drawBehind {
                    val usable = trackWidthPx - thumbSizePx
                    val startX = thumbSizePx / 2 + valueStart * usable
                    val endX = thumbSizePx / 2 + valueEnd * usable

                    // before startX
                    drawLine(LightGray, Offset(thumbSizePx / 2, size.height / 2), Offset(startX, size.height / 2), strokeWidth = size.height)
                    // between
                    drawLine(Blue, Offset(startX, size.height / 2), Offset(endX, size.height / 2), strokeWidth = size.height)
                    // after endX
                    drawLine(LightGray, Offset(endX, size.height / 2), Offset(trackWidthPx - thumbSizePx / 2, size.height / 2), strokeWidth = size.height)
                }
        )

        // left thumb
        Box(
            modifier = Modifier
                .offset {
                    val usable = trackWidthPx - thumbSizePx
                    IntOffset((valueStart * usable).roundToInt(), 0)
                }
                .size(ThumbSize)
                .background(Blue, CircleShape)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        val usable = trackWidthPx - thumbSizePx
                        val newStart = (valueStart + dragAmount / usable).coerceIn(0f, valueEnd)
                        onValueChange(newStart, valueEnd)
                    }
                }
        )

        // right thumb
        Box(
            modifier = Modifier
                .offset {
                    val usable = trackWidthPx - thumbSizePx
                    IntOffset((valueEnd * usable).roundToInt(), 0)
                }
                .size(ThumbSize)
                .background(Blue, CircleShape)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        val usable = trackWidthPx - thumbSizePx
                        val newEnd = (valueEnd + dragAmount / usable).coerceIn(valueStart, 1f)
                        onValueChange(valueStart, newEnd)
                    }
                }
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RangeSliderPreview() {
//    RangeSlider(valueStart = 0.15f, valueEnd = 0.65f, onValueChange = { _, _ -> })
//}