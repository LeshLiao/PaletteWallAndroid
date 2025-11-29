package com.palettex.palettewall.data.repository

import com.palettex.palettewall.data.local.ImageCacheDataSource
import com.palettex.palettewall.data.remote.api.WallpaperApiService
import com.palettex.palettewall.data.remote.dto.mappers.toDomain
import com.palettex.palettewall.domain.model.WallpaperItem
import com.palettex.palettewall.domain.repository.WallpaperRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WallpaperRepositoryImpl(
    private val apiService: WallpaperApiService,
    private val imageCacheDataSource: ImageCacheDataSource
) : WallpaperRepository {

    override suspend fun searchWallpapers(query: String): Result<List<WallpaperItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWallpapersBySearch(query)
                val wallpapers = response.map { it.toDomain() }
                Result.success(wallpapers)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getWallpapers(): Result<List<WallpaperItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWallpapers()
                //val wallpapers = response.map { it.toDomain() }
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getPopularWallpapers(page: Int): Result<List<WallpaperItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPopular(page)
                //val wallpapers = response.map { it.toDomain() }
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun getImageCacheList(): List<String> {
        return imageCacheDataSource.getCachedImages()
    }
}