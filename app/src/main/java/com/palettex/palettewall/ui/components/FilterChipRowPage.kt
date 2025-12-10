package com.palettex.palettewall.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.tooling.preview.Preview

@Composable
fun FilterChipRowPage() {
    val filters = listOf("Anime", "Natural", "Landscape", "Abstract", "Minimalist")
    var selectedFilters by remember { mutableStateOf(emptyList<String>()) }

    FilterChipRow(
        filters = filters,
        selectedFilters = selectedFilters,
        onFilterSelected = { filter ->
            selectedFilters = if (filter in selectedFilters) {
                selectedFilters - filter
            } else {
                selectedFilters + filter
            }
        }
    )
}

@Composable
fun FilterChipRow(
    filters: List<String>,
    selectedFilters: List<String>,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
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
}

@Preview(showBackground = true)
@Composable
private fun FilterChipRowPreview() {
    FilterChipRowPage()
}

@Preview(showBackground = true, name = "With Selected Items")
@Composable
private fun FilterChipRowPreviewSelected() {
    FilterChipRow(
        filters = listOf("Anime", "Natural", "Landscape", "Abstract", "Minimalist"),
        selectedFilters = listOf("Anime", "Landscape"),
        onFilterSelected = {}
    )
}

@Preview(showBackground = true, name = "All Selected")
@Composable
private fun FilterChipRowPreviewAllSelected() {
    FilterChipRow(
        filters = listOf("Anime", "Natural", "Landscape", "Abstract", "Minimalist"),
        selectedFilters = listOf("Anime", "Natural", "Landscape", "Abstract", "Minimalist"),
        onFilterSelected = {}
    )
}