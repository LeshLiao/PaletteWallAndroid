package com.palettex.palettewall.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun FavoriteScreen(name: String, nav: NavController) {
    var isPopBack by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxSize(), // Make the card fill the entire available space
        colors = CardDefaults.cardColors(
            containerColor = Color.Black // Set the background color to black
        ),
        elevation = CardDefaults.cardElevation(8.dp), // Optional: set elevation
        shape = RectangleShape // Set shape to rectangle if you want sharp corners
    ) {
        // Add content inside the card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (!isPopBack) {
                        nav.popBackStack()
                        isPopBack = true
                    }

                }
            ,
            contentAlignment = Alignment.Center
        ) {
            // Example content inside the card
            Text(
                text = "PaletteX © 2024",
                color = Color.White,
                fontSize = 32.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}