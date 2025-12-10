package com.palettex.palettewall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.palettex.palettewall.data.remote.dto.CatalogConfig
import com.palettex.palettewall.ui.components.utility.throttleClick

data class CarouselItem(
    val imageUrl: String,
    val title: String,
    val key: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3Carousel(
    outerNav: NavController,
) {

    val carouselItems = listOf(
        CarouselItem(
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Flayout%2Fcollection_mountains.jpg?alt=media&token=0a79ea6c-754d-4f57-aa39-f1d7299c5644",
            title = "Mountains",
            key = "mountains"
        ),
        CarouselItem(
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Flayout%2Fcollection_forest.jpg?alt=media&token=c67832fb-13a4-44b9-aa45-0c49266f081d",
            title = "Forest",
            key = "forest"
        ),
        CarouselItem(
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Flayout%2Fcollection_sunset.jpg?alt=media&token=b9a6d6a3-2a0f-4077-934d-a47d4d4371a0",
            title = "Sunset",
            key = "sunset"
        ),
        CarouselItem(
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Flayout%2Fcollection_flowers.jpg?alt=media&token=d0c56259-7441-4b64-a9c9-cde2964f9eed",
            title = "Flowers",
            key = "flowers"
        ),
        CarouselItem(
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Flayout%2Fcollection_sky.jpg?alt=media&token=28004b27-68ad-4341-b561-c031db217749",
            title = "Sky",
            key = "sky"
        )
    )

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { carouselItems.size },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        preferredItemWidth = 300.dp,
        itemSpacing = 10.dp,
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) { i ->
        val item = carouselItems[i]

        Box(
            modifier = Modifier.fillMaxSize().throttleClick {
                outerNav.navigate("see_more/${item.key}")
            }
        ) {
            // Background Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxSize()
                    .maskClip(MaterialTheme.shapes.extraLarge),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Title Text
            Text(
                text = item.title,
                modifier = Modifier
                    //.align(Alignment.BottomStart)
                    .align(Alignment.BottomEnd)
                    //.padding(start = 32.dp, bottom = 8.dp),
                    .padding(end = 32.dp, bottom = 6.dp),
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}