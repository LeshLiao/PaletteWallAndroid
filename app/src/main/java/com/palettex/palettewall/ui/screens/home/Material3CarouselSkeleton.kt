package com.palettex.palettewall.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.palettex.palettewall.ui.components.SkeletonParagraph

@Composable
fun Material3CarouselSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        items(itemCount) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
            ) {
                // Background skeleton
                SkeletonParagraph(
                    rows = 1,
                    modifier = Modifier.fillMaxSize(),
                    animated = true
                )

                // Gradient overlay (same as original)
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
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun Material3CarouselSkeletonPreview() {
    Material3CarouselSkeleton()
}