package com.palettex.palettewall.ui.screens.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.ImageLoader
import com.palettex.palettewall.PaletteWallApplication
import com.palettex.palettewall.R
import com.palettex.palettewall.data.local.database.WallpaperDatabase
import com.palettex.palettewall.data.remote.dto.CatalogConfig
import com.palettex.palettewall.ui.components.AutoScrollImagePager
import com.palettex.palettewall.ui.components.AutoScrollImagePagerSkeleton
import com.palettex.palettewall.ui.components.Material3Carousel
import com.palettex.palettewall.ui.components.ProgressiveImageLoaderBest
import com.palettex.palettewall.ui.components.RowWallpapers
import com.palettex.palettewall.ui.components.Titles
import com.palettex.palettewall.ui.components.TitlesSkeleton
import com.palettex.palettewall.ui.components.getImageSourceFromAssets
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainPageContent(
    bottomOffset: Dp,
    topViewModel: TopBarViewModel,
    outerNav: NavController,
    navController: NavController,
    wallpaperViewModel: HomeViewModel,
    billingViewModel: BillingViewModel,
    context: Context = LocalContext.current
) {
    val listState = rememberLazyListState()
    val lastScrollOffset = remember { mutableIntStateOf(0) }
    val imageLoader = remember { ImageLoader(context) }
    val appSettings by wallpaperViewModel.appSettings.collectAsState()
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()
    val currentCatalog by wallpaperViewModel.currentCatalog.collectAsState()
    val isRemoteConfigInitialized by wallpaperViewModel.isRemoteConfigInitialized.collectAsState()
    val isLoading by wallpaperViewModel.isLoading.collectAsStateWithLifecycle()
    val catalogConfigs by wallpaperViewModel.catalogConfigs.collectAsState()
    val catalogWallpapers by wallpaperViewModel.catalogWallpapers.collectAsState()
    val imageCacheList = PaletteWallApplication.imageCacheList
    val boards by wallpaperViewModel.boards.collectAsState()

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

    LaunchedEffect(isRemoteConfigInitialized) {
        wallpaperViewModel.setFullScreenWallpaper(wallpapers)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { currentScrollOffset ->
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
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 12
        }
        .distinctUntilChanged()
        .collect { isNearBottom ->
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
        LazyColumn(
            state = listState,
            modifier = Modifier.testTag("wallpaper_list")
        ) {

            item { Spacer(modifier = Modifier.height(80.dp).fillMaxWidth()) }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                if (boards.isEmpty() || isLoading) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp, start = 8.dp, end = 8.dp),
                    ) {
                        AutoScrollImagePagerSkeleton()
                    }
                } else {
                    AutoScrollImagePager(
                        images = boards.map { it.photoUrl },
                        titles = boards.map { it.title },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 2.dp, start = 8.dp, end = 8.dp),
                        onItemClick = { index ->
                            val board = boards[index]
                            when (board.action) {
                                "NAVIGATION_HALLOWEEN" -> {
                                    outerNav.navigate("see_more/halloween")
                                }
                                "NAVIGATION_CHRISTMAS" -> {
                                    outerNav.navigate("see_more/christmas")
                                }
                                else -> {}
                            }
                        }
                    )
                }
            }

            val newCatalogConfig = CatalogConfig(title = "Anime", key = "anime")

            item {
                if (isLoading) {
                    RowWallpapersSkeleton()
                } else {
                    val catalogItems = catalogWallpapers[newCatalogConfig.key] ?: emptyList()
                    RowWallpapers(
                        title = newCatalogConfig.title,
                        wallpapers = catalogItems,
                        isShowLabel = true,
                        onSeeMore = {
                            outerNav.navigate("see_more/${newCatalogConfig.key}")
                        }
                    ) { itemId ->
                        topViewModel.hideTopBar()
                        wallpaperViewModel.initFullScreenDataSourceByList(catalogItems)
                        navController.navigate("fullscreen/${itemId}")
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                ) {
                    if (isLoading) {
                        TitlesSkeleton(
                            modifier = Modifier.padding(
                                start = 16.dp, top = 6.dp, end = 16.dp, bottom = 6.dp
                            ),
                            isShowLabel = false,
                            isShowViewMore = false
                        )
                    } else {
                        Titles(
                            title = "Collections",
                            fontSize = 18.sp,
                            isShowViewMore = false,
                            modifier = Modifier.padding(
                                start = 16.dp, top = 6.dp, end = 16.dp, bottom = 6.dp
                            ),
                            onSeeMore = {}
                        )
                    }
                }
            }

            item {
                if (isLoading) {
                    Material3CarouselSkeleton()
                } else {
                    Material3Carousel(outerNav)
                }
            }

            item { Spacer(Modifier.height(8.dp)) }

            catalogConfigs.forEach { config ->

                if (config.key == newCatalogConfig.key) return@forEach // skip "anime"

                val catalogItems = catalogWallpapers[config.key] ?: emptyList()

                if (catalogItems.isNotEmpty()) {
                    item {
                        RowWallpapers(
                            title = config.title,
                            wallpapers = catalogItems,
                            onSeeMore = {
                                outerNav.navigate("see_more/${config.key}")
                            }
                        ) { itemId ->
                            topViewModel.hideTopBar()
                            wallpaperViewModel.initFullScreenDataSourceByList(catalogItems)
                            navController.navigate("fullscreen/${itemId}")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            itemsIndexed(wallpapers.chunked(3)) { index, rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { wallpaper ->
                        Card (
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .aspectRatio(0.5f)
                                .clickable {
                                    topViewModel.hideTopBar()
                                    wallpaperViewModel.initFullScreenDataSourceByList(wallpapers)
                                    navController.navigate("fullscreen/${wallpaper.itemId}")
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