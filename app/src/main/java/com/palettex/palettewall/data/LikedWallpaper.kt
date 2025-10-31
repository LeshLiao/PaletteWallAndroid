package com.palettex.palettewall.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_wallpapers")
data class LikedWallpaper(
    @PrimaryKey
    val wallpaperId: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val isDarkThemeEnabled: Boolean = true
)