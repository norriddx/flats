package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.LightGray

@Composable
fun PageIndicator(
    count: Int,
    selected: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (index == selected) Blue else LightGray,
                        shape = CircleShape
                    )
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PageIndicatorPreview() {
//    PageIndicator(count = 3, selected = 0)
//}