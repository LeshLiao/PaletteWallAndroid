package com.palettex.palettewall.view.component

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.launch

@Composable
fun ShareButton(
    itemId: String,
    wallpaperViewModel: WallpaperViewModel,
    currentImage: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        modifier = Modifier.padding(4.dp).testTag("testTag_sharing"),
        onClick = {
            coroutineScope.launch {
                if (currentImage != null) {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT,
                            "- Check out more amazing wallpaper!\n" +
                                    "    https://www.palettex.ca/ \n\n" +
                                    "- Share Wallpaper: $currentImage")
                        type = "text/plain"
                    }
                    wallpaperViewModel.firebaseShareEvent(itemId)
                    val shareIntent = Intent.createChooser(sendIntent, "Share wallpaper")
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(shareIntent)
                } else {
                    Toast.makeText(
                        context,
                        "Unable to share wallpaper at this time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    ) {
        Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = "Share",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}