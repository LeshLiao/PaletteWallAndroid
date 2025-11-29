package com.palettex.palettewall.domain.usecase

import com.palettex.palettewall.domain.model.WallpaperItem
import com.palettex.palettewall.domain.repository.WallpaperRepository

class WallpaperUseCase(
    private val repository: WallpaperRepository
) {
    suspend fun search(query: String): Result<List<WallpaperItem>> {
        if (query.isBlank()) return Result.success(emptyList())
        return repository.searchWallpapers(query.trim())
    }

    suspend fun getPopular(page: Int): Result<List<WallpaperItem>> {
        return repository.getPopularWallpapers(page)
    }

    suspend fun getAll(): Result<List<WallpaperItem>> {
        return repository.getWallpapers()
    }
}