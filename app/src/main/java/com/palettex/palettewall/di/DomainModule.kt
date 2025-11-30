package com.palettex.palettewall.di

import com.palettex.palettewall.domain.repository.WallpaperRepository
import com.palettex.palettewall.domain.usecase.GetAllWallpapersUseCase
import com.palettex.palettewall.domain.usecase.GetAppSettingsUseCase
import com.palettex.palettewall.domain.usecase.GetBoardsUseCase
import com.palettex.palettewall.domain.usecase.GetCatalogsUseCase
import com.palettex.palettewall.domain.usecase.GetPopularWallpapersUseCase
import com.palettex.palettewall.domain.usecase.GetWallpapersByPageUseCase
import com.palettex.palettewall.domain.usecase.SendLogEventUseCase
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

    @Provides
    @ViewModelScoped
    fun provideGetPopularWallpapersUseCase(
        repository: WallpaperRepository
    ): GetPopularWallpapersUseCase {
        return GetPopularWallpapersUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllWallpapersUseCase(
        repository: WallpaperRepository
    ): GetAllWallpapersUseCase {
        return GetAllWallpapersUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetWallpapersByPageUseCase(
        repository: WallpaperRepository
    ): GetWallpapersByPageUseCase {
        return GetWallpapersByPageUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCatalogsUseCase(
        repository: WallpaperRepository
    ): GetCatalogsUseCase {
        return GetCatalogsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetBoardsUseCase(
        repository: WallpaperRepository
    ): GetBoardsUseCase {
        return GetBoardsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAppSettingsUseCase(
        repository: WallpaperRepository
    ): GetAppSettingsUseCase {
        return GetAppSettingsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideSendLogEventUseCase(
        repository: WallpaperRepository
    ): SendLogEventUseCase {
        return SendLogEventUseCase(repository)
    }
}