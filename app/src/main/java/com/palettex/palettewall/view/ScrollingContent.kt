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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.data.PaletteRemoteConfig
import com.palettex.palettewall.data.WallpaperDatabase
import com.palettex.palettewall.view.component.LikeButton
import com.palettex.palettewall.viewmodel.AdManager
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScrollingContent(
    bottomOffset: Dp,
    topViewModel: TopBarViewModel,
    navController: NavController,
    wallpaperViewModel: WallpaperViewModel,
    context: Context = LocalContext.current
) {
    val listState = rememberLazyListState()
    val lastScrollOffset = remember { mutableIntStateOf(0) }
    val appSettings by wallpaperViewModel.appSettings.collectAsState()
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()
    val currentCatalog by wallpaperViewModel.currentCatalog.collectAsState()
    val isRemoteConfigInitialized by wallpaperViewModel.isRemoteConfigInitialized.collectAsState()

    // Add pull-to-refresh state
    val refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            wallpaperViewModel.updateCurrentCatalog()
        }
    )

    // Pre-initialize the AdMobBannerView for early initialization
    val isBottomAdsLoaded by wallpaperViewModel.isBottomAdsLoaded.collectAsState()
    var showPopular by remember { mutableStateOf(false) }
    val scrollToTopTrigger by wallpaperViewModel.scrollToTopTrigger.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val database = remember { WallpaperDatabase.getDatabase(context) }
    val dao = remember { database.likedWallpaperDao() }

    val adMobBannerView = remember { AdManager.getOrCreateAd(context) }

    LaunchedEffect(Unit, isRemoteConfigInitialized) {
        Log.d("GDT", "isRemoteConfigInitialized=" + isRemoteConfigInitialized)
        if (isRemoteConfigInitialized) {
            AdManager.loadAdIfNeeded(wallpaperViewModel)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { currentScrollOffset ->
                val delta = currentScrollOffset - lastScrollOffset.intValue

                // Scroll handling for top bar visibility
                topViewModel.onScroll(delta.toFloat())

                // Check if scrolled to the top (first item and no offset)
                if (listState.firstVisibleItemIndex <= 1) {
                    topViewModel.showTopBar()  // Call showTopBar when at the top
                }

                lastScrollOffset.intValue = currentScrollOffset
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
                    title = "Popular Collections",
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
                                navController.navigate("fullscreen/${wallpaper.itemId}")
//                                Log.d("GDT","itemId=${wallpaper.itemId}")
                            },
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = wallpaperViewModel.getThumbnailByItemId(
                                    wallpaper.itemId
                                )
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        val isLiked by dao.isWallpaperLiked(wallpaper.itemId)
                            .collectAsState(initial = false)
                        if (isLiked) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd) // Place the button at the bottom-end
                                    .padding(2.dp) // Add padding if needed
                            ) {
                                LikeButton(
                                    isLiked,
                                    dao,
                                    wallpaper.itemId,
                                    wallpaperViewModel,
                                    coroutineScope
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

        item {
            Spacer(modifier = Modifier.height(12.dp))
            if (PaletteRemoteConfig.shouldShowBannerAds()) {
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { adMobBannerView }
                )
            }
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
            text = ">",
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = Color.White,
        )
    }
}