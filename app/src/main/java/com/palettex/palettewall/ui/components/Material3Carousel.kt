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
import coil.compose.AsyncImage
import coil.request.ImageRequest

data class CarouselItem(
    val imageUrl: String,
    val title: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3Carousel() {
    val carouselItems = listOf(
        CarouselItem(
            imageUrl = "https://fastly.picsum.photos/id/387/600/400.jpg?hmac=eMC7zbZnTh1bCidirECGPd8Ne0gcKlNOLmYI1VICf7E",
            title = "Mountain View"
        ),
        CarouselItem(
            imageUrl = "https://fastly.picsum.photos/id/863/600/400.jpg?hmac=47WltmH3OOJMxS-RPQeo3XPJDTj_UmC0hycyGBcgLvE",
            title = "Ocean Sunset"
        ),
        CarouselItem(
            imageUrl = "https://fastly.picsum.photos/id/162/600/400.jpg?hmac=bzRv5N9MHV0XJ_J9y_MIs5wOEmeD0ZLtQakfcNJ-0yo",
            title = "Forest Path"
        ),
        CarouselItem(
            imageUrl = "https://fastly.picsum.photos/id/429/600/400.jpg?hmac=D82_XrRqUkACPSkK_5Lpb5D-gOW_9MHS7cXbPk6zKnQ",
            title = "City Lights"
        ),
        CarouselItem(
            imageUrl = "https://fastly.picsum.photos/id/401/600/400.jpg?hmac=5ZHOTXoYjt3gFCdkLGFHPXbWULVfWBes7qlIEMze3Qw",
            title = "Desert Landscape"
        )
    )

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { carouselItems.size },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        preferredItemWidth = 300.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = carouselItems[i]

        Box(
            modifier = Modifier.fillMaxSize()
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
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Title Text
            Text(
                text = item.title,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 6.dp),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}