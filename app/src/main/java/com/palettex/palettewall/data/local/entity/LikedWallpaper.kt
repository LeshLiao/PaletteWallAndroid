package com.palettex.palettewall.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_wallpapers")
data class LikedWallpaper(
    @PrimaryKey
    val wallpaperId: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

