package com.palettex.palettewall.view

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

import androidx.compose.material3.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.material3.icons.Icons
//import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.palettex.palettewall.R
import com.palettex.palettewall.view.component.BottomModal
import com.palettex.palettewall.viewmodel.AndroidDownloader
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun FullscreenScreen(
    itemId: String,
    navController: NavController?,
    viewModel: TopBarViewModel?,
    wallpaperViewModel: WallpaperViewModel?
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val debounceTime = 1000L // Time in milliseconds to prevent double clicks
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) } // Manage dialog visibility
    var msg by remember { mutableStateOf("") }
    var ThumbnailImage: String? = wallpaperViewModel?.getThumbnailByItemId(itemId)
    var DownloadImage: String? = wallpaperViewModel?.getDownloadListLinkByItemId(itemId)
    var showModel by remember { mutableStateOf(false) }

    val downloadBtnStatus by wallpaperViewModel?.downloadBtnStatus?.collectAsState() ?: remember { mutableStateOf(0) }

    LaunchedEffect(itemId) {
        wallpaperViewModel?.updateDownloadBtnStatus(0)
        Log.d("GDT", "FullscreenScreen Init")
        wallpaperViewModel?.setFullScreenStatus(true)
    }

    // Disposal log
    DisposableEffect(itemId) {
        onDispose {
            Log.d("GDT", "FullscreenScreen dispose")
            wallpaperViewModel?.setFullScreenStatus(false)
        }
    }

    Scaffold(
        topBar = {},
        content = { paddingValues ->
            val test = paddingValues
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = ThumbnailImage),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Transparent Box for pop back
                val boxHeight = LocalConfiguration.current.screenHeightDp.dp * 4 / 5
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(boxHeight)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null // Disable click animation
                        ) {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastClickTime > debounceTime) {
                                navController?.popBackStack()
                                lastClickTime = currentTime
//                                viewModel?.showTopBar()
                            }
                        }
                )

                if (downloadBtnStatus != 2) {
                    AnimatedFloatingActionButton(
                        onClick = {
                            if (downloadBtnStatus == 0) {
                                showModel = true
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 56.dp)
                            .size(52.dp),

                        isLoading = downloadBtnStatus == 1
                    )
                }

                // Show the dialog when download starts
                if (isDialogVisible) {
                    ShowDownloadDialog (msg) { isDialogVisible = false } // Dismiss dialog on Confirm
                }
            }
        }
    )

    if (showModel) {
        BottomModal(
            context = context,
            onDismissRequest = { showModel = false },
            onAdWatched = {
                wallpaperViewModel?.updateDownloadBtnStatus(1)
                // Call downloadImage() after ad is watched
                wallpaperViewModel?.getDownloadListLinkByItemId(itemId)?.let {
                    Log.d("GDT","onAdWatched")
                    downloadImage(context, it) { msg ->
                        // Handle the download completion message
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

fun downloadImage(context: Context, imageUrl: String?, onDownloadEnqueued: (String) -> Unit) {
    if (imageUrl == null) {
        Toast.makeText(context, "Image URL not found", Toast.LENGTH_SHORT).show()
        return
    }
    val downloader = AndroidDownloader(context)
    downloader.downloadFile(imageUrl)
}

@Composable
fun AnimatedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    // Define a list of colors for the animation
    val colors = listOf(
        Color.White,
        Color.Transparent
    )

    // InfiniteTransition to cycle through colors
    val transition = rememberInfiniteTransition(label = "")
    val animatedColor = transition.animateColor(
        initialValue = colors.first(),
        targetValue = colors.last(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = animatedColor.value,
        shape = CircleShape,
    ) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color.White,
                    strokeWidth = 5.dp
                )
            } else {
                Image(
                    painterResource(R.drawable.download2),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}