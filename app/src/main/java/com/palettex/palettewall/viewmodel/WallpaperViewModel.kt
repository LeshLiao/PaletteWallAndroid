package com.palettex.palettewall.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class WallpaperViewModel() : ViewModel() {

    private val _wallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val wallpapers: StateFlow<List<WallpaperItem>> = _wallpapers

    init {
        fetchShuffledWallpapersApi()
    }

    fun fetchShuffledWallpapersApi() {
        viewModelScope.launch {
            try {
                // Fetch the wallpapers and shuffle the list before assigning
                _wallpapers.value = RetrofitInstance.api.getWallpapers().shuffled()
                val test = 0
            } catch (e: Exception) {
                // Handle the exception (e.g., log error)
            }
        }
    }

    fun fetchAnimeApi() {
        viewModelScope.launch {
            try {
                _wallpapers.value = RetrofitInstance.api.getAnime()
            } catch (e: Exception) {
            }
        }
    }

    fun fetchCityApi() {
        viewModelScope.launch {
            try {
                _wallpapers.value = RetrofitInstance.api.getCity()
            } catch (e: Exception) {
            }
        }
    }

    fun fetchPaintingApi() {
        viewModelScope.launch {
            try {
                _wallpapers.value = RetrofitInstance.api.getPainting()
            } catch (e: Exception) {
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

    fun getThumbnailByItemId(itemId: String): String{
        // Search for the WallpaperItem with the matching itemId
        val wallpaper = _wallpapers.value.find { it.itemId == itemId }

        if (wallpaper == null) return ""

        return if (wallpaper.thumbnail.contains("https")) {
            wallpaper.thumbnail
        } else {
            "https://www.palettex.ca/images/items/${wallpaper.itemId}/${wallpaper.thumbnail}"
        }

    }

    fun getDownloadListLinkByItemId(itemId: String): String? {
        val wallpaperItem = _wallpapers.value.find { it.itemId == itemId }
        return wallpaperItem?.downloadList?.firstOrNull()?.link
    }
}
