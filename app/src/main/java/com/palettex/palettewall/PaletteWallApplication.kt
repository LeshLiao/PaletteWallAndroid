package com.palettex.palettewall

import android.app.Application
import android.util.Log
import com.palettex.palettewall.data.local.model.ImageCacheList
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PaletteWallApplication : Application() {

    companion object {
        const val TAG = "PaletteWallApplication" + "_GDT"
        lateinit var imageCacheList: ImageCacheList
            private set
    }

    override fun onCreate() {
        super.onCreate()
        initImageCache()
    }

    private fun initImageCache() {
        try {
            imageCacheList = ImageCacheList.fromAssets(applicationContext)
            val totalImages = imageCacheList.getAllCachedFileNames().size
            Log.d(TAG, "Loaded $totalImages cached images")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image cache", e)
            imageCacheList = ImageCacheList(emptyMap())
        }
    }
}