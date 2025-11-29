package com.palettex.palettewall.data.remote.dto.mappers

import com.palettex.palettewall.data.remote.dto.DownloadItemDto
import com.palettex.palettewall.data.remote.dto.ImageItemDto
import com.palettex.palettewall.data.remote.dto.WallpaperDto
import com.palettex.palettewall.domain.model.WallpaperItem
import com.palettex.palettewall.domain.model.DownloadItem
import com.palettex.palettewall.domain.model.ImageItem

fun WallpaperDto.toDomain(): WallpaperItem {
    return WallpaperItem(
        id = id,
        itemId = itemId,
        name = name,
        price = price,
        freeDownload = freeDownload,
        stars = stars,
        photoType = photoType,
        tags = tags,
        thumbnail = thumbnail,
        preview = preview,
        imageList = imageList.map { it.toDomain() },
        downloadList = downloadList.map { it.toDomain() },
        sizeOptions = imageList.map { it.resolution },
        createdAt = createdAt,
        updatedAt = updatedAt,
        version = version
    )
}

fun ImageItemDto.toDomain(): ImageItem {
    return ImageItem(
        type = type,
        resolution = resolution,
        link = link,
        blob = blob
    )
}

fun DownloadItemDto.toDomain(): DownloadItem {
    return DownloadItem(
        size = size,
        ext = ext,
        link = link
    )
}