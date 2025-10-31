package com.palettex.palettewall.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SeeMoreViewModel : ViewModel() {
    companion object {
        private val TAG = SeeMoreViewModel::class.java.simpleName + "_GDT"
        private const val PAGE_SIZE = 36
    }

    private val _wallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val wallpapers: StateFlow<List<WallpaperItem>> = _wallpapers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPage = 0
    private var hasMorePages = true
    private var currentCatalog = ""

    fun initializeCatalog(catalog: String) {
        if (currentCatalog != catalog) {
            currentCatalog = catalog
            resetPagination()
            fetchSpecificWallpapers()
        }
    }

    fun fetchSpecificWallpapers() {
        if (_isLoading.value || !hasMorePages) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = RetrofitInstance.api.getWallpapersByPage(
                    page = currentPage,
                    pageSize = PAGE_SIZE,
                    catalog = currentCatalog
                )

                // Append new items to existing list
                _wallpapers.value = _wallpapers.value + result.items

                // Update pagination state
                hasMorePages = result.hasMore
                currentPage++

                Log.d(TAG, "Loaded page $currentPage, total items: ${_wallpapers.value.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching wallpapers: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshWallpapers() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                resetPagination()

                val result = RetrofitInstance.api.getWallpapersByPage(
                    page = 0,
                    pageSize = PAGE_SIZE,
                    catalog = currentCatalog
                )

                _wallpapers.value = result.items
                hasMorePages = result.hasMore
                currentPage = 1

                Log.d(TAG, "Refreshed wallpapers, loaded ${result.items.size} items")
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing wallpapers: ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun resetPagination() {
        currentPage = 0
        hasMorePages = true
        _wallpapers.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        resetPagination()
    }
}