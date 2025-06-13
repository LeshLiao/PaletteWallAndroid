package com.palettex.palettewall.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.R
import com.palettex.palettewall.view.component.ImageSkeletonLoader
import com.palettex.palettewall.viewmodel.WallpaperViewModel

@Composable
fun CatalogRow(
    wallpaperViewModel: WallpaperViewModel,
) {
    val catalogs by wallpaperViewModel.catalogs.collectAsState()

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        catalogs.forEach { item ->
            Column(
                modifier = Modifier.padding(2.dp).width(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 0.dp)
                        .height(item.height.dp)
                        .width(item.width.dp)
                        .clickable {
                            when (item.key) {
                                "Wallpapers" -> {
                                    wallpaperViewModel.fetchWallpaperBy(item.key)
                                }
                                else -> {
                                    wallpaperViewModel.fetchWallpaperBy(item.key)
                                }
                            }
                            wallpaperViewModel.firebaseCatalogEvent(item.key)
                        },
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.black)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val painter = rememberAsyncImagePainter(model = item.photoUrl)
                        val painterState = painter.state

                        // Show skeleton loader while loading
                        if (painterState is AsyncImagePainter.State.Loading ||
                            painterState is AsyncImagePainter.State.Error) {
                            ImageSkeletonLoader(
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Image(
                            painter = painter,
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}