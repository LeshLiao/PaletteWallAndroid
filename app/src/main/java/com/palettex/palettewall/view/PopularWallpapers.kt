package com.palettex.palettewall.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PopularWallpapers(
    viewModel: TopBarViewModel,
    navController: NavController,
    wallpaperViewModel: WallpaperViewModel,
) {
    val topTenWallpapers by wallpaperViewModel.topTenWallpapers.collectAsState()
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }

    val borderColorList = listOf(
        Color(0xFF7A7A7A) // Medium-Dark Gray
    )

    // Preload all popular wallpaper images when the component is first composed
    LaunchedEffect(topTenWallpapers) {
        // Execute in IO dispatcher to avoid blocking the UI thread
        launch(Dispatchers.IO) {
            topTenWallpapers.forEach { wallpaper ->
                val thumbnailUrl = wallpaperViewModel.getThumbnailByItemId(wallpaper.itemId)

                // Create an image request with aggressive caching
                val request = ImageRequest.Builder(context)
                    .data(thumbnailUrl)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .placeholderMemoryCacheKey(thumbnailUrl)
                    .build()

                // Execute the request to preload it
                imageLoader.enqueue(request)
            }
        }
    }

    LazyRow(
        modifier = Modifier
            .height(260.dp)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        topTenWallpapers.forEachIndexed { index, wallpaper ->
            item {
                // Use the color from the rainbow list in cyclic order
                val rainbowColor = borderColorList[index % borderColorList.size]
                val thumbnailUrl = wallpaperViewModel.getThumbnailByItemId(wallpaper.itemId)

                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(0.5f)
                        .border(2.dp, rainbowColor, RoundedCornerShape(8.dp)) // Add border
                        .clickable {
                            viewModel.hideTopBar()
                            navController.navigate("fullscreen/${wallpaper.itemId}")
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black
                    )
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(thumbnailUrl)
                                .crossfade(true)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .placeholderMemoryCacheKey(thumbnailUrl)
                                .build(),
                            imageLoader = imageLoader
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}