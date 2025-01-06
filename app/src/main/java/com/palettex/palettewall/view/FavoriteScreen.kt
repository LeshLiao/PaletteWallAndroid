package com.palettex.palettewall.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.palettex.palettewall.data.WallpaperDatabase
import com.palettex.palettewall.viewmodel.TopBarViewModel

@Composable
fun FavoriteScreen(
    topViewModel: TopBarViewModel,
    navController: NavController,
    context: Context = LocalContext.current
) {
    val database = remember { WallpaperDatabase.getDatabase(context) }
    val dao = remember { database.likedWallpaperDao() }
    val likedWallpapers by dao.getAllLikedWallpapers()
        .collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        topViewModel.showTopBar()
    }

    Column (modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Spacer(modifier = Modifier.height(102.dp).fillMaxWidth())
        Row() {
            Icon(
                modifier = Modifier.padding(16.dp,10.dp,0.dp,0.dp),
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Like",
                tint =Color(0xFFFF3E51)
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Liked Collection",
                fontSize = 24.sp,
                color = Color.White
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(likedWallpapers.size) { index ->
                val wallpaper = likedWallpapers[index]
                AsyncImage(
                    model = wallpaper.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(9f / 18f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            topViewModel.hideTopBar()
                            navController.navigate("fullscreen/${wallpaper.wallpaperId}")
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}