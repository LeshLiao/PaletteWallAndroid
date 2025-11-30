package com.palettex.palettewall.data.repository

import com.palettex.palettewall.data.local.datasource.ImageCacheDataSource
import com.palettex.palettewall.data.remote.api.WallpaperApiService
import com.palettex.palettewall.data.remote.dto.AppSettings
import com.palettex.palettewall.data.remote.dto.BoardItem
import com.palettex.palettewall.data.remote.dto.CatalogItem
import com.palettex.palettewall.data.remote.dto.LogEventRequest
import com.palettex.palettewall.data.remote.dto.PaginatedResponse
import com.palettex.palettewall.data.remote.mappers.toDomain
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
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getWallpapersByPage(page: Int, pageSize: Int, catalog: String): Result<PaginatedResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWallpapersByPage(page, pageSize, catalog)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCatalogs(): Result<List<CatalogItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCatalogs()
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getBoards(): Result<List<BoardItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getBoards()
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getAppSettings(): Result<AppSettings> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAppSettings()
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun sendLogEvent(logEvent: LogEventRequest): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.sendLogEvent(logEvent)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun getImageCacheList(): List<String> {
        return imageCacheDataSource.getCachedImages()
    }
}
