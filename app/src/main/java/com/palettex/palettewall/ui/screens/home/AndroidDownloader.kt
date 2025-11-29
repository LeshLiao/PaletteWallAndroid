package com.palettex.palettewall.ui.screens.home

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import java.math.BigInteger
import java.security.MessageDigest

class AndroidDownloader(
    private val context: Context
) : Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String): Long {
        val fileName = "PaletteWall-" + generateFileName(url) + ".jpg"  // Generates a random name

        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .addRequestHeader("Authorization", "Bearer <token>")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        return downloadManager.enqueue(request)
    }

    // Function to generate an 8-character alphanumeric string from the URL
    private fun generateFileName(url: String): String {
        val md5Hash = md5(url)  // Create MD5 hash from URL
        return md5Hash.take(8)  // Take the first 8 characters
    }

    // Function to create MD5 hash from a string (like the URL)
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}

