package com.palettex.palettewall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AutoScrollImagePagerSkeleton(
    modifier: Modifier = Modifier,
    indicatorCount: Int = 3
) {
    Column(modifier = modifier) {
        // Image skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 6f)
        ) {
            SkeletonParagraph(
                rows = 1,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                animated = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Page indicator skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(indicatorCount) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == 0)
                                Color(0xFFCCCCCC).copy(alpha = 0.3f)
                            else
                                Color.Gray.copy(alpha = 0.2f)
                        )
                )

                if (index != indicatorCount - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AutoScrollImagePagerSkeletonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 3 indicators
        AutoScrollImagePagerSkeleton(
            indicatorCount = 3
        )
    }
}