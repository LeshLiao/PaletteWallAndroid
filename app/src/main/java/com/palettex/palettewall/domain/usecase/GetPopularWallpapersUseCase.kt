package com.palettex.palettewall.domain.usecase

import com.palettex.palettewall.domain.model.WallpaperItem
import com.palettex.palettewall.domain.repository.WallpaperRepository

class GetPopularWallpapersUseCase(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(count: Int): Result<List<WallpaperItem>> {
        return repository.getPopularWallpapers(count)
    }
}

