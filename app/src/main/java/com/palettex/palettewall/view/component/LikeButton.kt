package com.palettex.palettewall.view.component

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
import com.palettex.palettewall.data.LikedWallpaper
import com.palettex.palettewall.data.LikedWallpaperDao
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LikeButton(isLiked: Boolean, dao: LikedWallpaperDao, itemId: String, wallpaperViewModel: WallpaperViewModel, coroutineScope: CoroutineScope) {
    IconButton(
        modifier = Modifier.padding(4.dp),
        onClick = {
            coroutineScope.launch {
                if (isLiked) {
                    dao.deleteLikedWallpaper(LikedWallpaper(itemId, wallpaperViewModel.getThumbnailByItemId(itemId)))
                } else {
                    dao.insertLikedWallpaper(LikedWallpaper(itemId, wallpaperViewModel.getThumbnailByItemId(itemId)))
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