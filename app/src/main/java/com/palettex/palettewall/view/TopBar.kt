package com.palettex.palettewall.view

import TopBarViewModel
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TopBar(viewModel: TopBarViewModel) {
    AnimatedVisibility(
        visible = viewModel.isTopBarVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
    ) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // val (height, width) = LocalConfiguration.current.run { screenHeightDp.dp to screenWidthDp.dp }
                    Text(
                        // text = "PaletteWall$height $width",
                        text = "PaletteWall",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 20.sp,
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { /* Handle menu click */ }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            actions = {
                IconButton(onClick = { /* Handle search click */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent, // Set the background to transparent
                titleContentColor = Color.Black // Set the title color if needed
            )
        )
    }
}
