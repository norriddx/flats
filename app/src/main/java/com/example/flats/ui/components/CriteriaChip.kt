package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.Typography

@Composable
fun CriteriaChip(
    value: String,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editing by remember { mutableStateOf(value.isEmpty()) }
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier
            .background(LightBlue, RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!editing) editing = true
            },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (editing) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = Typography.bodyLarge.copy(color = Dark),
                cursorBrush = SolidColor(Dark),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { if (!it.isFocused) editing = false }
            )
        } else {
            Text(
                text = value,
                style = Typography.bodyLarge,
                color = Dark
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_bin),
                contentDescription = null,
                tint = Dark,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDelete() }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CriteriaChipDisplayPreview() {
//    CriteriaChip(value = "Нет шума", onValueChange = {}, onDelete = {})
//}

