package com.example.flats.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.DarkBlue
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.LightGray
import com.example.flats.ui.theme.Typography
import com.example.flats.ui.theme.White

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    maxLines: Int = Int.MAX_VALUE
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = when {
        !enabled  -> LightGray
        isPressed -> DarkBlue
        else      -> Blue
    }

    val textColor = when {
        !enabled -> Gray
        else     -> White
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(59.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        interactionSource = interactionSource,
        contentPadding = contentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor         = backgroundColor,
            contentColor           = textColor,
            disabledContainerColor = LightGray,
            disabledContentColor   = Gray
        )
    ) {
        Text(
            text = text,
            style = Typography.labelLarge,
            maxLines = maxLines
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    maxLines: Int = Int.MAX_VALUE
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed && enabled) LightBlue else White
    val borderColor = if (enabled) Blue else Gray
    val textColor = if (enabled) Blue else Gray

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(59.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        interactionSource = interactionSource,
        border = BorderStroke(2.dp, borderColor),
        contentPadding = contentPadding,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = White,
            disabledContentColor = Gray
        )
    ) {
        Text(
            text = text,
            style = Typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = maxLines
        )
    }
}