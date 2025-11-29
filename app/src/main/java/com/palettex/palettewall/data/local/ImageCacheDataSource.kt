package com.palettex.palettewall.data.local

import android.content.Context
import com.palettex.palettewall.data.ImageCacheList

class ImageCacheDataSource(private val context: Context) {

    private val imageCacheList: ImageCacheList by lazy {
        try {
            ImageCacheList.fromAssets(context)
        } catch (e: Exception) {
            ImageCacheList(emptyMap())
        }
    }

    fun getCachedImages(): List<String> {
        return imageCacheList.getAllCachedFileNames()
    }
}