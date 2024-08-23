package com.palettex.palettewall.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.network.RetrofitInstance
import com.palettex.palettewall.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch




class TopBarViewModel : ViewModel() {
    var isTopBarVisible by mutableStateOf(true)
        private set

    fun onScroll(deltaY: Float) {
        if (deltaY > 0) {
            // Scrolling down
            isTopBarVisible = false
        } else if (deltaY < 0) {
            // Scrolling up
            isTopBarVisible = true
        }
    }
}

class WallpaperViewModel() : ViewModel() {

    private val _wallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val wallpapers: StateFlow<List<WallpaperItem>> = _wallpapers

    init {
        fetchWallpapers()
    }

    private fun fetchWallpapers() {
        Log.d("GDT","fetchWallpapers()")
        viewModelScope.launch {
            try {
                _wallpapers.value = RetrofitInstance.api.getWallpapers()
                Log.d("GDT", _wallpapers.value.toString())
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    fun getThumbnailUrl(wallpaper: WallpaperItem): String {
        return if (wallpaper.thumbnail.contains("https")) {
            wallpaper.thumbnail
        } else {
            "https://www.palettex.ca/images/items/${wallpaper.itemId}/${wallpaper.thumbnail}"
        }
    }
}
