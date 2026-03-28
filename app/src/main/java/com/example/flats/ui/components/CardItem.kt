package com.example.flats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.flats.R
import com.example.flats.data.model.Card
import com.example.flats.ui.theme.*

@Composable
fun CardItem(
    card: Card,
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val periodLabel = when (card.pricePeriod) {
        "day"   -> "/день"
        "month" -> "/месяц"
        "year"  -> "/год"
        else    -> ""
    }

    Column(
        modifier = modifier
            .width(372.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x0C000000),
                ambientColor = Color(0x0C000000)
            )
            .background(White, RoundedCornerShape(10.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(44.dp)
                    .background(
                        color = White.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // need to fix the design
                IconButton(onClick = onFavouriteClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_favourite),
                        contentDescription = null,
                        tint = if (card.isFavourite) Dark else White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = card.name,
                    style = Typography.headlineSmall
                )
                if (card.square != null) {
                    Text(
                        text = "(${card.square.toInt()} кв. м.)",
                        style = Typography.bodyMedium,
                        color = Gray
                    )
                }
            }

            if (card.address != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = null,
                        tint = Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = card.address,
                        style = Typography.bodyMedium,
                        color = Gray
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (card.price != null) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(Typography.headlineSmall.toSpanStyle().copy(color = Dark)) {
                            append("${card.price.toInt()}")
                        }
                        if (periodLabel.isNotEmpty()) {
                            append(" ")
                            withStyle(Typography.bodyMedium.toSpanStyle().copy(color = Gray)) {
                                append(periodLabel)
                            }
                        }
                    }
                )
            }

            if (card.utilitiesIncluded) {
                Box(
                    modifier = Modifier
                        .background(LightBlue, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "+ ЖКУ", style = Typography.bodyMedium, color = Gray)
                }
            }

            if (card.isDraft) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(Gray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "Черновик", style = Typography.bodyMedium, color = LightBlue)
                }
            }
        }
    }
}

//@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
//@Composable
//fun CardItemPreview() {
//    FlatsTheme {
//        CardItem(
//            card = Card(
//                name = "Квартира №1",
//                address = "Нижний Новгород, ул. Белинского, 49",
//                price = 30000.0,
//                square = 28.0,
//                isFavourite = false,
//                isDraft = true,
//                pricePeriod = "month",
//                utilitiesIncluded = true
//            ),
//            onFavouriteClick = {}
//        )
//    }
//}