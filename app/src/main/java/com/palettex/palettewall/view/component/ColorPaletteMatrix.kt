package com.palettex.palettewall.view.component

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.data.WallpaperDatabase
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.absoluteValue

@Composable
fun ColorPaletteMatrix(
    onColorSelected: (List<Color>) -> Unit
) {
    // Define 14 different colors for the 2x7 matrix
    val colors = listOf(
        Color.Red,
        Color(0xFFFF4500), // Orange Red
        Color(0xFFFFA500), // Orange
        Color.Yellow,
        Color(0xFF32CD32), // Lime Green
        Color(0xFF008000), // Green
        Color(0xFF20B2AA), // Light Sea Green
        Color(0xFF4169E1), // Royal Blue
        Color(0xFF0000CD), // Medium Blue
        Color(0xFF8A2BE2), // Blue Violet
        Color(0xFFFF1493), // Deep Pink
        Color.White,
        Color.DarkGray,
        Color(0xFF111111)
    )

    // Keep track of selected colors
    var selectedColors by remember { mutableStateOf(setOf<Color>()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp,vertical = 8.dp)
    ) {
        // Create 2 rows
        for (row in 0..1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Create 7 columns for each row
                for (col in 0..6) {
                    val index = row * 7 + col
                    if (index < colors.size) {
                        val color = colors[index]
                        ColorBox(
                            color = color,
                            isSelected = selectedColors.contains(color),
                            onClick = {
                                selectedColors = if (selectedColors.contains(color)) {
                                    // Deselect if already selected
                                    selectedColors - color
                                } else {
                                    // Select if not selected
                                    selectedColors + color
                                }
                                onColorSelected(selectedColors.toList())
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
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
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
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewColorPaletteMatrix() {
    ColorPaletteMatrix { selectedColors ->
        // Handle selected colors in your app
        println("Selected colors: $selectedColors")
    }
}