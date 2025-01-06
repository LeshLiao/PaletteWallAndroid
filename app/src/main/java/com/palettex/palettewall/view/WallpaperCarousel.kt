package com.palettex.palettewall.view

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlin.math.absoluteValue

@Composable
fun WallpaperScreen(name: String, nav: NavController, wallpaperViewModel: WallpaperViewModel, viewModel: TopBarViewModel) {
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()

    Column {
        Spacer(modifier = Modifier.height(120.dp).fillMaxWidth())
            WallpaperCarousel(wallpapers, wallpaperViewModel) { selectedWallpaper ->
                viewModel.hideTopBar()
                nav.navigate("fullscreen/${selectedWallpaper}")
                Log.d("GDT","selectedWallpaper="+ selectedWallpaper)
            }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WallpaperCarousel(
//    wallpapers: List<String>,
    wallpapers: List<WallpaperItem>,
    wallpaperViewModel: WallpaperViewModel,
    onWallpaperSelected: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { wallpapers.size })

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 48.dp),
        pageSpacing = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val pageOffset = (
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                ).absoluteValue

        val scale = 1f - (pageOffset * 0.15f).coerceIn(0f, 0.15f)
        val alpha = 1f - (pageOffset * 0.5f).coerceIn(0f, 0.5f)

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }
                .aspectRatio(0.55f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
                .clickable { onWallpaperSelected(wallpapers[page].itemId) }
        ) {
            val imageUrl = wallpaperViewModel.getThumbnailByItemId(wallpapers[page].itemId)
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Wallpaper ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWallpaperScreen() {
    val navController = rememberNavController()
    val mockWallpaperViewModel = WallpaperViewModel().apply {}
    val mockTopBarViewModel = TopBarViewModel().apply {}
    WallpaperScreen(name = "Preview", nav = navController , mockWallpaperViewModel, mockTopBarViewModel)
}
