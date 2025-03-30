package com.palettex.palettewall.network

import com.palettex.palettewall.data.LogEventRequest
import com.palettex.palettewall.model.AppSettings
import com.palettex.palettewall.model.CatalogItem
import com.palettex.palettewall.model.PaginatedResponse
import com.palettex.palettewall.model.WallpaperItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WallpaperApiService {
    @GET("items/photoType/static")
    suspend fun getWallpapers(): List<WallpaperItem>

    @GET("items/wallpapers/popular")
    suspend fun getPopular(
        @Query("number") page: Int
    ): List<WallpaperItem>

    @GET("items/tag/Anime")
    suspend fun getAnime(): List<WallpaperItem>

    @GET("items/tag/City")
    suspend fun getCity(): List<WallpaperItem>

    @GET("items/tag/Painting")
    suspend fun getPainting(): List<WallpaperItem>

    // Function to dynamically pass a parameter
    @GET("items/tag/{param}")
    suspend fun getWallpaperBy(@Path("param") param: String): List<WallpaperItem>

    @GET("items/catalogs/top")
    suspend fun getCatalogs(): List<CatalogItem>

    @GET("items/settings/init")
    suspend fun getAppSettings(): AppSettings

    @POST("items/log")
    suspend fun sendLogEvent(@Body logEvent: LogEventRequest)

    @GET("items/wallpapers/page")
    suspend fun getWallpapersByPage(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("catalog") catalog: String
    ): PaginatedResponse
}
