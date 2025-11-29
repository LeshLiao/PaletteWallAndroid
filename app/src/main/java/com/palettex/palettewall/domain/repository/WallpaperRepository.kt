package com.palettex.palettewall.domain.repository

import com.palettex.palettewall.domain.model.WallpaperItem

interface WallpaperRepository {
    suspend fun searchWallpapers(query: String): Result<List<WallpaperItem>>
    suspend fun getWallpapers(): Result<List<WallpaperItem>>
    suspend fun getPopularWallpapers(page: Int): Result<List<WallpaperItem>>
    fun getImageCacheList(): List<String>
}