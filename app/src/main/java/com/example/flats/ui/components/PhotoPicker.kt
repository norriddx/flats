package com.example.flats.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.flats.R
import com.example.flats.ui.theme.Dark
import com.example.flats.ui.theme.LightBlue
import com.example.flats.ui.theme.Gray

private val CellSize = 84.dp
private val CellShape = RoundedCornerShape(10.dp)

@Composable
fun PhotoPicker(
    images: List<Uri>,
    onAdd: (List<Uri>) -> Unit,
    onDelete: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> if (uris.isNotEmpty()) onAdd(uris) }

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        images.forEach { uri ->
            Box(
                modifier = Modifier
                    .size(CellSize)
                    .clip(CellShape)
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.5f), CircleShape)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onDelete(uri) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bin),
                        contentDescription = null,
                        tint = Dark,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(CellSize)
                .background(LightBlue, CellShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                tint = Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}