package com.palettex.palettewall.model

import com.palettex.palettewall.domain.model.WallpaperItem

data class PaginatedResponse(
    val items: List<WallpaperItem>,
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val hasMore: Boolean
)