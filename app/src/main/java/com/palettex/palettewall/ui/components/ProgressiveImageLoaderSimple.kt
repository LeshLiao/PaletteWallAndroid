package com.palettex.palettewall.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun ProgressiveImageLoaderBest(
    blurImageUrl: String,
    fullImageSource: String,
    imageLoader: ImageLoader,
    repeatMode: RepeatMode = RepeatMode.Reverse,
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Load blur image first
        if (blurImageUrl.isNotEmpty()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(blurImageUrl)
                    //.crossfade(true)
                    .crossfade(1000)
                    .build(),
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    SimpleGraySkeletonLoader(
                        modifier = Modifier.fillMaxSize(),
                        repeatMode = repeatMode
                    )
                }
            )
        } else {
            SimpleGraySkeletonLoader(
                modifier = Modifier.fillMaxSize(),
                repeatMode = repeatMode
            )
        }

        // Load full image on top
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(fullImageSource)
                .crossfade(380)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}