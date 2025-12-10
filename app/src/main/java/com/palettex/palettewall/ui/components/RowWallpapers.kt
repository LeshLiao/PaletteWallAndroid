package com.palettex.palettewall.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.palettex.palettewall.PaletteWallApplication
import com.palettex.palettewall.R
import com.palettex.palettewall.domain.model.ImageItem
import com.palettex.palettewall.domain.model.WallpaperItem
import com.palettex.palettewall.ui.components.utility.throttleClick

@Composable
fun RowWallpapers(
    title: String,
    wallpapers: List<WallpaperItem>,
    onSeeMore: () -> Unit,
    showDiamond: Boolean = true,
    isShowLabel: Boolean = false,
    onClick: (itemId: String) -> Unit
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }
    val imageCacheList = PaletteWallApplication.imageCacheList

    Column {
        Titles(
            title = title,
            isShowLabel = isShowLabel,
            modifier = Modifier.padding(16.dp, 6.dp, 16.dp, 4.dp)
        ) {
            onSeeMore()
        }

        LazyRow(
            modifier = Modifier
                .height(260.dp)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val borderColorList = listOf(
                Color(0xFF7A7A7A)
            )
            wallpapers.forEachIndexed { index, wallpaper ->
                item {
                    val rainbowColor = borderColorList[index % borderColorList.size]
                    val imageUrl = wallpaper.imageList.firstOrNull {
                        it.type == "LD" && it.link.isNotEmpty()
                    }?.link ?: ""

                    val blurImageUrl = wallpaper.imageList.firstOrNull {
                        it.type == "BL" && it.link.isNotEmpty()
                    }?.link ?: ""

                    val imageSource = imageUrl.getImageSourceFromAssets(context, imageCacheList)
                    val blurSource = blurImageUrl.getImageSourceFromAssets(context, imageCacheList)

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(0.5f)
                            //.border(2.dp, rainbowColor, RoundedCornerShape(8.dp))
                            .throttleClick{
                                onClick(wallpaper.itemId)
                            }
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF111111))
                            .testTag("wallpaper_card"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            ProgressiveImageLoaderBest(
                                blurImageUrl = blurSource,
                                fullImageSource = imageSource,
                                imageLoader = imageLoader
                            )
                        }

                        if (showDiamond) {
                            Box(
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(40.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (!wallpaper.freeDownload) {
                                        Image(
                                            painter = painterResource(R.drawable.diamond),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
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
        wallpapers = sampleWallpapers,
        onSeeMore = {},
        onClick = { itemId ->
            println("Clicked wallpaper: $itemId")
        }
    )
}