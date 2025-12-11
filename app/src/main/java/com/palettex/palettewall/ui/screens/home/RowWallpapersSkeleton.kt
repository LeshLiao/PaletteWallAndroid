package com.palettex.palettewall.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.palettex.palettewall.ui.components.SkeletonParagraph

@Composable
fun RowWallpapersSkeleton(
    title: String = "Loading...",
    itemCount: Int = 5
) {
    Column {
        // Title skeleton
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SkeletonParagraph(
                rows = 1,
                modifier = Modifier
                    .padding(16.dp, 6.dp, 16.dp, 4.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp)),
                animated = true
            )

            SkeletonParagraph(
                rows = 1,
                modifier = Modifier
                    .padding(16.dp, 6.dp, 16.dp, 4.dp)
                    .width(80.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp)),
                animated = true
            )
        }

        // Wallpaper cards skeleton
        LazyRow(
            modifier = Modifier
                .height(260.dp)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(itemCount) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(0.5f)
                ) {
                    SkeletonParagraph(
                        rows = 1,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(0.5f)
                            .clip(RoundedCornerShape(8.dp)),
                        animated = true
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun RowWallpapersSkeletonPreview() {
    RowWallpapersSkeleton()
}