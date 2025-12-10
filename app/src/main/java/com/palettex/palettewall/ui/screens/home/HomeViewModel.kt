package com.palettex.palettewall.ui.screens.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.data.remote.dto.BoardItem
import com.palettex.palettewall.data.remote.dto.CatalogConfig
import com.palettex.palettewall.data.local.model.ImageCacheList
import com.palettex.palettewall.data.local.entity.LikedWallpaper
import com.palettex.palettewall.data.remote.dto.LogEventRequest
import com.palettex.palettewall.data.remote.dto.AppSettings
import com.palettex.palettewall.data.remote.dto.CatalogItem
import com.palettex.palettewall.data.remote.dto.PaginatedResponse
import com.palettex.palettewall.domain.model.WallpaperItem
import com.palettex.palettewall.domain.usecase.GetAllWallpapersUseCase
import com.palettex.palettewall.domain.usecase.GetAppSettingsUseCase
import com.palettex.palettewall.domain.usecase.GetBoardsUseCase
import com.palettex.palettewall.domain.usecase.GetCatalogsUseCase
import com.palettex.palettewall.domain.usecase.GetPopularWallpapersUseCase
import com.palettex.palettewall.domain.usecase.GetWallpapersByPageUseCase
import com.palettex.palettewall.domain.usecase.SendLogEventUseCase
import com.palettex.palettewall.ui.components.getImageSourceFromAssets
import com.palettex.palettewall.domain.utils.handleImageInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sqrt
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val analytics: FirebaseAnalytics,
    private val getPopularWallpapersUseCase: GetPopularWallpapersUseCase,
    private val getAllWallpapersUseCase: GetAllWallpapersUseCase,
    private val getWallpapersByPageUseCase: GetWallpapersByPageUseCase,
    private val getCatalogsUseCase: GetCatalogsUseCase,
    private val getBoardsUseCase: GetBoardsUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val sendLogEventUseCase: SendLogEventUseCase
) : ViewModel() {

    companion object {
        private val TAG = HomeViewModel::class.java.simpleName + "_GDT"
        private const val DEFAULT_PAGE_SIZE = 36
    }

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings

    private val _catalogs = MutableStateFlow<List<CatalogItem>>(emptyList())
    val catalogs: StateFlow<List<CatalogItem>> = _catalogs

    private val _boards = MutableStateFlow<List<BoardItem>>(emptyList())
    val boards: StateFlow<List<BoardItem>> = _boards

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

    private val _collectionWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val collectionWallpapers: StateFlow<List<WallpaperItem>> = _collectionWallpapers

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

    private val _currentCatalog = MutableStateFlow<String>("")
    val currentCatalog: StateFlow<String> = _currentCatalog

    private val _scrollToTopTrigger = MutableStateFlow(false)
    val scrollToTopTrigger: StateFlow<Boolean> = _scrollToTopTrigger

    private val _currentImage = MutableStateFlow("")
    var currentImage: StateFlow<String> = _currentImage

    private val _currentBlurImage = MutableStateFlow("")
    var currentBlurImage: StateFlow<String> = _currentBlurImage

    private val _isCurrentFreeDownload = MutableStateFlow(false)
    var isCurrentFreeDownload: StateFlow<Boolean> = _isCurrentFreeDownload

    private val _firstSelectedColor = MutableStateFlow<Color?>(null)
    val firstSelectedColor: StateFlow<Color?> = _firstSelectedColor.asStateFlow()

    private val _secondSelectedColor = MutableStateFlow<Color?>(null)
    val secondSelectedColor: StateFlow<Color?> = _secondSelectedColor.asStateFlow()

    private val _isRemoteConfigInitialized = MutableStateFlow(false)
    val isRemoteConfigInitialized: StateFlow<Boolean> = _isRemoteConfigInitialized

    private val _popularWallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val popularWallpapers: StateFlow<List<WallpaperItem>> = _popularWallpapers

    // Replace individual catalog flows with a map (using key as the map key)
    private val _catalogWallpapers = MutableStateFlow<Map<String, List<WallpaperItem>>>(emptyMap())
    val catalogWallpapers: StateFlow<Map<String, List<WallpaperItem>>> = _catalogWallpapers.asStateFlow()

    // Store catalog configurations
    private val _catalogConfigs = MutableStateFlow<List<CatalogConfig>>(emptyList())
    val catalogConfigs: StateFlow<List<CatalogConfig>> = _catalogConfigs.asStateFlow()

    // Add these properties to HomeViewModel
    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())
    val selectedTags: StateFlow<List<String>> = _selectedTags.asStateFlow()

    // Pagination properties
    private var currentPage = 1
    private var isLastPage = false
    private var pageSize = DEFAULT_PAGE_SIZE

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading
        .onStart {
            Log.d("GDT","onStart")

            setCurrentCatalog("Wallpapers") // main catalog

            resetPagination()
            getAppSettings()
            //getCatalogs()
            getBoards()
            fetchPopularWallpapers()
            loadMoreWallpapers()
            initAllCatalogs()

            fetchAllWallpapersToCarouselAll()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(60000L), // Wait 60 seconds before stopping flow (allows pending API refresh)
            false
        )

    init {

    }

    fun pullRefreshCurrentCatalog() {
        Log.d("GDT","pullRefreshCurrentCatalog")

        resetPagination()
        getAppSettings()
        //getCatalogs()
        getBoards()
        fetchPopularWallpapers()
        loadMoreWallpapers()
        initAllCatalogs()
    }

    fun initAllCatalogs() {
        initializeCatalogs(
            listOf(
                CatalogConfig(title = "Anime", key = "anime"),
                CatalogConfig(title = "Space", key = "space"),
                CatalogConfig(title = "Minimalistic", key = "minimalistic"),
                CatalogConfig(title = "Nature", key = "nature"),
                CatalogConfig(title = "Landscape", key = "landscape")
            )
        )
    }

    fun initializeCatalogs(catalogs: List<CatalogConfig>) {
        _catalogConfigs.value = catalogs

        _catalogWallpapers.value = catalogs.associate { it.key to emptyList<WallpaperItem>() }

        catalogs.forEach { config ->
            loadCatalogWallpapers(config.key)
        }
    }

    fun loadCatalogWallpapers(catalogKeyName: String) {
        viewModelScope.launch {
            try {
                val wallpapers = fetchSpecificWallpapers(0, 12, catalogKeyName).items
                _catalogWallpapers.value = _catalogWallpapers.value.toMutableMap().apply {
                    put(catalogKeyName, wallpapers)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading $catalogKeyName wallpapers", e)
            }
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

    private fun fetchPopularWallpapers() {
        viewModelScope.launch {
            getPopularWallpapersUseCase(16)
                .onSuccess { popularWallpapers ->
                    _popularWallpapers.value = popularWallpapers
                }
                .onFailure { error ->
                    Log.e(TAG, "Error fetching popular wallpapers: ${error.message}")
                }
        }
    }

    fun fetchAllWallpapersToCarouselAll() {
        viewModelScope.launch {
            getAllWallpapersUseCase()
                .onSuccess { allWallpapers ->
                    _carouselAllWallpapers.value = allWallpapers.shuffled()
                    // Calculate and print most common 20 tags (excluding color tags)
                    // printMostCommonTags(allWallpapers)
                }
                .onFailure { error ->
                    Log.e(TAG, "Error fetching all wallpapers: ${error.message}")
                }
        }
    }

    private fun printMostCommonTags(wallpapers: List<WallpaperItem>) {
        // Flatten all tags from all wallpapers, filter out color tags (starting with #)
        val tagFrequency = wallpapers
            .flatMap { it.tags }
            .filter { !it.startsWith("#") }
            .groupingBy { it.lowercase().trim() } // Normalize to lowercase and trim
            .eachCount()
            .toList()
            .sortedByDescending { it.second }
            .take(20)

        Log.d(TAG, "=".repeat(60))
        Log.d(TAG, "Most Common 20 Tags:")
        Log.d(TAG, "=".repeat(60))

        tagFrequency.forEachIndexed { index, (tag, count) ->
            Log.d(TAG, "${index + 1}. \"$tag\" - appears $count times")
        }

        Log.d(TAG, "=".repeat(60))
        Log.d(TAG, "Total unique tags (excluding colors): ${
            wallpapers.flatMap { it.tags }
                .filter { !it.startsWith("#") }
                .map { it.lowercase().trim() }
                .distinct()
                .size
        }")
        Log.d(TAG, "=".repeat(60))
    }

    fun initCollections(likeList: List<LikedWallpaper>, purchasedList: Set<String>) {
        // Log.d("GDT", "initLikeCollection")
        viewModelScope.launch {
            // Check if both lists are empty to avoid unnecessary processing
            if (likeList.isEmpty() && purchasedList.isEmpty()) {
                _collectionWallpapers.value = emptyList()
                return@launch
            }

            // Wait for carouselAllWallpapers to be loaded
            if (_carouselAllWallpapers.value.isEmpty()) {
                _collectionWallpapers.value = emptyList()
                return@launch
            }

            // Create a map of wallpaper ID to WallpaperItem for quick lookups
            val wallpaperItemsMap = _carouselAllWallpapers.value.associateBy { it.itemId }

            // Map each liked wallpaper to its corresponding WallpaperItem
            // This preserves the exact order from the database (newest first)
            val likedWallpapers = likeList.mapNotNull { likedWallpaper ->
                wallpaperItemsMap[likedWallpaper.wallpaperId]
            }

            // Map purchased wallpapers
            val purchasedWallpapers = purchasedList.mapNotNull { itemId ->
                wallpaperItemsMap[itemId]
            }

            // Combine both lists, removing duplicates (in case a wallpaper is both liked and purchased)
            // Liked wallpapers come first to preserve their timestamp order
            val combinedWallpapers = (purchasedWallpapers + likedWallpapers).distinctBy { it.itemId }

            // Set the combined list to _collectionWallpapers
            _collectionWallpapers.value = combinedWallpapers
            // Log.d(TAG, "Collection wallpapers initialized with ${combinedWallpapers.size} items (${likedWallpapers.size} liked, ${purchasedWallpapers.size} purchased)")
        }
    }

    fun initFullScreenDataSourceByList(list: List<WallpaperItem>) {
        viewModelScope.launch {
            _fullScreenWallpapers.value = list
        }
    }

    fun setThumbnailImageByItemId(itemId: String, type: String, context: Context, imageCacheList: ImageCacheList) {
        val wallpaper = _fullScreenWallpapers.value.find { it.itemId == itemId }
        if (wallpaper != null) {
            _isCurrentFreeDownload.value = wallpaper.freeDownload

            val imageUrl = wallpaper.imageList.firstOrNull {
                it.type == type && it.link.isNotEmpty()
            }?.link ?: ""

            val blurImageUrl = wallpaper.imageList.firstOrNull {
                it.type == "BL" && it.link.isNotEmpty()
            }?.link ?: ""

            val imageSource = imageUrl.getImageSourceFromAssets(context, imageCacheList)
            val blurSource = blurImageUrl.getImageSourceFromAssets(context, imageCacheList)

            _currentBlurImage.value = blurSource
            _currentImage.value = imageSource
        }
    }

    fun getDownloadListLinkByItemId(itemId: String): String? {
        val wallpaperItem = _fullScreenWallpapers.value.find { it.itemId == itemId }
        return wallpaperItem?.downloadList?.firstOrNull()?.link
    }

    fun getImageInfoByItemId(itemId: String): String {
        val wallpaperItem = _fullScreenWallpapers.value
            .find { it.itemId == itemId } ?: return ""

        return handleImageInfo(
            name = wallpaperItem.name,
            tags = wallpaperItem.tags
        )
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
            getCatalogsUseCase()
                .onSuccess { catalogs ->
                    _catalogs.value = catalogs
                }
                .onFailure { error ->
                    Log.e(TAG, "Error(getCatalogs): ${error.message}")
                }
        }
    }

    private fun getBoards() {
        viewModelScope.launch {
            getBoardsUseCase()
                .onSuccess { boards ->
                    _boards.value = boards
                }
                .onFailure { error ->
                    Log.e(TAG, "Error(getBoards): ${error.message}")
                }
        }
    }

    private fun getAppSettings() {
        viewModelScope.launch {
            getAppSettingsUseCase()
                .onSuccess { settings ->
                    _appSettings.value = settings
                }
                .onFailure { error ->
                    Log.e(TAG, "Error(getAppSettings): ${error.message}")
                }
        }
    }

    fun sendLogEvent(itemId: String, eventType: String) {
        viewModelScope.launch {
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

            sendLogEventUseCase(request)
                .onFailure { error ->
                    Log.e(TAG, "Error sendLogEvent(): ${error.message}")
                }
        }
    }

    // Add these extension functions and methods to your HomeViewModel class
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

    private val FILTER_OUT_NUBMER = 30

    // Update the filtering logic
    // Update the filtering logic with AND logic for tags
    fun updateFilteredWallpapers() {
        viewModelScope.launch {
            val first = firstSelectedColor.value
            val second = secondSelectedColor.value
            val tags = _selectedTags.value

            val filteredList = when {
                // Has both colors and tags
                first != null && second != null && tags.isNotEmpty() -> {
                    _carouselAllWallpapers.value
                        .filter { wallpaper ->
                            // Check if wallpaper contains ALL selected tags (AND logic)
                            tags.all { selectedTag ->
                                wallpaper.tags.any { wallpaperTag ->
                                    wallpaperTag.equals(selectedTag, ignoreCase = true)
                                }
                            }
                        }
                        .map { wallpaper ->
                            val wallpaperColors = getWallpaperColors(wallpaper)
                            val similarity = calculateDualColorSimilarity(first, second, wallpaperColors)
                            wallpaper to similarity
                        }
                        .sortedBy { it.second }
                        .take(FILTER_OUT_NUBMER)
                        .map { it.first }
                }
                // Has first color and tags
                first != null && tags.isNotEmpty() -> {
                    _carouselAllWallpapers.value
                        .filter { wallpaper ->
                            // ALL selected tags must be present
                            tags.all { selectedTag ->
                                wallpaper.tags.any { wallpaperTag ->
                                    wallpaperTag.equals(selectedTag, ignoreCase = true)
                                }
                            }
                        }
                        .map { wallpaper ->
                            val wallpaperColors = getWallpaperColors(wallpaper)
                            val similarity = wallpaperColors.minOfOrNull {
                                calculateColorSimilarity(first, it)
                            } ?: Double.MAX_VALUE
                            wallpaper to similarity
                        }
                        .sortedBy { it.second }
                        .take(FILTER_OUT_NUBMER)
                        .map { it.first }
                }
                // Has second color and tags
                second != null && tags.isNotEmpty() -> {
                    _carouselAllWallpapers.value
                        .filter { wallpaper ->
                            // ALL selected tags must be present
                            tags.all { selectedTag ->
                                wallpaper.tags.any { wallpaperTag ->
                                    wallpaperTag.equals(selectedTag, ignoreCase = true)
                                }
                            }
                        }
                        .map { wallpaper ->
                            val wallpaperColors = getWallpaperColors(wallpaper)
                            val similarity = wallpaperColors.minOfOrNull {
                                calculateColorSimilarity(second, it)
                            } ?: Double.MAX_VALUE
                            wallpaper to similarity
                        }
                        .sortedBy { it.second }
                        .take(FILTER_OUT_NUBMER)
                        .map { it.first }
                }
                // Has both colors only
                first != null && second != null -> {
                    _carouselAllWallpapers.value
                        .map { wallpaper ->
                            val wallpaperColors = getWallpaperColors(wallpaper)
                            val similarity = calculateDualColorSimilarity(first, second, wallpaperColors)
                            wallpaper to similarity
                        }
                        .sortedBy { it.second }
                        .take(FILTER_OUT_NUBMER)
                        .map { it.first }
                }
                // Has first color only
                first != null -> {
                    _carouselAllWallpapers.value
                        .map { wallpaper ->
                            val wallpaperColors = getWallpaperColors(wallpaper)
                            val similarity = wallpaperColors.minOfOrNull {
                                calculateColorSimilarity(first, it)
                            } ?: Double.MAX_VALUE
                            wallpaper to similarity
                        }
                        .sortedBy { it.second }
                        .take(FILTER_OUT_NUBMER)
                        .map { it.first }
                }
                // Has second color only
                second != null -> {
                    _carouselAllWallpapers.value
                        .map { wallpaper ->
                            val wallpaperColors = getWallpaperColors(wallpaper)
                            val similarity = wallpaperColors.minOfOrNull {
                                calculateColorSimilarity(second, it)
                            } ?: Double.MAX_VALUE
                            wallpaper to similarity
                        }
                        .sortedBy { it.second }
                        .take(FILTER_OUT_NUBMER)
                        .map { it.first }
                }
                // Has tags only
                tags.isNotEmpty() -> {
                    _carouselAllWallpapers.value
                        .filter { wallpaper ->
                            // ALL selected tags must be present (AND logic)
                            tags.all { selectedTag ->
                                wallpaper.tags.any { wallpaperTag ->
                                    wallpaperTag.equals(selectedTag, ignoreCase = true)
                                }
                            }
                        }
                        .shuffled()
                        .take(FILTER_OUT_NUBMER)
                }
                // No filters - show random wallpapers
                else -> {
                    _carouselAllWallpapers.value.shuffled().take(30)
                }
            }
            _carouselWallpapers.value = filteredList
        }
    }

    // Add method to update selected tags
    fun setSelectedTags(tags: List<String>) {
        _selectedTags.value = tags
        updateFilteredWallpapers()
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

    // Add these properties to HomeViewModel class
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

    // Add this method to your HomeViewModel class to check if more data should be loaded
    fun shouldLoadMore(): Boolean {
        return !isLastPage && !_isLoading.value
    }

    private suspend fun fetchWallpapers(page: Int, pageSize: Int): PaginatedResponse {
        val catalog = _currentCatalog.value
        val catalogParam = if (catalog == "Wallpapers" || catalog.isEmpty()) "" else catalog

        return getWallpapersByPageUseCase(page, pageSize, catalogParam)
            .getOrElse { error ->
                Log.e(TAG, "Error fetching wallpapers: ${error.message}")
                PaginatedResponse(emptyList(), page, 1, 0, false)
            }
    }

    private suspend fun fetchSpecificWallpapers(page: Int, pageSize: Int, catalog : String): PaginatedResponse {
        return getWallpapersByPageUseCase(page, pageSize, catalog)
            .getOrElse { error ->
                Log.e(TAG, "Error fetching specific wallpapers: ${error.message}")
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
                // Log.d(TAG, "Loaded ${response.items.size} wallpapers, total: ${currentList.size}, hasMore: ${response.hasMore}")

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

    fun logOutImages(list: List<WallpaperItem>, num: Int) {
        var count = num
        list.forEach { wallpaper->
            val imageUrl = wallpaper.imageList.firstOrNull {
                it.type == "HD" && it.link.isNotEmpty()
            }?.link ?: ""
            count++
            if (count <= 6) Log.d("GDT","=== HD" + imageUrl)
        }
    }
}