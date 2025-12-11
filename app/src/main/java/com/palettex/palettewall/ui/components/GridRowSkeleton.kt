package com.palettex.palettewall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

object SkeletonDarkColor {
    val color = Color(128, 128, 128).copy(0.1f)
    val brushColorList: List<Color> = listOf(
        color.copy(0.10f),
        color.copy(0.15f),
        color.copy(0.20f)
    )
}

@Composable
fun GridRowSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            Box (
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF000000))
                    .aspectRatio(9f / 16f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    SkeletonParagraph(
                        rows = 1,
                        modifier = Modifier.fillMaxSize(),
                        animated = true,
                        brushColorList = SkeletonDarkColor.brushColorList
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, name = "Single Row")
@Composable
fun SeeMoreGridRowSkeletonSinglePreview() {
    GridRowSkeleton()
}