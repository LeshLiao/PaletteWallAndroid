package com.palettex.palettewall.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.data.remote.api.WallpaperApiService

object RetrofitInstance {
    private val BASE_URL = BuildConfig.BASE_URL

    val api: WallpaperApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WallpaperApiService::class.java)
    }
}
