package com.palettex.palettewall.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.palettex.palettewall.data.local.entity.LikedWallpaper
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedWallpaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedWallpaper(likedWallpaper: LikedWallpaper)

    @Delete
    suspend fun deleteLikedWallpaper(likedWallpaper: LikedWallpaper)

    @Query("SELECT * FROM liked_wallpapers ORDER BY timestamp DESC")
    fun getAllLikedWallpapers(): Flow<List<LikedWallpaper>>

    @Query("SELECT EXISTS(SELECT 1 FROM liked_wallpapers WHERE wallpaperId = :wallpaperId)")
    fun isWallpaperLiked(wallpaperId: String): Flow<Boolean>
}

