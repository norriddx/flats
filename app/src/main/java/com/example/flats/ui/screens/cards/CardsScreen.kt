package com.example.flats.ui.screens.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flats.R
import com.example.flats.ui.components.BottomBar
import com.example.flats.ui.components.BottomNavItem
import com.example.flats.ui.components.FAB
import com.example.flats.ui.components.TextField
import com.example.flats.ui.components.TopBar
import com.example.flats.ui.components.TopBarAction
import com.example.flats.ui.theme.Gray
import com.example.flats.ui.theme.Typography

@Composable
fun CardsScreen(
    onNavigateToComparison: () -> Unit,
    onNavigateToCreateCard: () -> Unit,
    onLogout: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            TopBar(
                title = "Мои просмотры",
                actions = listOf(
                    TopBarAction(R.drawable.ic_archive) { },
                    TopBarAction(R.drawable.ic_favourite) { }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Найти",
                modifier = Modifier.padding(horizontal = 20.dp),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Gray
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Gray
                    )
                }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Пусто :(\nЧтобы добавить первую\n квартиру, нажми +",
                    style = Typography.bodyLarge,
                    color = Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        FAB(
            onClick = onNavigateToCreateCard,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        )

        BottomBar(
            currentRoute = BottomNavItem.Home.route,
            onItemClick = { route ->
                when (route) {
                    BottomNavItem.Comparison.route -> onNavigateToComparison()
                    BottomNavItem.Settings.route -> { /* TODO */ }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        )
    }
}