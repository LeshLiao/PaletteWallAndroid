package com.palettex.palettewall.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AATest(
) {
    Column {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("AAA")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAATest() {
    AATest()
}