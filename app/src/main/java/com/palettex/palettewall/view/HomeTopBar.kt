package com.palettex.palettewall.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.palettex.palettewall.view.utility.throttleClick
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    topViewModel: TopBarViewModel,
    wallpaperViewModel: WallpaperViewModel,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onBannerHeightMeasured: (Dp) -> Unit
) {
    val density = LocalDensity.current

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
                    text = "PaletteX",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().throttleClick {
                        wallpaperViewModel.setCurrentCatalog("Wallpapers")
                        wallpaperViewModel.scrollToTop()
                    }
                ) },
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    // Handle search button click here
                }) {
//                    Icon(
//                        imageVector = Icons.Default.Search,
//                        contentDescription = "Search",
//                        tint = Color.White // Set the color to white
//                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            ),
        )
    }

}