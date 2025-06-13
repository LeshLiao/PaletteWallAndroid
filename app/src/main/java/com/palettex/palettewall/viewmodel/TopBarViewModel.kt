package com.palettex.palettewall.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class TopBarViewModel : ViewModel() {
    var isTopBarVisible by mutableStateOf(true)
        private set

    private val scrollThreshold = 20f // Adjust this value as needed

    private val queue = mutableListOf<Float>()
    private val queueSize = 5
    private val threshold = 3f

    fun onScroll(deltaY: Float) {
        // Ignore values that are too large
//        if (deltaY > 200 || deltaY < -200) return

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
//            Log.d("GDT", "${deltaY}  ")
            queue.clear() // Clear the queue after action
        } else if (negativeCount > threshold) {
            showTopBar()
//            Log.d("GDT", "${deltaY}  ++++++")
            queue.clear() // Clear the queue after action
        }
    }

    fun hideTopBar() {

        isTopBarVisible = false
    }

    fun showTopBar() {
        isTopBarVisible = true
    }
}
