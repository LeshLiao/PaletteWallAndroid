package com.palettex.palettewall.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LikedWallpaper::class, UserSettings::class],
    version = 2,
    exportSchema = false
)
abstract class WallpaperDatabase : RoomDatabase() {
    abstract fun likedWallpaperDao(): LikedWallpaperDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: WallpaperDatabase? = null

        fun getDatabase(context: Context): WallpaperDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WallpaperDatabase::class.java,
                    "wallpaper_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}