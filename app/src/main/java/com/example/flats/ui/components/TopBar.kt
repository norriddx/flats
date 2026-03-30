package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Typography

data class TopBarAction(val iconRes: Int, val onClick: () -> Unit)

@Composable
fun TopBar(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    actions: List<TopBarAction> = emptyList(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            // top bar h2 (with an arrow)
            Box(modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = null,
                    tint = Dark,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onBack() }
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = Typography.headlineMedium,
                        color = Dark
                    )
                }
            }

            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                actions.firstOrNull()?.let { action ->
                    Icon(
                        painter = painterResource(id = action.iconRes),
                        contentDescription = null,
                        tint = Dark,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { action.onClick() }
                    )
                }
            }
        } else {
            // top bar h1
            if (title != null) {
                Text(
                    text = title,
                    style = Typography.headlineLarge,
                    color = Dark,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                actions.forEach { action ->
                    Icon(
                        painter = painterResource(id = action.iconRes),
                        contentDescription = null,
                        tint = Dark,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { action.onClick() }
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TopBarMainPreview() {
//    TopBar(
//        title = "Мои просмотры",
//        actions = listOf(
//            TopBarAction(R.drawable.ic_archive) {},
//            TopBarAction(R.drawable.ic_favourite) {}
//        )
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TopBarDetailPreview() {
//    TopBar(
//        title = "Редактировать",
//        onBack = {},
//        actions = listOf(TopBarAction(R.drawable.ic_bin) {})
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TopBarBackOnlyPreview() {
//    TopBar(onBack = {})
//}