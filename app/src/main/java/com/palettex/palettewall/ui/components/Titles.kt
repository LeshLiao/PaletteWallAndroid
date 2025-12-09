package com.palettex.palettewall.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palettex.palettewall.ui.components.utility.throttleClick

@Composable
fun Titles(
    title: String,
    modifier: Modifier,
    isShowLabel: Boolean = false,
    onSeeMore: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.width(8.dp))

            if (isShowLabel) {
                NewLabel(text = "new")
            }
        }

        Row (
            modifier = Modifier.throttleClick{
                onSeeMore()
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = "view more",
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.primary,
            )

            Icon(
                modifier = Modifier
                    //.padding(end = 8.dp)
                    .size(12.dp)
                    //.align(Alignment.Bottom)
                    .drawWithContent {
                        drawContent()
                        drawContent() // Draw twice for bold effect
                    },
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Back",
                tint = Color.White
            )
        }

    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF121212,
    name = "Titles - Dark"
)
@Composable
private fun TitlesPreview() {
    Titles(
        title = "New Arrivals",
        isShowLabel = true,
        modifier = Modifier.padding(16.dp),
        onSeeMore = {}
    )
}