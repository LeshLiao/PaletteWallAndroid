package com.palettex.palettewall.view.component

import androidx.compose.foundation.Image
import com.palettex.palettewall.model.WallpaperItem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.palettex.palettewall.model.ImageItem
import com.palettex.palettewall.view.Titles

@Composable
fun RowWallpapers(
    title: String,
    list: List<WallpaperItem>,
    onClick: (itemId: String) -> Unit
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }

    val borderColorList = listOf(
        Color(0xFF7A7A7A) // Medium-Dark Gray
    )
    Column {
        Titles(
            title = title,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 2.dp)
        )

        LazyRow(
            modifier = Modifier
                .height(260.dp)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            list.forEachIndexed { index, wallpaper ->
                item {
                    // Use the color from the rainbow list in cyclic order
                    val rainbowColor = borderColorList[index % borderColorList.size]
                    val imageUrl = wallpaper.imageList.firstOrNull {
                        it.type == "LD" && it.link.isNotEmpty()
                    }?.link ?: ""

                    Card(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(0.5f)
                            .border(2.dp, rainbowColor, RoundedCornerShape(8.dp)) // Add border
                            .clickable {
                                onClick(wallpaper.itemId)
                            }
                            .testTag("popular_wallpaper_card"),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Create the painter with ImageRequest for better control
                            val painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .placeholderMemoryCacheKey(imageUrl)
                                    .build(),
                                imageLoader = imageLoader
                            )

                            // Check the state of the painter
                            val painterState = painter.state

                            // Show skeleton loader while loading
                            if (painterState is AsyncImagePainter.State.Loading ||
                                painterState is AsyncImagePainter.State.Error) {
                                ImageSkeletonLoader(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Show the image (will be drawn on top of skeleton when loaded)
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun RowWallpapersPreview() {
    // Create sample wallpaper data
    val sampleWallpapers = listOf(
        WallpaperItem(
            id = "1",
            itemId = "wall_001",
            name = "Sunset Paradise",
            price = 0.0,
            freeDownload = true,
            stars = 5,
            photoType = "Nature",
            tags = listOf("sunset", "nature", "landscape"),
            sizeOptions = listOf("1080x1920", "1440x2560"),
            thumbnail = "https://picsum.photos/200/400?random=1",
            preview = "https://picsum.photos/400/800?random=1",
            imageList = listOf(
                ImageItem(
                    type = "LD",
                    link = "https://picsum.photos/400/800?random=1",
                    resolution = "400x800",
                    blob = ""
                ),
                ImageItem(
                    type = "HD",
                    link = "https://picsum.photos/1080/1920?random=1",
                    resolution = "1080x1920",
                    blob = ""
                )
            ),
            downloadList = emptyList(),
            createdAt = "2025-10-01T10:00:00Z",
            updatedAt = "2025-10-01T10:00:00Z",
            version = 1
        ),
        WallpaperItem(
            id = "2",
            itemId = "wall_002",
            name = "Ocean Waves",
            price = 2.99,
            freeDownload = false,
            stars = 4,
            photoType = "Ocean",
            tags = listOf("ocean", "waves", "blue"),
            sizeOptions = listOf("1080x1920", "1440x2560"),
            thumbnail = "https://picsum.photos/200/400?random=2",
            preview = "https://picsum.photos/400/800?random=2",
            imageList = listOf(
                ImageItem(
                    type = "LD",
                    link = "https://picsum.photos/400/800?random=2",
                    resolution = "400x800",
                    blob = ""
                ),
                ImageItem(
                    type = "HD",
                    link = "https://picsum.photos/1080/1920?random=2",
                    resolution = "1080x1920",
                    blob = ""
                )
            ),
            downloadList = emptyList(),
            createdAt = "2025-10-02T10:00:00Z",
            updatedAt = "2025-10-02T10:00:00Z",
            version = 1
        ),
        WallpaperItem(
            id = "3",
            itemId = "wall_003",
            name = "Mountain Peak",
            price = 0.0,
            freeDownload = true,
            stars = 5,
            photoType = "Mountain",
            tags = listOf("mountain", "snow", "peak"),
            sizeOptions = listOf("1080x1920", "1440x2560", "2160x3840"),
            thumbnail = "https://picsum.photos/200/400?random=3",
            preview = "https://picsum.photos/400/800?random=3",
            imageList = listOf(
                ImageItem(
                    type = "LD",
                    link = "https://picsum.photos/400/800?random=3",
                    resolution = "400x800",
                    blob = ""
                ),
                ImageItem(
                    type = "HD",
                    link = "https://picsum.photos/1080/1920?random=3",
                    resolution = "1080x1920",
                    blob = ""
                )
            ),
            downloadList = emptyList(),
            createdAt = "2025-10-03T10:00:00Z",
            updatedAt = "2025-10-03T10:00:00Z",
            version = 1
        ),
        WallpaperItem(
            id = "4",
            itemId = "wall_004",
            name = "City Lights",
            price = 1.99,
            freeDownload = false,
            stars = 4,
            photoType = "Urban",
            tags = listOf("city", "night", "lights"),
            sizeOptions = listOf("1080x1920", "1440x2560"),
            thumbnail = "https://picsum.photos/200/400?random=4",
            preview = "https://picsum.photos/400/800?random=4",
            imageList = listOf(
                ImageItem(
                    type = "LD",
                    link = "https://picsum.photos/400/800?random=4",
                    resolution = "400x800",
                    blob = ""
                ),
                ImageItem(
                    type = "HD",
                    link = "https://picsum.photos/1080/1920?random=4",
                    resolution = "1080x1920",
                    blob = ""
                )
            ),
            downloadList = emptyList(),
            createdAt = "2025-10-04T10:00:00Z",
            updatedAt = "2025-10-04T10:00:00Z",
            version = 1
        )
    )

    RowWallpapers(
        title = "Popular Wallpapers",
        list = sampleWallpapers,
        onClick = { itemId ->
            println("Clicked wallpaper: $itemId")
        }
    )
}