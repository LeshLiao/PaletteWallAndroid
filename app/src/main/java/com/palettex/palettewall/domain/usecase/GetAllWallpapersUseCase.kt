package com.palettex.palettewall.domain.usecase

import com.palettex.palettewall.domain.model.WallpaperItem
import com.palettex.palettewall.domain.repository.WallpaperRepository

class GetAllWallpapersUseCase(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(): Result<List<WallpaperItem>> {
        return repository.getWallpapers()
    }
}

