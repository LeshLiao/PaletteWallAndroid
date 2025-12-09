package com.palettex.palettewall.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.palettex.palettewall.PaletteWallApplication
import com.palettex.palettewall.ui.components.utility.throttleClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AutoScrollImagePager(
    images: List<String>,
    modifier: Modifier = Modifier,
    autoScrollDuration: Long = 3000L,
    onItemClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val actualImageCount = images.size
    val infinitePageCount = Int.MAX_VALUE
    val initialPage = infinitePageCount / 2 - (infinitePageCount / 2 % actualImageCount) // Start from first image
    val imageLoader = remember { ImageLoader(context) }
    val imageCacheList = PaletteWallApplication.imageCacheList

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { infinitePageCount }
    )
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll effect
    LaunchedEffect(pagerState.currentPage) {
        delay(autoScrollDuration)
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 16.dp
        ) { page ->
            // Calculate the actual image index using modulo
            val actualIndex = page % actualImageCount
            val imageUrl = images[actualIndex]
            val imageSource = imageUrl.getImageSourceFromAssets(context, imageCacheList)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 6f)
                    .throttleClick{
                        onItemClick(actualIndex)
                    }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    ProgressiveImageLoaderBest(
                        blurImageUrl = "",
                        fullImageSource = imageSource,
                        imageLoader = imageLoader
                    )
                }
//                AsyncImage(
//                    model = images[actualIndex],
//                    contentDescription = "Image $actualIndex",
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Page Indicator below the image
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(actualImageCount) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage % actualImageCount == index)
                                Color(0xFFCCCCCC) // Active indicator color
                            else
                                Color.Gray.copy(alpha = 0.5f)
                        )
                )

                if (index != actualImageCount - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}