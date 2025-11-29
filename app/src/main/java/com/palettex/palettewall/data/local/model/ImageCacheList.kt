package com.palettex.palettewall.data.local.model

import android.content.Context
import android.util.Log

data class ImageCacheList(
    val cacheList: Map<String, List<String>> = emptyMap()
) {
    /**
     * Extract filename from Firebase Storage URL and convert to asset filename format
     *
     * Input: "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2FLD%2F20250309_082201_LD_204x364_1056c0db-9058-4b79-bf3f-7d5b8b1824e2.jpg?alt=media"
     * Output: "images_LD_20250309_082201_LD_204x364_1056c0db-9058-4b79-bf3f-7d5b8b1824e2.jpg"
     */
    private fun extractFileName(url: String): String {
        // Extract the base filename from URL
        val baseFileName = url
            .substringAfter("images%2F") // Remove everything before images/
            .substringAfter("%2F") // Remove quality folder (LD, HD, etc.)
            .substringBefore("?") // Remove query parameters

        // Extract quality type from URL path
        val qualityType = url
            .substringAfter("images%2F")
            .substringBefore("%2F") // Gets "LD", "HD", etc.

        // Construct the asset filename format: images_LD_basefilename.jpg
        return "images_${qualityType}_$baseFileName"
    }

    /**
     * Extract quality type from Firebase Storage URL
     *
     * Input: "https://...o/images%2FLD%2F20250309_082201..."
     * Output: "LD"
     */
    private fun extractQualityType(url: String): String {
        return url
            .substringAfter("images%2F")
            .substringBefore("%2F")
    }

    /**
     * Get the asset path if image is cached
     * Returns the full asset path like "cached_images/LD/images_LD_20250309_082201_LD_204x364_1056c0db-9058-4b79-bf3f-7d5b8b1824e2.jpg"
     */
    fun getCachedImagePath(imageUrl: String): String? {
        val fileName = extractFileName(imageUrl)
        val qualityType = extractQualityType(imageUrl)

        cacheList[qualityType]?.forEach { cachedFileName ->
            if (cachedFileName == fileName) {
                val assetPath = "cached_images/$qualityType/$cachedFileName"
                Log.d("ImageCache", "Found cached image path: $assetPath")
                return assetPath
            }
        }
        return null
    }

    /**
     * Get cached images for a specific quality type
     */
    fun getCachedImages(quality: String): List<String> {
        return cacheList[quality] ?: emptyList()
    }

    /**
     * Get all cached image filenames (for debugging)
     */
    fun getAllCachedFileNames(): List<String> {
        return cacheList.values.flatten()
    }

    companion object {
        private const val CACHE_BASE_PATH = "cached_images"

        /**
         * Scan assets folder and automatically build ImageCacheList
         * @param context Android Context to access assets
         * @return ImageCacheList with all found cached images
         */
        fun fromAssets(context: Context): ImageCacheList {
            val cacheMap = mutableMapOf<String, MutableList<String>>()

            try {
                // List all quality folders (LD, HD, BL, etc.)
                val qualityFolders = context.assets.list(CACHE_BASE_PATH) ?: emptyArray()

                Log.d("ImageCacheList", "Found quality folders: ${qualityFolders.joinToString()}")

                qualityFolders.forEach { qualityType ->
                    val folderPath = "$CACHE_BASE_PATH/$qualityType"

                    try {
                        // List all images in this quality folder
                        val images = context.assets.list(folderPath) ?: emptyArray()

                        // Filter only image files (jpg, png, webp)
                        val imageFiles = images.filter { fileName ->
                            fileName.endsWith(".jpg", ignoreCase = true) ||
                                    fileName.endsWith(".jpeg", ignoreCase = true) ||
                                    fileName.endsWith(".png", ignoreCase = true) ||
                                    fileName.endsWith(".webp", ignoreCase = true)
                        }

                        if (imageFiles.isNotEmpty()) {
                            cacheMap[qualityType] = imageFiles.toMutableList()
                            Log.d("ImageCacheList", "$qualityType: Found ${imageFiles.size} images")

                            // Log first few images for debugging
                            imageFiles.take(3).forEach { fileName ->
                                Log.d("ImageCacheList", "  - $fileName")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ImageCacheList", "Error reading folder: $folderPath", e)
                    }
                }

                val totalImages = cacheMap.values.sumOf { it.size }
                Log.d("ImageCacheList", "Total cached images loaded: $totalImages")

            } catch (e: Exception) {
                Log.e("ImageCacheList", "Error scanning assets folder", e)
            }

            return ImageCacheList(cacheMap)
        }
    }
}

