package com.palettex.palettewall.domain.usecase

import com.palettex.palettewall.domain.repository.WallpaperRepository

class GetWallpapersByPageUseCase(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(
        page: Int,
        pageSize: Int,
        catalog: String
    ): Result<com.palettex.palettewall.data.remote.dto.PaginatedResponse> {
        return repository.getWallpapersByPage(page, pageSize, catalog)
    }
}

