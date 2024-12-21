package com.palettex.palettewall.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palettex.palettewall.model.AppSettings
import com.palettex.palettewall.model.CatalogItem
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class WallpaperViewModel() : ViewModel() {

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings

    private val _catalogs = MutableStateFlow<List<CatalogItem>>(emptyList())
    val catalogs: StateFlow<List<CatalogItem>> = _catalogs

    private val _wallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val wallpapers: StateFlow<List<WallpaperItem>> = _wallpapers

    private val _downloadBtnStatus = MutableStateFlow(0)
    val downloadBtnStatus: StateFlow<Int> = _downloadBtnStatus

    private val _isFullScreen = MutableStateFlow(false)
    val isFullScreen: StateFlow<Boolean> = _isFullScreen

    private val _versionName = MutableStateFlow("")
    val versionName: StateFlow<String> = _versionName

    private val _isBottomAdsLoaded = MutableStateFlow(false)
    val isBottomAdsLoaded: StateFlow<Boolean> = _isBottomAdsLoaded

    private val _topTenWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val topTenWallpapers: StateFlow<List<WallpaperItem>> = _topTenWallpapers

    private val _currentCatalog = MutableStateFlow<String>("")
    val currentCatalog: StateFlow<String> = _currentCatalog

    init {
        viewModelScope.launch {
            getAppSettings()
            getCatalogs()
            fetchShuffledWallpapersApi()
            setCurrentCatalog("Wallpapers") // main catalog
        }
    }

    fun setVersionName(version: String) {
        _versionName.value = version
    }

    fun setFullScreenStatus(status: Boolean) {
        _isFullScreen.value = status
    }

    // Example method to update download button status
    fun updateDownloadBtnStatus(status: Int) {
        _downloadBtnStatus.value = status
    }

    fun setCurrentCatalog(status: String) {
        _currentCatalog.value = status
    }

    fun fetchShuffledWallpapersApi() {
        viewModelScope.launch {
            try {
                // Fetch the wallpapers and shuffle the list before assigning
                _wallpapers.value = RetrofitInstance.api.getWallpapers().shuffled()

                // get 10 random wallpapers and shuffle it.
                // Assign to _topTenWallpapers only if it is empty
                if (_topTenWallpapers.value.isEmpty()) {
                    _topTenWallpapers.value = _wallpapers.value.shuffled().take(10)
                }
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

    fun fetchWallpaperBy(param: String) {
        viewModelScope.launch {
            try {
                _wallpapers.value = RetrofitInstance.api.getWallpaperBy(param)
            } catch (e: Exception) {
            }
        }
    }

    private fun getCatalogs() {
        viewModelScope.launch {
            try {
                _catalogs.value = RetrofitInstance.api.getCatalogs()
            } catch (e: Exception) {
                Log.e("GDT", "Error(getCatalogs):$e")
            }
        }
    }

    private fun getAppSettings() {
        viewModelScope.launch {
            try {
                _appSettings.value = RetrofitInstance.api.getAppSettings()
            } catch (e: Exception) {
                Log.e("GDT", "Error(getAppSettings):$e")
            }
        }
    }
}
