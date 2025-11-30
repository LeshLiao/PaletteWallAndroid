package com.palettex.palettewall.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palettex.palettewall.domain.model.WallpaperItem
import com.palettex.palettewall.domain.usecase.WallpaperUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<WallpaperItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
@OptIn(FlowPreview::class)
class SearchViewModel @Inject constructor(
    private val wallpaperUseCase: WallpaperUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel" + "_GDT"
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        Log.d(TAG,"SearchViewModel init{}")
        viewModelScope.launch {
            searchQuery
                .debounce(500)
                .filter { it.trim().isNotEmpty() }
                .distinctUntilChanged()
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"SearchViewModel onCleared{}")
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.trim().isEmpty()) {
            _searchResults.value = emptyList()
            _errorMessage.value = null
        }
    }

    private suspend fun performSearch(query: String) {
        _isLoading.value = true
        _errorMessage.value = null

        val result = wallpaperUseCase.search(query)

        result
            .onSuccess { wallpapers ->
                _searchResults.value = wallpapers

                if (wallpapers.isEmpty()) {
                    _errorMessage.value = "No results found for \"$query\""
                }
            }
            .onFailure { error ->
                _errorMessage.value = "Search failed: ${error.message}"
                _searchResults.value = emptyList()
            }

        _isLoading.value = false
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _errorMessage.value = null
    }
}