package com.palettex.palettewall.ui.components.utility

fun getCurrentTime(): String {
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date())
}

fun getCurrentDate(): String {
    val sdf = java.text.SimpleDateFormat("EEE, MMM dd", java.util.Locale.getDefault())
    return sdf.format(java.util.Date())
}