package com.palettex.palettewall.di

import android.content.Context
import com.palettex.palettewall.data.local.ImageCacheDataSource
import com.palettex.palettewall.data.remote.api.WallpaperApiService
import com.palettex.palettewall.data.repository.WallpaperRepositoryImpl
import com.palettex.palettewall.domain.repository.WallpaperRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideImageCacheDataSource(
        @ApplicationContext context: Context
    ): ImageCacheDataSource {
        return ImageCacheDataSource(context)
    }

    @Provides
    @Singleton
    fun provideWallpaperRepository(
        apiService: WallpaperApiService,
        imageCacheDataSource: ImageCacheDataSource
    ): WallpaperRepository {
        return WallpaperRepositoryImpl(
            apiService = apiService,
            imageCacheDataSource = imageCacheDataSource
        )
    }
}