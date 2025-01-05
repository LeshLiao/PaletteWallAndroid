package com.palettex.palettewall.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlin.math.absoluteValue

@Composable
fun WallpaperScreen(name: String, nav: NavController) {
    val wallpapers = listOf(
        "https://fastly.picsum.photos/id/240/300/600.jpg?hmac=LAkt9UXHs1HHjNbJ7jrWrwRZ4OI0Rk-0ef3-MAkz0_E",
        "https://fastly.picsum.photos/id/61/300/600.jpg?hmac=9yuTI9wZRaa_XbDn9T3glKjq4bw99MHI91kJInO8Ey8",
        "https://fastly.picsum.photos/id/847/300/600.jpg?hmac=xYyeCFbSHqdeQDqPOO8_uoYbWVx5RRJ_zq-_aj--678",
        "https://fastly.picsum.photos/id/191/300/600.jpg?hmac=Xza9GF3zvhBmGj0LaeTaOoJgoz_2TMfCLkxPKYuGs_E",
        "https://fastly.picsum.photos/id/174/300/600.jpg?hmac=N7PTKsEZ7AlKrxi6lxH9gLzAe4AMXM1Yvq0bsqWZe38"
    )

    WallpaperCarousel(wallpapers) { selectedWallpaper ->
        // Handle wallpaper selection, e.g., set as wallpaper
        println("Selected Wallpaper: $selectedWallpaper")

    }
}

@Composable
fun WallpaperCarousel(
    wallpapers: List<String>,
    onWallpaperSelected: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { wallpapers.size })

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 48.dp),
        pageSpacing = 16.dp,
        modifier = Modifier.fillMaxSize()
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
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
                .clickable { onWallpaperSelected(wallpapers[page]) }
        ) {
            Image(
                painter = rememberAsyncImagePainter(wallpapers[page]),
                contentDescription = "Wallpaper ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}