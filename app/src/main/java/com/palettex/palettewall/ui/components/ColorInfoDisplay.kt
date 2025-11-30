package com.palettex.palettewall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ColorInfoDisplay(colorList: List<Color>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        if (colorList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Black)
            )
        } else {
            colorList.forEach { color ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color)
                ) {
                    Text(
                        text = colorToARGBString(color),
                        color = Color.White,
                        modifier = Modifier.padding(4.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun colorToARGBString(color: Color): String {
    return String.format(
        "#%08X",
        (color.alpha * 255).toInt() shl 24 or
                ((color.red * 255).toInt() shl 16) or
                ((color.green * 255).toInt() shl 8) or
                (color.blue * 255).toInt()
    )
}