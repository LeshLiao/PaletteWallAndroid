package com.palettex.palettewall.data.remote.dto

data class WallpaperDto(
    val id: String,
    val itemId: String,
    val name: String,
    val price: Double,
    val freeDownload: Boolean,
    val stars: Int,
    val photoType: String,
    val tags: List<String>,
    val thumbnail: String,
    val preview: String,
    val imageList: List<ImageItemDto>,
    val downloadList: List<DownloadItemDto>,
    val createdAt: String,
    val updatedAt: String,
    val version: Int
)

data class ImageItemDto(
    val type: String,
    val resolution: String,
    val link: String,
    val blob: String
)

data class DownloadItemDto(
    val size: String,
    val ext: String,
    val link: String
)