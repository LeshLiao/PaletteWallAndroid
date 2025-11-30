package com.palettex.palettewall.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TopBarViewModel @Inject constructor() : ViewModel() {
    var isTopBarVisible by mutableStateOf(true)
        private set

    private val scrollThreshold = 20f // Adjust this value as needed

    private val queue = mutableListOf<Float>()
    private val queueSize = 5
    private val threshold = 3f

    private val _topBarTitle = MutableStateFlow("PaletteX")
    val topBarTitle: StateFlow<String> = _topBarTitle

    fun onScroll(deltaY: Float) {

        // Add the value to the queue
        if (queue.size == queueSize) {
            queue.removeAt(0) // Remove the oldest value to maintain the size
        }
        queue.add(deltaY)

        // Count positive and negative values
        val positiveCount = queue.count { it > scrollThreshold }
        val negativeCount = queue.count { it < -scrollThreshold }

        // Check if there are more than 3 positive or negative values
        if (positiveCount > threshold) {
            hideTopBar()
            queue.clear() // Clear the queue after action
        } else if (negativeCount > threshold) {
            showTopBar()
            queue.clear() // Clear the queue after action
        }
    }

    fun hideTopBar() {

        isTopBarVisible = false
    }

    fun showTopBar() {
        isTopBarVisible = true
    }

    fun setTopBarTitle(title: String) {
        _topBarTitle.value = title
    }
}
