package com.palettex.palettewall.network

import com.palettex.palettewall.model.WallpaperItem
import retrofit2.http.GET

interface WallpaperApiService {
    @GET("items/photoType/static")
    suspend fun getWallpapers(): List<WallpaperItem>
}
