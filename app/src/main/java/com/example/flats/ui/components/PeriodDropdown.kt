package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.theme.*

@Composable
fun PeriodDropdown(
    selected: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    val options = listOf("day" to "день", "month" to "месяц", "year" to "год")
    var expanded by remember { mutableStateOf(initialExpanded) }

    val selectedLabel = options.firstOrNull { it.first == selected }?.second?.let { "/$it" } ?: "/месяц"

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .background(White, RoundedCornerShape(4.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = selectedLabel, style = Typography.bodyMedium, color = Gray)
            Icon(
                painter = painterResource(
                    id = if (expanded) R.drawable.ic_list_up else R.drawable.ic_list
                ),
                contentDescription = null,
                tint = Dark,
                modifier = Modifier.size(16.dp)
            )
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(4.dp),
                        spotColor = Color(0x0C000000),
                        ambientColor = Color(0x0C000000)
                    )
                    .background(White, RoundedCornerShape(4.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                options.forEach { (value, label) ->
                    Text(
                        text = label,
                        style = Typography.bodyMedium,
                        color = Gray,
                        modifier = Modifier
                            .clickable {
                                onSelect(value)
                                expanded = false
                            }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PeriodDropdownCollapsedPreview() {
    FlatsTheme {
        PeriodDropdown(selected = "month", onSelect = {})
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PeriodDropdownExpandedPreview() {
    FlatsTheme {
        Box(modifier = Modifier.height(150.dp)) {
            PeriodDropdown(selected = "month", onSelect = {}, initialExpanded = true)
        }
    }
}