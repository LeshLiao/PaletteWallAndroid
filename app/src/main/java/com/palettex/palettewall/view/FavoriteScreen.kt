package com.palettex.palettewall.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FavoriteScreen(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name",
    )
}