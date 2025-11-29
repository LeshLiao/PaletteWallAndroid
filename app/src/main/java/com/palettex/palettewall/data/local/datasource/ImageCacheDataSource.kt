package com.palettex.palettewall.data.local.datasource

import android.content.Context
import com.palettex.palettewall.data.local.model.ImageCacheList

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

