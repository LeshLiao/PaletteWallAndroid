package com.palettex.palettewall.domain.usecase

import com.palettex.palettewall.domain.repository.WallpaperRepository

class SendLogEventUseCase(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(logEvent: com.palettex.palettewall.data.remote.dto.LogEventRequest): Result<Unit> {
        return repository.sendLogEvent(logEvent)
    }
}

