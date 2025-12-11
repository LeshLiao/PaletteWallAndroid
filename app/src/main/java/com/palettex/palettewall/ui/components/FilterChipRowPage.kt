package com.palettex.palettewall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun FilterChipRow(
    modifier: Modifier = Modifier,
    filters: List<String>,
    selectedFilters: List<String>,
    onFilterSelected: (String) -> Unit,
    onClearFilters: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Filter chips in LazyRow
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 64.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = filter in selectedFilters,
                    onClick = { onFilterSelected(filter) },
                    label = { Text(filter) },
                    leadingIcon = if (filter in selectedFilters) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Selected",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }

        // Clear button floating on the right
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(100.dp)
                .height(40.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        )
                    )
                )
        )

        IconButton(
            onClick = {
                onClearFilters()
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Clear filters",
                tint = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Preview(showBackground = true, name = "No Selection", backgroundColor = 0xFF000000)
@Composable
private fun FilterChipRowPreviewNoSelection() {
    FilterChipRow(
        filters = listOf("Anime", "Natural", "Landscape", "Abstract", "Minimalist"),
        selectedFilters = listOf("Anime", "Abstract"),
        onFilterSelected = {},
        onClearFilters = {}
    )
}