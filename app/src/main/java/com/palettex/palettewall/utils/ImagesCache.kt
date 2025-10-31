package com.palettex.palettewall.utils

import android.content.Context
import com.palettex.palettewall.data.ImageCacheList

fun String.getImageSourceFromAssets(context: Context, imageCacheList: ImageCacheList): String {
    val cachedImageName = imageCacheList.getCachedImagePath(this)
    return if (cachedImageName != null) {
        try {
            context.assets.open(cachedImageName).close()
            "file:///android_asset/$cachedImageName"
        } catch (e: Exception) {
            this
        }
    } else this
}