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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlin.random.Random

@Composable
fun PopularWallpapers(
    viewModel: TopBarViewModel,
    navController: NavController,
    wallpaperViewModel: WallpaperViewModel,
) {
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()

    // Initialize shuffledWallpapers as a mutable state with an empty list
    val shuffledWallpapers = remember { mutableStateOf<List<WallpaperItem>>(emptyList()) }

    LaunchedEffect(wallpapers) {
        if (shuffledWallpapers.value.isEmpty()) {
            shuffledWallpapers.value = wallpapers.shuffled()
        }
    }

    LazyRow(
        modifier = Modifier
            .height(260.dp)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        shuffledWallpapers.value.forEach { wallpaper ->
            item {
                // Generate a random color for the border
                val randomColor = Color(
                    red = Random.nextFloat(),
                    green = Random.nextFloat(),
                    blue = Random.nextFloat(),
                    alpha = 1f
                )

                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(0.5f)
                        .border(2.dp, randomColor, RoundedCornerShape(8.dp)) // Add border
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
                            model = wallpaperViewModel.getThumbnailByItemId(
                                wallpaper.itemId
                            )
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
