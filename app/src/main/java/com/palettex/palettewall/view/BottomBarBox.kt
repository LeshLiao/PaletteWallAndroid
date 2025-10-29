package com.palettex.palettewall.view

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.palettex.palettewall.R
import com.palettex.palettewall.data.PaletteRemoteConfig
import com.palettex.palettewall.viewmodel.AdManager
import com.palettex.palettewall.viewmodel.BillingViewModel
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel

@Composable
fun BottomBarBox(
    topViewModel: TopBarViewModel,
    wallpaperViewModel: WallpaperViewModel,
    navController: NavHostController,
    billingViewModel: BillingViewModel,
    context: Context = LocalContext.current,
    onHeightMeasured: (Dp) -> Unit
) {
    var adMobBannerView by remember { mutableStateOf<android.view.View?>(null) }
    val isRemoteConfigInitialized by wallpaperViewModel.isRemoteConfigInitialized.collectAsState()
    val isPremium by billingViewModel.isPremium.collectAsState()
    val density = LocalDensity.current

    LaunchedEffect(isRemoteConfigInitialized) {
        Log.d("GDT", "ScrollingContent isRemoteConfigInitialized=$isRemoteConfigInitialized")
        // Initialize the ad view only when remote config is ready
        if (isRemoteConfigInitialized) {
            adMobBannerView = AdManager.getOrCreateAd(context)
            AdManager.loadAdIfNeeded(wallpaperViewModel)
        }
    }

    AnimatedVisibility(
        visible = topViewModel.isTopBarVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 2000)
        ) + fadeIn(animationSpec = tween(durationMillis = 2000)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 2000)
        ) + fadeOut(animationSpec = tween(durationMillis = 2000))
    ) {
        val items = listOf("Home", "Carousel", "Favorite", "Settings")

        Column(
            modifier = Modifier
                .background(Color.Black)
                .onGloballyPositioned { coordinates ->
                    val navigationBarHeight = with(density) { coordinates.size.height.toDp() }
                    onHeightMeasured(navigationBarHeight)
                },
        ) {
            Spacer(modifier = Modifier.height(1.dp))
            if (!isPremium && PaletteRemoteConfig.shouldShowBannerAds()) {
                adMobBannerView?.let { adView ->
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { adView }
                    )
                }
            }

            NavigationBar(containerColor = Color.Black) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (item) {
                                    "Home" -> ImageVector.vectorResource(id = R.drawable.icon_home)
                                    "Carousel" -> ImageVector.vectorResource(id = R.drawable.icon_search_alt)
                                    "Favorite" -> ImageVector.vectorResource(id = R.drawable.icon_heart)
                                    "Settings" -> ImageVector.vectorResource(id = R.drawable.icon_setting)
                                    else -> ImageVector.vectorResource(id = R.drawable.ic_home)
                                },
                                contentDescription = item,
                                modifier = Modifier.size(25.dp), // Set the size of the icon
                                tint = Color.White // Set the icon color to white
                            )
                        },
                        selected = false,
                        onClick = {
                            navController.navigate(item) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                            when (item) {
                                "Home" -> {
                                    topViewModel.setTopBarTitle("PaletteX")
                                    wallpaperViewModel.setCurrentCatalog("Wallpapers")
                                    wallpaperViewModel.scrollToTop()
                                }

                                "Carousel" -> {
                                    topViewModel.setTopBarTitle("Color Picker")
                                }

                                "Favorite" -> {
                                    topViewModel.setTopBarTitle("Liked Collection")
                                }

                                "Settings" -> {
                                    topViewModel.setTopBarTitle("Settings")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}