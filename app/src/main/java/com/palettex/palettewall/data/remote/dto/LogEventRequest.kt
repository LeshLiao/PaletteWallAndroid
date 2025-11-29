package com.palettex.palettewall.data.remote.dto

data class LogEventRequest(
    val itemId: String,
    val appVersion: String,
    val eventType: String,
    val manufacturer: String,
    val model: String,
    val release: String,
    val sdk: String,
    val country: String
)

