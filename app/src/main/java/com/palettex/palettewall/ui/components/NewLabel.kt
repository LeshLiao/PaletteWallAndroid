package com.palettex.palettewall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewLabel(
    modifier: Modifier = Modifier,
    text: String = "new",
    backgroundColor: Color = Color.Red,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(
                    topStart = 6.dp,
                    topEnd = 6.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 6.dp
                )
            )
            .padding(horizontal = 6.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            lineHeight = 12.sp,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "New Label")
@Composable
private fun NewLabelPreview() {
    MaterialTheme {
        Surface {
            NewLabel(
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "New Label - Custom")
@Composable
private fun NewLabelCustomPreview() {
    MaterialTheme {
        Surface {
            NewLabel(
                modifier = Modifier.padding(16.dp),
                text = "HOT",
                backgroundColor = Color(0xFFFF6B00)
            )
        }
    }
}