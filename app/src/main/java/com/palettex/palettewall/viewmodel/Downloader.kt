package com.palettex.palettewall.viewmodel

interface Downloader {
    fun downloadFile(url: String): Long
}