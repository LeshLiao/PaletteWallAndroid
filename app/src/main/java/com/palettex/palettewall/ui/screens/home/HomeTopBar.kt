package com.palettex.palettewall.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.palettex.palettewall.ui.components.utility.throttleClick
import com.palettex.palettewall.ui.screens.home.TopBarViewModel
import com.palettex.palettewall.ui.screens.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    topViewModel: TopBarViewModel,
    wallpaperViewModel: HomeViewModel,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onBannerHeightMeasured: (Dp) -> Unit,
    onClickSearch: () -> Unit
) {
    val density = LocalDensity.current
    val topTitle by topViewModel.topBarTitle.collectAsState()

    AnimatedVisibility(
        visible = topViewModel.isTopBarVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 2000)
        ) + fadeIn(animationSpec = tween(durationMillis = 2000)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 2000)
        ) + fadeOut(animationSpec = tween(durationMillis = 2000))
    ) {
        TopAppBar(
            modifier = Modifier.onSizeChanged { size ->
                val topHeight = with(density) { size.height.toDp() }
                onBannerHeightMeasured(topHeight)
            },
            title = {
                Text(
                    text = topTitle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().throttleClick {
                        wallpaperViewModel.setCurrentCatalog("Wallpapers")
                        wallpaperViewModel.scrollToTop()
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            actions = {
                if (topTitle == "PaletteX") {
                    IconButton(onClick = {
                        onClickSearch()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Box(modifier = Modifier.size(48.dp))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.primary
            ),
        )
    }
}