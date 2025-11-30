package com.palettex.palettewall.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palettex.palettewall.ui.screens.home.HomeViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ColorPaletteMatrix(
    wallpaperViewModel: HomeViewModel // Add ViewModel parameter
) {
    val colors = listOf(
        Color(0xFFFF0000), // Color.Red
        Color(0xFFFF4500), // Orange Red
        Color(0xFFFFA500), // Orange
        Color(0xFFFFFF00), // Color.Yellow
        Color(0xFF32CD32), // Lime Green
        Color(0xFF008000), // Green
        Color(0xFF20B2AA), // Light Sea Green
        Color(0xFF4169E1), // Royal Blue
        Color(0xFF0000CD), // Medium Blue
        Color(0xFF8A2BE2), // Blue Violet
        Color(0xFFFF1493), // Deep Pink
        Color(0xFFFFFFFF), // Color.White
        Color(0xFF444444), // Color.DarkGray
        Color(0xFF000000), // Black
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        for (row in 0..1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (col in 0..6) {
                    val index = row * 7 + col
                    if (index < colors.size) {
                        val color = colors[index]
                        ColorBox(
                            color = color,
                            isFirstSelected = wallpaperViewModel.firstSelectedColor.value == color,
                            isSecondSelected = wallpaperViewModel.secondSelectedColor.value == color,
                            onClick = {
                                if (wallpaperViewModel.firstSelectedColor.value == color) {
                                    wallpaperViewModel.setFirstSelectedColor(null)
                                } else if (wallpaperViewModel.secondSelectedColor.value == color) {
                                    wallpaperViewModel.setSecondSelectedColor(null)
                                } else if (wallpaperViewModel.firstSelectedColor.value == null) {
                                    wallpaperViewModel.setFirstSelectedColor(color)
                                } else if (wallpaperViewModel.secondSelectedColor.value == null) {
                                    wallpaperViewModel.setSecondSelectedColor(color)
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ColorBox(
    color: Color,
    isFirstSelected: Boolean,
    isSecondSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag("testTag_colorBox_" + color.toHexString())
            .size(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .border(
                width = 1.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
    ) {
        when {
            isFirstSelected -> {
                Text(
                    text = "1",
                    color = if (color.luminance() > 0.5f) Color.Black else Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(4.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            isSecondSelected -> {
                Text(
                    text = "2",
                    color = if (color.luminance() > 0.5f) Color.Black else Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(4.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun Color.toHexString(): String {
    // Color values are in 0..1 range, convert to 0..255
    val alpha = (alpha * 255).toInt()
    val red = (red * 255).toInt()
    val green = (green * 255).toInt()
    val blue = (blue * 255).toInt()

    // Format as 0xAARRGGBB
    return "0x%02X%02X%02X%02X".format(alpha, red, green, blue)
}