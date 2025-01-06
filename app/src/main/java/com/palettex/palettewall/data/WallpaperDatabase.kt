package com.palettex.palettewall.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LikedWallpaper::class], version = 1)
abstract class WallpaperDatabase : RoomDatabase() {
    abstract fun likedWallpaperDao(): LikedWallpaperDao

    companion object {
        @Volatile
        private var INSTANCE: WallpaperDatabase? = null

        fun getDatabase(context: Context): WallpaperDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WallpaperDatabase::class.java,
                    "wallpaper_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}