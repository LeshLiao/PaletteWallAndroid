package com.palettex.palettewall.domain.usecase

import com.palettex.palettewall.domain.repository.WallpaperRepository

class GetAppSettingsUseCase(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(): Result<com.palettex.palettewall.data.remote.dto.AppSettings> {
        return repository.getAppSettings()
    }
}

