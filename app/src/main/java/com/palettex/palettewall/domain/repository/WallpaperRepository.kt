package com.palettex.palettewall.domain.repository

import com.palettex.palettewall.domain.model.WallpaperItem

interface WallpaperRepository {
    suspend fun searchWallpapers(query: String): Result<List<WallpaperItem>>
    suspend fun getWallpapers(): Result<List<WallpaperItem>>
    suspend fun getPopularWallpapers(page: Int): Result<List<WallpaperItem>>
    suspend fun getWallpapersByPage(page: Int, pageSize: Int, catalog: String): Result<com.palettex.palettewall.data.remote.dto.PaginatedResponse>
    suspend fun getCatalogs(): Result<List<com.palettex.palettewall.data.remote.dto.CatalogItem>>
    suspend fun getBoards(): Result<List<com.palettex.palettewall.data.remote.dto.BoardItem>>
    suspend fun getAppSettings(): Result<com.palettex.palettewall.data.remote.dto.AppSettings>
    suspend fun sendLogEvent(logEvent: com.palettex.palettewall.data.remote.dto.LogEventRequest): Result<Unit>
    fun getImageCacheList(): List<String>
}