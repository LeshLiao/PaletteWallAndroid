package com.palettex.palettewall.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palettex.palettewall.model.AppSettings
import com.palettex.palettewall.model.CatalogItem
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.data.LogEventRequest
import retrofit2.http.Path
import java.util.Locale

open class WallpaperViewModel(
    private val analytics: FirebaseAnalytics = Firebase.analytics
) : ViewModel() {

    companion object {
        private val TAG = WallpaperViewModel::class.java.simpleName + "_GDT"
    }

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings

    private val _catalogs = MutableStateFlow<List<CatalogItem>>(emptyList())
    val catalogs: StateFlow<List<CatalogItem>> = _catalogs

    private val _allWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val allWallpapers: StateFlow<List<WallpaperItem>> = _allWallpapers

    private val _wallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val wallpapers: StateFlow<List<WallpaperItem>> = _wallpapers

    private val _carouselWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val carouselWallpapers: StateFlow<List<WallpaperItem>> = _carouselWallpapers

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

    private val _scrollToTopTrigger = MutableStateFlow(false)
    val scrollToTopTrigger: StateFlow<Boolean> = _scrollToTopTrigger

    private val _currentImage = MutableStateFlow("")
    var currentImage: StateFlow<String> = _currentImage

    private val _firstSelectedColor = MutableStateFlow<Color?>(null)
    val firstSelectedColor: StateFlow<Color?> = _firstSelectedColor.asStateFlow()

    private val _secondSelectedColor = MutableStateFlow<Color?>(null)
    val secondSelectedColor: StateFlow<Color?> = _secondSelectedColor.asStateFlow()

    init {
        viewModelScope.launch {
            getAppSettings()
            getCatalogs()
            fetchShuffledWallpapersApi()
            setCurrentCatalog("Wallpapers") // main catalog
        }
    }

    fun scrollToTop() {
        viewModelScope.launch {
            _scrollToTopTrigger.value = true
            setCurrentCatalog("Wallpapers")
        }
    }

    fun setBottomAdsLoaded(isLoaded: Boolean) {
        _isBottomAdsLoaded.value = isLoaded
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

    fun setScrollToTopTrigger(status: Boolean) {
        _scrollToTopTrigger.value = status
    }

    fun updateCurrentCatalog() {
        if (_currentCatalog.value == "Wallpapers") {
            fetchShuffledWallpapersApi()
        } else {
            fetchWallpaperBy(_currentCatalog.value)
        }
    }

    fun fetchShuffledWallpapersApi() {
        viewModelScope.launch {
            try {
                // Fetch the wallpapers and shuffle the list before assigning
                _allWallpapers.value = RetrofitInstance.api.getWallpapers().shuffled()
                _wallpapers.value = _allWallpapers.value
                _topTenWallpapers.value = _wallpapers.value.shuffled().take(10)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun showCurrentAllWallpaper() {
        _wallpapers.value = _allWallpapers.value
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

    fun setThumbnailImageByItemId(itemId: String) {
        val wallpaper = _allWallpapers.value.find { it.itemId == itemId }
        if (wallpaper != null) {
            if (wallpaper.thumbnail.contains("https")) {
                _currentImage.value = wallpaper.thumbnail
            } else {
                _currentImage.value = "https://www.palettex.ca/images/items/${wallpaper.itemId}/${wallpaper.thumbnail}"
            }
        }
    }

    fun getThumbnailByItemId(itemId: String): String{
        // Search for the WallpaperItem with the matching itemId
        val wallpaper = _allWallpapers.value.find { it.itemId == itemId }

        if (wallpaper == null) return ""

        return if (wallpaper.thumbnail.contains("https")) {
            wallpaper.thumbnail
        } else {
            "https://www.palettex.ca/images/items/${wallpaper.itemId}/${wallpaper.thumbnail}"
        }

    }

    fun getDownloadListLinkByItemId(itemId: String): String? {
        val wallpaperItem = _allWallpapers.value.find { it.itemId == itemId }
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
                Log.e(TAG, "Error(getCatalogs):$e")
            }
        }
    }

    private fun getAppSettings() {
        viewModelScope.launch {
            try {
                _appSettings.value = RetrofitInstance.api.getAppSettings()
            } catch (e: Exception) {
                Log.e(TAG, "Error(getAppSettings):$e")
            }
        }
    }

    fun sendLogEvent(itemId: String, eventType: String) {
        viewModelScope.launch {
            try {
                val request = LogEventRequest(
                    itemId = itemId,
                    eventType = eventType,
                    manufacturer = Build.MANUFACTURER,
                    model = Build.MODEL,
                    release = Build.VERSION.RELEASE,
                    sdk = Build.VERSION.SDK_INT.toString(),
                    country = Locale.getDefault().country,
                )

                RetrofitInstance.api.sendLogEvent(request)
            } catch (e: Exception) {
                Log.e(TAG, "Error sendLogEvent(): $e")
            }
        }
    }

    // Add these extension functions and methods to your WallpaperViewModel class
    private fun Color.toHexString(): String {
        return String.format(
            "#%02X%02X%02X",
            (red * 255).toInt(),
            (green * 255).toInt(),
            (blue * 255).toInt()
        )
    }

    private fun String.toColor(): Color? {
        return try {
            if (this.startsWith("#") && this.length == 7) {
                Color(android.graphics.Color.parseColor(this))
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateColorSimilarity(color1: Color, color2: Color): Double {
        // Convert Float to Double using .toDouble()
        val rDiff = (color1.red - color2.red).toDouble()
        val gDiff = (color1.green - color2.green).toDouble()
        val bDiff = (color1.blue - color2.blue).toDouble()

        return sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff)
    }

    private fun calculateDualColorSimilarity(
        targetColor1: Color,
        targetColor2: Color,
        wallpaperColors: List<Color>
    ): Double {
        if (wallpaperColors.isEmpty()) return Double.MAX_VALUE

        // Find the best matching pair of colors
        var minTotalDistance = Double.MAX_VALUE

        for (wallpaperColor in wallpaperColors) {
            val dist1 = calculateColorSimilarity(targetColor1, wallpaperColor)
            for (otherWallpaperColor in wallpaperColors) {
                if (otherWallpaperColor != wallpaperColor) {
                    val dist2 = calculateColorSimilarity(targetColor2, otherWallpaperColor)
                    val totalDist = dist1 + dist2
                    if (totalDist < minTotalDistance) {
                        minTotalDistance = totalDist
                    }
                }
            }
        }
        return minTotalDistance
    }

    private fun getWallpaperColors(wallpaper: WallpaperItem): List<Color> {
        return wallpaper.tags
            .filter { it.startsWith("#") }
            .mapNotNull { it.toColor() }
    }

    fun updateFilteredWallpapers() {
        viewModelScope.launch {
            val first = firstSelectedColor.value
            val second = secondSelectedColor.value

            val filteredList = when {
                first != null && second != null -> {
                    // Filter by both colors
                    _allWallpapers.value
                        .map { wallpaper ->
                            val wallpaperColors = getWallpaperColors(wallpaper)
                            val similarity = calculateDualColorSimilarity(first, second, wallpaperColors)
                            wallpaper to similarity
                        }
                        .sortedBy { it.second }
                        .take(10)
                        .map { it.first }
                }
                first != null -> {
                    // Filter by first color only
                    _allWallpapers.value
                        .map { wallpaper ->
                            val wallpaperColors = getWallpaperColors(wallpaper)
                            val similarity = wallpaperColors.minOfOrNull {
                                calculateColorSimilarity(first, it)
                            } ?: Double.MAX_VALUE
                            wallpaper to similarity
                        }
                        .sortedBy { it.second }
                        .take(10)
                        .map { it.first }
                }
                second != null -> {
                    // Filter by second color only
                    _allWallpapers.value
                        .map { wallpaper ->
                            val wallpaperColors = getWallpaperColors(wallpaper)
                            val similarity = wallpaperColors.minOfOrNull {
                                calculateColorSimilarity(second, it)
                            } ?: Double.MAX_VALUE
                            wallpaper to similarity
                        }
                        .sortedBy { it.second }
                        .take(10)
                        .map { it.first }
                }
                else -> {
                    // No color selected - show 30 random wallpapers
                    _allWallpapers.value.shuffled().take(30)
                }
            }
            _carouselWallpapers.value = filteredList
        }
    }

    // Update the color setter methods to trigger filtering
    fun setFirstSelectedColor(color: Color?) {
        _firstSelectedColor.value = color
        updateFilteredWallpapers()
    }

    fun setSecondSelectedColor(color: Color?) {
        _secondSelectedColor.value = color
        updateFilteredWallpapers()
    }

    // Add these properties to WallpaperViewModel class
    private val _currentCarouselPage = MutableStateFlow(0)
    val currentCarouselPage: StateFlow<Int> = _currentCarouselPage.asStateFlow()

    fun setCurrentCarouselPage(page: Int) {
        _currentCarouselPage.value = page
    }

    // Firebase Events
    sealed class AnalyticsEvent(val eventName: String) {
        data class DownloadFree(val itemId: String) : AnalyticsEvent("download_free")
        data class Category(val category: String) : AnalyticsEvent("category_$category")
        data class Share(val itemId: String) : AnalyticsEvent(FirebaseAnalytics.Event.SHARE)
        data class Like(val itemId: String) : AnalyticsEvent("like_button")
    }

    private fun logEvent(event: AnalyticsEvent) {
        try {
            val bundle = Bundle().apply {
                when (event) {
                    is AnalyticsEvent.DownloadFree -> {
                        putString(FirebaseAnalytics.Param.ITEM_ID, event.itemId)
                        putString(FirebaseAnalytics.Param.CONTENT_TYPE, "wallpaper")
                    }
                    is AnalyticsEvent.Category -> {
                        putString(FirebaseAnalytics.Param.ITEM_CATEGORY, event.category)
                    }
                    is AnalyticsEvent.Share -> {
                        putString(FirebaseAnalytics.Param.ITEM_ID, event.itemId)
                    }
                    is AnalyticsEvent.Like -> {
                        putString(FirebaseAnalytics.Param.ITEM_ID, event.itemId)
                    }
                }
            }

            if (BuildConfig.DEBUG_MODE) {
                Log.d(TAG, "DEBUG_MODE event: ${event.eventName}")
            } else {
                analytics.logEvent(event.eventName, bundle)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to firebase log event ${event.eventName}", e)
        }
    }

    fun firebaseDownloadFreeEvent(itemId: String) = logEvent(AnalyticsEvent.DownloadFree(itemId))
    fun firebaseCatalogEvent(category: String) = logEvent(AnalyticsEvent.Category(category))
    fun firebaseShareEvent(itemId: String) = logEvent(AnalyticsEvent.Share(itemId))
    fun firebaseLikeEvent(itemId: String) = logEvent(AnalyticsEvent.Like(itemId))
}