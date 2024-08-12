package com.palettex.palettewall.view

import TopBarViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(viewModel: TopBarViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box {
            ScrollingContent(viewModel)
            TopBar(viewModel)

        }
    }
}