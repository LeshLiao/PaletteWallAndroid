package com.palettex.palettewall.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitlesSkeleton(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 22.sp,
    isShowLabel: Boolean = false,
    isShowViewMore: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title skeleton
            SkeletonParagraph(
                rows = 1,
                modifier = Modifier
                    .width(100.dp)
                    .height(fontSize.value.dp)
                    .clip(RoundedCornerShape(4.dp)),
                animated = true
            )

            if (isShowLabel) {
                Spacer(Modifier.width(8.dp))

                // Label skeleton
                SkeletonParagraph(
                    rows = 1,
                    modifier = Modifier
                        .width(40.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    animated = true
                )
            }
        }

        if (isShowViewMore) {
            // "View more" skeleton
            SkeletonParagraph(
                rows = 1,
                modifier = Modifier
                    .width(70.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp)),
                animated = true
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    name = "TitlesSkeleton - Dark"
)
@Composable
private fun TitlesSkeletonPreview() {
    TitlesSkeleton(
        modifier = Modifier.padding(16.dp),
        isShowLabel = true,
        isShowViewMore = true
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    name = "TitlesSkeleton - No Label"
)
@Composable
private fun TitlesSkeletonNoLabelPreview() {
    TitlesSkeleton(
        modifier = Modifier.padding(16.dp),
        isShowLabel = false,
        isShowViewMore = true
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    name = "TitlesSkeleton - No ViewMore"
)
@Composable
private fun TitlesSkeletonNoViewMorePreview() {
    TitlesSkeleton(
        modifier = Modifier.padding(16.dp),
        isShowLabel = false,
        isShowViewMore = false
    )
}