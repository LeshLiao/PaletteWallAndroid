package com.palettex.palettewall.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.palettex.palettewall.R
import com.palettex.palettewall.data.WallpaperDatabase
import com.palettex.palettewall.view.utility.throttleClick
import com.palettex.palettewall.viewmodel.TopBarViewModel

@Composable
fun LikeCollection(
    topViewModel: TopBarViewModel,
    navController: NavController,
    topOffset: Dp,
    bottomOffset: Dp,
    context: Context = LocalContext.current
) {
    val database = remember { WallpaperDatabase.getDatabase(context) }
    val dao = remember { database.likedWallpaperDao() }
    val likedWallpapers by dao.getAllLikedWallpapers().collectAsState(initial = emptyList())
    val topSystemOffset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    LaunchedEffect(Unit) {
        topViewModel.hideTopBar()
    }

    Column (modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Spacer(modifier = Modifier.height(topSystemOffset))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .throttleClick {
                    navController.popBackStack()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Liked Collection",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(end = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        if (likedWallpapers.isEmpty()) {
            EmptyBox(bottomOffset)
        } else {
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
}

@Composable
fun EmptyBox(bottomOffset: Dp) {
    Column( modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(136.dp).background(Color.Black)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_empty_box),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Back",
                    tint = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(bottomOffset))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyBox() {
    val navController = rememberNavController()
    val mockTopBarViewModel = TopBarViewModel().apply {}
    LikeCollection(mockTopBarViewModel, navController, 100.dp, 100.dp)
}