package com.palettex.palettewall.ui.screens.home

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.ImageLoader
import com.palettex.palettewall.PaletteWallApplication
import com.palettex.palettewall.R
import com.palettex.palettewall.ui.components.getImageSourceFromAssets
import com.palettex.palettewall.ui.components.ProgressiveImageLoaderBest
import com.palettex.palettewall.ui.components.utility.throttleClick
import com.palettex.palettewall.ui.screens.seemore.SeeMoreViewModel
import com.palettex.palettewall.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SeeMorePage(
    catalog: String,
    wallpaperViewModel: HomeViewModel,
    outerNav: NavController
) {
    val context = LocalContext.current
    val seeMoreViewModel: SeeMoreViewModel = hiltViewModel()

    val listState = rememberLazyListState()
    val imageLoader = remember { ImageLoader(context) }
    val imageCacheList = PaletteWallApplication.imageCacheList

    val wallpapers by seeMoreViewModel.wallpapers.collectAsStateWithLifecycle()
    val isLoading by seeMoreViewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by seeMoreViewModel.isRefreshing.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { seeMoreViewModel.refreshWallpapers() }
    )

    // Initialize catalog on first composition
    LaunchedEffect(catalog) {
        seeMoreViewModel.initializeCatalog(catalog)
    }

    // Detect when user scrolls near the bottom to load more
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount

            // Load more when user is within 3 items from the bottom
            lastVisibleItem != null &&
                    lastVisibleItem.index >= totalItems - 9 &&
                    !isLoading
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            seeMoreViewModel.fetchSpecificWallpapers()
        }
    }

    Scaffold(
        modifier = Modifier.pullRefresh(pullRefreshState),
        topBar = {},
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                state = listState
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(16.dp).throttleClick {
                                    outerNav.popBackStack()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        val title = catalog.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            text = title,
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Grid items
                itemsIndexed(
                    items = wallpapers.chunked(3),
                    key = { index, _ -> index }
                ) { index, rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { wallpaper ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .aspectRatio(9f / 16f)
                                    .clickable {
                                        wallpaperViewModel.initFullScreenDataSourceByList(wallpapers)
                                        outerNav.navigate("fullscreen/${wallpaper.itemId}")
                                    },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF111111)
                                )
                            ) {
                                val imageUrl = wallpaper.imageList.firstOrNull {
                                    it.type == "LD" && it.link.isNotEmpty()
                                }?.link ?: ""

                                val blurImageUrl = wallpaper.imageList.firstOrNull {
                                    it.type == "BL" && it.link.isNotEmpty()
                                }?.link ?: ""

                                val imageSource = imageUrl.getImageSourceFromAssets(context, imageCacheList)
                                val blurSource = blurImageUrl.getImageSourceFromAssets(context, imageCacheList)

                                Box(modifier = Modifier.fillMaxSize()) {
                                    ProgressiveImageLoaderBest(
                                        blurImageUrl = blurSource,
                                        fullImageSource = imageSource,
                                        imageLoader = imageLoader
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
                        }

                        // Fill remaining space if row has less than 3 items
                        if (rowItems.size < 3) {
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Loading indicator at bottom
                item {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Pull-to-refresh indicator
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}