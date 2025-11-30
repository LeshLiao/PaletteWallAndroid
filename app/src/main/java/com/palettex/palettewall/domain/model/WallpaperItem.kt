package com.palettex.palettewall.domain.model

data class WallpaperItem(
    val id: String,
    val itemId: String,
    val name: String,
    val price: Double,
    val freeDownload: Boolean,
    val stars: Int,
    val photoType: String,
    val tags: List<String>,
    val sizeOptions: List<String>,
    val thumbnail: String,
    val preview: String,
    val imageList: List<ImageItem>,
    val downloadList: List<DownloadItem>,
    val createdAt: String,
    val updatedAt: String,
    val version: Int
)

data class ImageItem(
    val type: String,
    val resolution: String,
    val link: String,
    val blob: String
)

data class DownloadItem(
    val size: String,
    val ext: String,
    val link: String
)