package com.palettex.palettewall.repository

import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.network.WallpaperApiService

class WallpaperRepository(private val apiService: WallpaperApiService) {
    suspend fun getWallpapers(): List<WallpaperItem> {
        return apiService.getWallpapers()
    }
}

