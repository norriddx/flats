package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.Typography
import com.example.flats.ui.theme.White

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
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { expanded = !expanded }
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

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = White,
            shape = RoundedCornerShape(4.dp)
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            style = Typography.bodyMedium,
                            color = Gray
                        )
                    },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    },
                    interactionSource = remember { MutableInteractionSource() }
                )
            }
        }
    }
}