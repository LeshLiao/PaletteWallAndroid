package com.palettex.palettewall.domain.usecase

import com.palettex.palettewall.domain.model.WallpaperItem
import com.palettex.palettewall.domain.repository.WallpaperRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPopularWallpapersUseCaseTest {

    private lateinit var repository: FakeWallpaperRepository
    private lateinit var useCase: GetPopularWallpapersUseCase

    @Before
    fun setup() {
        repository = FakeWallpaperRepository()
        useCase = GetPopularWallpapersUseCase(repository)
    }

    @Test
    fun `invoke returns success with wallpapers when repository succeeds`() {
        runBlocking {
            // Given
            val count = 16
            val expectedWallpapers = listOf(
                createMockWallpaper("1"),
                createMockWallpaper("2")
            )
            repository.popularWallpapers = expectedWallpapers

            // When
            val result = useCase(count)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedWallpapers, result.getOrNull())
        }
    }

    @Test
    fun `invoke returns failure when repository fails`() {
        runBlocking {
            // Given
            val count = 16
            val error = Exception("Network error")
            repository.shouldFail = true
            repository.error = error

            // When
            val result = useCase(count)

            // Then
            assertTrue(result.isFailure)
            assertEquals(error.message, result.exceptionOrNull()?.message)
        }
    }

    private fun createMockWallpaper(id: String): WallpaperItem {
        return WallpaperItem(
            id = id,
            itemId = id,
            name = "Test Wallpaper $id",
            price = 0.0,
            freeDownload = true,
            stars = 5,
            photoType = "static",
            tags = emptyList(),
            sizeOptions = emptyList(),
            thumbnail = "",
            preview = "",
            imageList = emptyList(),
            downloadList = emptyList(),
            createdAt = "",
            updatedAt = "",
            version = 1
        )
    }

    // Simple fake repository for testing
    private class FakeWallpaperRepository : WallpaperRepository {
        var popularWallpapers: List<WallpaperItem> = emptyList()
        var shouldFail = false
        var error: Exception? = null

        override suspend fun getPopularWallpapers(page: Int): Result<List<WallpaperItem>> {
            return if (shouldFail) {
                Result.failure(error ?: Exception("Test error"))
            } else {
                Result.success(popularWallpapers)
            }
        }

        // Other methods not used in this test - implement as needed
        override suspend fun searchWallpapers(query: String): Result<List<WallpaperItem>> {
            return Result.success(emptyList())
        }

        override suspend fun getWallpapers(): Result<List<WallpaperItem>> {
            return Result.success(emptyList())
        }

        override suspend fun getWallpapersByPage(
            page: Int,
            pageSize: Int,
            catalog: String
        ): Result<com.palettex.palettewall.data.remote.dto.PaginatedResponse> {
            return Result.success(
                com.palettex.palettewall.data.remote.dto.PaginatedResponse(
                    items = emptyList(),
                    currentPage = page,
                    totalPages = 1,
                    totalItems = 0,
                    hasMore = false
                )
            )
        }

        override suspend fun getCatalogs(): Result<List<com.palettex.palettewall.data.remote.dto.CatalogItem>> {
            return Result.success(emptyList())
        }

        override suspend fun getBoards(): Result<List<com.palettex.palettewall.data.remote.dto.BoardItem>> {
            return Result.success(emptyList())
        }

        override suspend fun getAppSettings(): Result<com.palettex.palettewall.data.remote.dto.AppSettings> {
            return Result.success(com.palettex.palettewall.data.remote.dto.AppSettings())
        }

        override suspend fun sendLogEvent(
            logEvent: com.palettex.palettewall.data.remote.dto.LogEventRequest
        ): Result<Unit> {
            return Result.success(Unit)
        }

        override fun getImageCacheList(): List<String> {
            return emptyList()
        }
    }
}

