package com.example.flats.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.DarkBlue
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.LightGray
import com.example.flats.ui.theme.Typography
import com.example.flats.ui.theme.White
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.text.style.TextAlign
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.White

//import androidx.compose.ui.tooling.preview.Preview
//import com.example.flats.ui.theme.FlatsTheme

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
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
        colors = ButtonDefaults.buttonColors(
            containerColor         = backgroundColor,
            contentColor           = textColor,
            disabledContainerColor = LightGray,
            disabledContentColor   = Gray
        )
    ) {
        Text(
            text  = text,
            style = Typography.labelLarge
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) LightBlue else White

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(59.dp),
        shape = RoundedCornerShape(10.dp),
        interactionSource = interactionSource,
        border = BorderStroke(2.dp, Blue),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor   = Blue
        )
    ) {
        Text(
            text  = text,
            style = Typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

