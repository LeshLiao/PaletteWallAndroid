package com.palettex.palettewall.ui.screens.home

interface Downloader {
    fun downloadFile(url: String): Long
}

