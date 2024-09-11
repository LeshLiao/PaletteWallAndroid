package com.palettex.palettewall.network

import com.palettex.palettewall.model.WallpaperItem
import retrofit2.http.GET

interface WallpaperApiService {
    @GET("items/photoType/static")
    suspend fun getWallpapers(): List<WallpaperItem>

    @GET("items/tag/Anime")
    suspend fun getAnime(): List<WallpaperItem>

    @GET("items/tag/City")
    suspend fun getCity(): List<WallpaperItem>

    @GET("items/tag/Painting")
    suspend fun getPainting(): List<WallpaperItem>
}
