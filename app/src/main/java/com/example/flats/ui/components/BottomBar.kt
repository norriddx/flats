package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.Dark

sealed class BottomNavItem(val route: String, val iconRes: Int) {
    object Home : BottomNavItem("cards", R.drawable.ic_home)
    object Comparison : BottomNavItem("comparison", R.drawable.ic_comparison)
    object Settings : BottomNavItem("settings", R.drawable.ic_settings)
}

private fun Modifier.topShadow(
    color: Color = Color(0x19000000),
    blur: Dp = 10.dp,
    offsetY: Dp = (-2).dp
): Modifier = drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.isAntiAlias = true
        frameworkPaint.color = android.graphics.Color.TRANSPARENT
        frameworkPaint.setShadowLayer(blur.toPx(), 0f, offsetY.toPx(), color.toArgb())
        canvas.drawRect(0f, 0f, size.width, size.height, paint)
    }
}

@Composable
fun BottomBar(
    currentRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Comparison,
        BottomNavItem.Settings
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .topShadow()
            .background(Color.White)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onItemClick(item.route) }
                        .padding(top = 12.dp, bottom = 6.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (selected) Blue else Dark
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun BottomBarPreview() {
//    BottomBar(currentRoute = "cards", onItemClick = {})
//}