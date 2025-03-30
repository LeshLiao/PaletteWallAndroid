package com.palettex.palettewall.model

data class PaginatedResponse(
    val items: List<WallpaperItem>,
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val hasMore: Boolean
)