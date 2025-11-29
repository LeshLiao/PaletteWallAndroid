package com.palettex.palettewall.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.palettex.palettewall.data.local.entity.LikedWallpaper
import com.palettex.palettewall.data.local.dao.LikedWallpaperDao
import com.palettex.palettewall.ui.screens.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LikeButton(isLiked: Boolean, dao: LikedWallpaperDao, itemId: String, wallpaperViewModel: HomeViewModel, coroutineScope: CoroutineScope, currentImage: String) {
    IconButton(
        modifier = Modifier.padding(4.dp),
        onClick = {
            coroutineScope.launch {
                if (isLiked) {
                    dao.deleteLikedWallpaper(LikedWallpaper(itemId, currentImage))
                } else {
                    dao.insertLikedWallpaper(LikedWallpaper(itemId, currentImage))
                    wallpaperViewModel.firebaseLikeEvent(itemId)
                }
            }
        }
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Like",
            tint = if (isLiked) Color(0xFFFF3E51) else Color.White,
        )
    }
}