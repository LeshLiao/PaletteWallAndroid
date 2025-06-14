package com.palettex.palettewall.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.palettex.palettewall.R
import com.palettex.palettewall.data.WallpaperDatabase
import com.palettex.palettewall.viewmodel.BillingViewModel
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import coil.compose.AsyncImagePainter
import com.palettex.palettewall.view.component.ImageSkeletonLoader


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScrollingContent(
    bottomOffset: Dp,
    topViewModel: TopBarViewModel,
    navController: NavController,
    wallpaperViewModel: WallpaperViewModel,
    billingViewModel: BillingViewModel,
    context: Context = LocalContext.current
) {
    val listState = rememberLazyListState()
    val lastScrollOffset = remember { mutableIntStateOf(0) }
    val appSettings by wallpaperViewModel.appSettings.collectAsState()
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()
    val currentCatalog by wallpaperViewModel.currentCatalog.collectAsState()
    val isRemoteConfigInitialized by wallpaperViewModel.isRemoteConfigInitialized.collectAsState()
    val isPremium by billingViewModel.isPremium.collectAsState()
    val isLoading by wallpaperViewModel.isLoading.collectAsStateWithLifecycle()

    // Add pull-to-refresh state
    val refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            wallpaperViewModel.pullRefreshCurrentCatalog()
        }
    )

    // Pre-initialize the AdMobBannerView for early initialization
    var showPopular by remember { mutableStateOf(false) }
    val scrollToTopTrigger by wallpaperViewModel.scrollToTopTrigger.collectAsState()
    val database = remember { WallpaperDatabase.getDatabase(context) }
    val dao = remember { database.likedWallpaperDao() }

    LaunchedEffect(isRemoteConfigInitialized) {
        wallpaperViewModel.setFullScreenWallpaper(wallpapers)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { currentScrollOffset ->
                val delta = currentScrollOffset - lastScrollOffset.intValue

                // Scroll handling for top bar visibility, TBC: Temporarily Stop hide it.
                // topViewModel.onScroll(delta.toFloat())

                // Check if scrolled to the top (first item and no offset)
                if (listState.firstVisibleItemIndex <= 1) {
                    topViewModel.showTopBar()
                }

                lastScrollOffset.intValue = currentScrollOffset
            }
    }

    // Add paging functionality
    LaunchedEffect(listState) {
        snapshotFlow {
            // Get the last visible item index
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // Get the total number of items currently rendered
            val totalItemsCount = listState.layoutInfo.totalItemsCount

            // Log for debugging
            // Log.d("GDT", "lastVisibleItemIndex=$lastVisibleItemIndex totalItemsCount=$totalItemsCount")

            // Check if we're near the end of the list
            // We need to consider if we're viewing the last 2-3 items
            lastVisibleItemIndex >= totalItemsCount - 3
        }
            .distinctUntilChanged()
            .collect { isNearBottom ->
                // Log.d("GDT", "isNearBottom=$isNearBottom isLoading=$isLoading wallpaperCount=${wallpapers.size}")

                // Only trigger loading more if:
                // 1. We're near the bottom
                // 2. We're not already loading
                // 3. We have already loaded some wallpapers (to avoid double-loading on init)
                if (isNearBottom && !isLoading && wallpapers.isNotEmpty()) {
                    wallpaperViewModel.loadMoreWallpapers()
                }
            }
    }

    LaunchedEffect(currentCatalog) {
        if (currentCatalog == "Wallpapers") {
            showPopular = true
        } else {
            showPopular = false
        }
    }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger) {
            listState.animateScrollToItem(0)
            wallpaperViewModel.setScrollToTopTrigger(false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        LazyColumn(state = listState) {
            item { Spacer(modifier = Modifier.height(80.dp).fillMaxWidth()) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { CatalogRow(wallpaperViewModel) }

            if (showPopular) {
                item {
                    Titles(
                        title = "Popular Wallpapers",
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 2.dp)
                    )
                }
                item {
                    PopularWallpapers(topViewModel, navController, wallpaperViewModel)
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            itemsIndexed(wallpapers.chunked(3)) { index, rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    rowItems.forEach { wallpaper ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .aspectRatio(0.5f)
                                .clickable {
                                    topViewModel.hideTopBar()
                                    navController.navigate("fullscreen/normal/${wallpaper.itemId}")
                                },
                        ) {
                            // Create the image painter
                            val imageUrl = wallpaper.imageList.firstOrNull {
                                it.type == "LD" && it.link.isNotEmpty()
                            }?.link ?: ""
                            val painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .size(Size.ORIGINAL)
                                    .build()
                            )

                            // Check the state of the painter
                            val painterState = painter.state

                            // Show skeleton loader while loading
                            if (painterState is AsyncImagePainter.State.Loading ||
                                painterState is AsyncImagePainter.State.Error) {
                                ImageSkeletonLoader(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Show the image
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (!wallpaper.freeDownload) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(6.dp)
                                ) {
                                    Image(
                                        painterResource(R.drawable.diamond),
                                        contentDescription = "",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (rowItems.size < 3) {
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Add loading indicator at the bottom when loading more content
            item {
                if (isLoading) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                // Bottom Area
                Spacer(modifier = Modifier.height(12.dp))
                Spacer(modifier = Modifier.height(bottomOffset))
            }
        }
        // Add pull-to-refresh indicator
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun Titles(title: String, modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = Color.White,
        )
        Text(
            text = "",
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = Color.White,
        )
    }
}