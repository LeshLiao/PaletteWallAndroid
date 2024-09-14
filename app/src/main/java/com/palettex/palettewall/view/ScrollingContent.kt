package com.palettex.palettewall.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.R
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.palettex.palettewall.viewmodel.TopBarViewModel
@Composable
fun ScrollingContent(
    viewModel: TopBarViewModel,
    navController: NavController,
    wallpaperViewModel: WallpaperViewModel,
) {
    val listState = rememberLazyListState()
    val lastScrollOffset = remember { mutableIntStateOf(0) }
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { currentScrollOffset ->
                val delta = currentScrollOffset - lastScrollOffset.intValue

                // Scroll handling for top bar visibility
                viewModel.onScroll(delta.toFloat())

                // Check if scrolled to the top (first item and no offset)
//                Log.d("GDT",listState.firstVisibleItemIndex.toString() + "," + currentScrollOffset.toString())
                if (listState.firstVisibleItemIndex <= 1) {
                    viewModel.showTopBar()  // Call showTopBar when at the top
                }

                lastScrollOffset.intValue = currentScrollOffset
            }
    }

    LazyColumn(state = listState) {
        item {
            Spacer(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            )
        }

        val listOfImages: List<Pair<String, String>> = listOf(
            "Wallpapers" to "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Flayout%2Fall.jpeg?alt=media&token=d7d90309-c950-40ca-92aa-cbae24e38212",
            "Anime" to "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Flayout%2Fanime.jpeg?alt=media&token=93edafa7-273b-481f-9963-14917aa07157",
            "City" to "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Flayout%2Fcity.jpeg?alt=media&token=4f22eee5-ac9d-45b2-963f-dc1a331cc2cc",
            "Painting" to "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Flayout%2Fpainting.jpeg?alt=media&token=e3d01014-a0be-45f2-a818-6fbadd3f78af"
        )

        // I have 4 api:
        // wallpaperViewModel.fetchWallpapersApi()
        // wallpaperViewModel.fetchAnimeApi()
        // wallpaperViewModel.fetchCityApi()
        // wallpaperViewModel.fetchPaintingApi()

        item {
            LazyRow {
                items(listOfImages) { item ->
                    Column(
                        modifier = Modifier
                            .padding(2.dp)
                            .width(100.dp),  // Removed height constraint
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 0.dp)
                                .height(100.dp)
                                .width(80.dp)
                                .clickable {
                                    when (item.first) {
                                        "Anime" -> wallpaperViewModel.fetchAnimeApi()
                                        "Wallpapers" -> wallpaperViewModel.fetchShuffledWallpapersApi()
                                        "City" -> wallpaperViewModel.fetchCityApi()
                                        "Painting" -> wallpaperViewModel.fetchPaintingApi()
                                        else -> {
                                            // Handle unknown cases if needed
                                        }
                                    }
                                },
                            elevation = CardDefaults.cardElevation(8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(id = R.color.teal_200)
                            )
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = item.second),
                                contentDescription = item.first,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.first,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }

        }

        items(wallpapers.chunked(3)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                rowItems.forEach { wallpaper ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .aspectRatio(0.5f)
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
                            painter = rememberAsyncImagePainter(model = wallpaperViewModel.getThumbnailByItemId(wallpaper.itemId)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
