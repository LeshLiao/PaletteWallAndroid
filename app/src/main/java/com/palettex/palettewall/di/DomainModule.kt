package com.palettex.palettewall.di

import com.palettex.palettewall.domain.repository.WallpaperRepository
import com.palettex.palettewall.domain.usecase.WallpaperUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    @ViewModelScoped
    fun provideWallpaperUseCase(
        repository: WallpaperRepository
    ): WallpaperUseCase {
        return WallpaperUseCase(repository)
    }
}