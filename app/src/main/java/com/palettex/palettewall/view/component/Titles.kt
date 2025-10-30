package com.palettex.palettewall.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.palettex.palettewall.view.utility.throttleClick

@Composable
fun Titles(
    title: String,
    modifier: Modifier,
    onSeeMore: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            modifier = Modifier.throttleClick{
                onSeeMore()
            },
            text = "view more >",
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}