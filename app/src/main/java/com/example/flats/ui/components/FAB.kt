package com.example.flats.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.theme.Blue
import com.example.flats.ui.theme.DarkBlue
import com.example.flats.ui.theme.LightGray
import com.example.flats.ui.theme.White

@Composable
fun FAB(
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

    FloatingActionButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(72.dp),
        shape = RoundedCornerShape(100.dp),
        containerColor = backgroundColor,
        contentColor = White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation  = 5.dp,
            pressedElevation  = 5.dp,
            focusedElevation  = 5.dp,
            hoveredElevation  = 5.dp
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_plus),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

