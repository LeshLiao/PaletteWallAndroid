package com.palettex.palettewall.viewmodel

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.data.LikedWallpaper
import com.palettex.palettewall.data.LogEventRequest
import com.palettex.palettewall.model.AppSettings
import com.palettex.palettewall.model.CatalogItem
import com.palettex.palettewall.model.PaginatedResponse
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sqrt

open class WallpaperViewModel(
    private val analytics: FirebaseAnalytics = Firebase.analytics
) : ViewModel() {

    companion object {
        private val TAG = WallpaperViewModel::class.java.simpleName + "_GDT"
        private const val DEFAULT_PAGE_SIZE = 12
    }

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings

    private val _catalogs = MutableStateFlow<List<CatalogItem>>(emptyList())
    val catalogs: StateFlow<List<CatalogItem>> = _catalogs

    private val _allWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val allWallpapers: StateFlow<List<WallpaperItem>> = _allWallpapers

    private val _wallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val wallpapers: StateFlow<List<WallpaperItem>> = _wallpapers

    private val _fullScreenWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val fullScreenWallpapers: StateFlow<List<WallpaperItem>> = _fullScreenWallpapers

    private val _carouselWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val carouselWallpapers: StateFlow<List<WallpaperItem>> = _carouselWallpapers

    private val _carouselAllWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val carouselAllWallpapers: StateFlow<List<WallpaperItem>> = _carouselAllWallpapers

    private val _likeWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val likeWallpapers: StateFlow<List<WallpaperItem>> = _likeWallpapers

    private val _downloadBtnStatus = MutableStateFlow(0)
    val downloadBtnStatus: StateFlow<Int> = _downloadBtnStatus

    private val _loadAdsBtnStatus = MutableStateFlow(false)
    val loadAdsBtnStatus: StateFlow<Boolean> = _loadAdsBtnStatus

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

    private val _isCurrentFreeDownload = MutableStateFlow(false)
    var isCurrentFreeDownload: StateFlow<Boolean> = _isCurrentFreeDownload

    private val _firstSelectedColor = MutableStateFlow<Color?>(null)
    val firstSelectedColor: StateFlow<Color?> = _firstSelectedColor.asStateFlow()

    private val _secondSelectedColor = MutableStateFlow<Color?>(null)
    val secondSelectedColor: StateFlow<Color?> = _secondSelectedColor.asStateFlow()

    private val _isRemoteConfigInitialized = MutableStateFlow(false)
    val isRemoteConfigInitialized: StateFlow<Boolean> = _isRemoteConfigInitialized

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Pagination properties
    private var currentPage = 1
    private var isLastPage = false
    private var pageSize = DEFAULT_PAGE_SIZE

    init {
        viewModelScope.launch {
            getAppSettings()
            getCatalogs()
            fetchTopTenWallpapers()
            setCurrentCatalog("Wallpapers") // main catalog
            loadMoreWallpapers()
            fetchAllWallpapersToCarouselAll()
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

    fun updateLoadAdsBtnStatus(status: Boolean) {
        _loadAdsBtnStatus.value = status
    }

    fun setCurrentCatalog(catalog: String) {
        Log.d("GDT","setCurrentCatalog")
        if (_currentCatalog.value != catalog) {
            _currentCatalog.value = catalog
        }
    }

    private fun resetPagination() {
        currentPage = 1
        isLastPage = false
        _wallpapers.value = emptyList()
    }

    fun setScrollToTopTrigger(status: Boolean) {
        _scrollToTopTrigger.value = status
    }

    fun setIsRemoteConfigInitialized(status: Boolean) {
        _isRemoteConfigInitialized.value = status
    }

    fun setFullScreenWallpaper(list: List<WallpaperItem>) {
        _fullScreenWallpapers.value = list
    }

    fun pullRefreshCurrentCatalog() {
        Log.d("GDT","pullRefreshCurrentCatalog")
        getAppSettings()
        getCatalogs()
        fetchTopTenWallpapers()
        resetPagination()
        loadMoreWallpapers()
    }

    private fun fetchTopTenWallpapers() {
        viewModelScope.launch {
            try {
                val popularWallpapers = RetrofitInstance.api.getPopular(10)
                _topTenWallpapers.value = popularWallpapers
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching wallpapers: ${e.message}")
            }
        }
    }

    fun fetchAllWallpapersToCarouselAll() {
        viewModelScope.launch {
            try {
                val allWallpapers = RetrofitInstance.api.getWallpapers().shuffled()
                _carouselAllWallpapers.value = allWallpapers
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching wallpapers: ${e.message}")
            }
        }
    }

    fun initLikeCollection(likeList: List<LikedWallpaper>) {
        Log.d("GDT","initLikeCollection")
        viewModelScope.launch {
            // Check if either list is empty to avoid unnecessary processing
            if (likeList.isEmpty() || _carouselAllWallpapers.value.isEmpty()) {
                _likeWallpapers.value = emptyList()
                return@launch
            }

            // Create a set of liked wallpaper IDs for faster lookup
            val likedIds = likeList.map { it.wallpaperId }.toSet()

            // Filter the wallpapers from carouselAllWallpapers that match the liked IDs
            val matchedWallpapers = _carouselAllWallpapers.value.filter { wallpaper ->
                likedIds.contains(wallpaper.itemId)
            }

            // Set the matched wallpapers to _likeWallpapers
            _likeWallpapers.value = matchedWallpapers
            Log.d(TAG, "Liked wallpapers initialized with ${matchedWallpapers.size} items")
        }
    }

    fun initFullScreenDataSource(catalog: String) {
        viewModelScope.launch {
            when (catalog) {
                "popular" -> {
                    _fullScreenWallpapers.value = _topTenWallpapers.value
                }
                "carousel" -> {
                    _fullScreenWallpapers.value = _carouselWallpapers.value
                }
                "like" -> {
                    _fullScreenWallpapers.value = _likeWallpapers.value
                }
                else -> {
                    // Default case - use current wallpapers from the main catalog
                    _fullScreenWallpapers.value = _wallpapers.value
                }
            }
            // Log the data source being used
            Log.d(TAG, "Initialized fullscreen data source from: $catalog with ${_fullScreenWallpapers.value.size} items")
        }
    }

    fun setThumbnailImageByItemId(itemId: String, type: String) {
        val wallpaper = _fullScreenWallpapers.value.find { it.itemId == itemId }
        if (wallpaper != null) {
            _isCurrentFreeDownload.value = wallpaper.freeDownload
            _currentImage.value = wallpaper.imageList.firstOrNull {
                it.type == type && it.link.isNotEmpty()
            }?.link ?: ""
        }
    }

    fun getDownloadListLinkByItemId(itemId: String): String? {
        val wallpaperItem = _allWallpapers.value.find { it.itemId == itemId }
        return wallpaperItem?.downloadList?.firstOrNull()?.link
    }

    fun fetchWallpaperBy(param: String) {
        Log.d("GDT","fetchWallpaperBy")
        viewModelScope.launch {
            try {
                resetPagination()
                setCurrentCatalog(param)
                loadMoreWallpapers()
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching wallpapers by $param: ${e.message}")
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
                    appVersion = _versionName.value,
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
            if (this.startsWith("#") && this.length >= 7) {  // Ensure at least "#RRGGBB"
                val validHex = this.take(7) // Extract only "#RRGGBB"
                Color(android.graphics.Color.parseColor(validHex))
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
            .mapNotNull { hexString ->
                val validHex = hexString.take(7) // Ensures only `#` + 6 characters
                try {
                    Color(android.graphics.Color.parseColor(validHex))
                } catch (e: IllegalArgumentException) {
                    null // Ignore invalid colors
                }
            }
    }


    fun updateFilteredWallpapers() {
        viewModelScope.launch {
            val first = firstSelectedColor.value
            val second = secondSelectedColor.value

            val filteredList = when {
                first != null && second != null -> {
                    // Filter by both colors
                    _carouselAllWallpapers.value
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
                    _carouselAllWallpapers.value
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
                    _carouselAllWallpapers.value
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
                    _carouselAllWallpapers.value.shuffled().take(30)
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

    // Add this method to your WallpaperViewModel class to check if more data should be loaded
    fun shouldLoadMore(): Boolean {
        return !isLastPage && !_isLoading.value
    }

    private suspend fun fetchWallpapers(page: Int, pageSize: Int): PaginatedResponse {
        return try {
            val catalog = _currentCatalog.value
            if (catalog == "Wallpapers" || catalog.isEmpty()) {
                RetrofitInstance.api.getWallpapersByPage(
                    page = page,
                    pageSize = pageSize,
                    catalog = ""
                )
            } else {
                RetrofitInstance.api.getWallpapersByPage(
                    page = page,
                    pageSize = pageSize,
                    catalog = catalog     // set filter by tag
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching wallpapers: ${e.message}")
            PaginatedResponse(emptyList(), page, 1, 0, false)
        }
    }

    // Replace your loadMoreWallpapers function with this improved version
    fun loadMoreWallpapers() {
        // First check if we're already loading or reached the last page
        if (isLastPage || _isLoading.value) {
            Log.d(TAG, "Skip loading: isLastPage=$isLastPage, isLoading=${_isLoading.value}")
            return
        }

        viewModelScope.launch {
            try {
                // Set loading state to true at the beginning
                _isLoading.value = true
                Log.d(TAG, "Loading page $currentPage with size $pageSize for catalog ${_currentCatalog.value}")

                // Fetch the next page of wallpapers
                val response = fetchWallpapers(currentPage, pageSize)

                // Update last page flag based on response
                isLastPage = !response.hasMore

                // Handle empty response
                if (response.items.isEmpty()) {
                    Log.d(TAG, "No more wallpapers to load")
                    isLastPage = true
                    return@launch
                }

                // Determine the new list based on whether this is the first page or a subsequent page
                val currentList = if (currentPage == 1) {
                    // First page, replace the whole list
                    response.items
                } else {
                    // Subsequent pages, append to existing list
                    _wallpapers.value.toMutableList().apply {
                        addAll(response.items)
                    }
                }

                // Update the wallpapers list
                _wallpapers.value = currentList
                Log.d(TAG, "Loaded ${response.items.size} wallpapers, total: ${currentList.size}, hasMore: ${response.hasMore}")

                // Increment the page number for the next request
                currentPage++
            } catch (e: Exception) {
                Log.e(TAG, "Error loading more wallpapers: ${e.message}")
            } finally {
                // Always reset loading state when done, regardless of success or failure
                _isLoading.value = false
            }
        }
    }
}