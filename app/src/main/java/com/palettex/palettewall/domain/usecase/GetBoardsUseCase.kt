package com.palettex.palettewall.domain.usecase

import com.palettex.palettewall.domain.repository.WallpaperRepository

class GetBoardsUseCase(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(): Result<List<com.palettex.palettewall.data.remote.dto.BoardItem>> {
        return repository.getBoards()
    }
}

