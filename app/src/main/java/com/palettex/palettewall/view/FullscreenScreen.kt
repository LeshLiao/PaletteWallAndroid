package com.palettex.palettewall.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.R
import com.palettex.palettewall.data.WallpaperDatabase
import com.palettex.palettewall.view.component.DarkShimmerSkeletonLoader
import com.palettex.palettewall.view.component.LikeButton
import com.palettex.palettewall.view.component.NormalModal
import com.palettex.palettewall.view.component.PremiumModal
import com.palettex.palettewall.view.component.ShareButton
import com.palettex.palettewall.view.component.SubscriptionModal
import com.palettex.palettewall.view.utility.throttleClick
import com.palettex.palettewall.viewmodel.AndroidDownloader
import com.palettex.palettewall.viewmodel.BillingViewModel
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlin.math.abs

@SuppressLint("UnrememberedMutableState")
@Composable
fun FullscreenScreen(
    catalog: String,
    itemId: String,
    navController: NavController?,
    wallpaperViewModel: WallpaperViewModel,
    billingViewModel: BillingViewModel,
    viewModel: TopBarViewModel,
) {
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf("") }
    var currentItemId by remember { mutableStateOf(itemId) }
    var showNormalModel by remember { mutableStateOf(false) }
    var showPremiumModel by remember { mutableStateOf(false) }
    var showSubscriptionMenu by remember { mutableStateOf(false) }
    var isButtonVisible by remember { mutableStateOf(true) }
    val downloadBtnStatus by wallpaperViewModel.downloadBtnStatus.collectAsState()
    val loadAdsBtnStatus by wallpaperViewModel.loadAdsBtnStatus.collectAsState()
    val currentImage by wallpaperViewModel.currentImage.collectAsState()
    val isCurrentFreeDownload by wallpaperViewModel.isCurrentFreeDownload.collectAsState()
    val isPremium by billingViewModel.isPremium.collectAsState()
    val fullScreenWallpapers by wallpaperViewModel.fullScreenWallpapers.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val database = remember { WallpaperDatabase.getDatabase(context) }
    val dao = remember { database.likedWallpaperDao() }
    val isLiked by dao.isWallpaperLiked(currentItemId).collectAsState(initial = false)
    val dragState = rememberDraggableState { delta ->
        // Add horizontal drag gesture state
        // Handle drag delta
    }
    var showInformation by remember { mutableStateOf(false) }

    LaunchedEffect(itemId) {
        wallpaperViewModel.initFullScreenDataSource(catalog)
        wallpaperViewModel.setThumbnailImageByItemId(currentItemId, "HD")
    }

    LaunchedEffect(currentItemId) {
        wallpaperViewModel.updateDownloadBtnStatus(0)
        wallpaperViewModel.updateLoadAdsBtnStatus(false)
        wallpaperViewModel.setFullScreenStatus(true)
    }

    DisposableEffect(currentItemId) {
        onDispose {
            wallpaperViewModel.setFullScreenStatus(false)
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
                    .draggable(
                        state = dragState,
                        orientation = Orientation.Horizontal,
                        onDragStarted = { },
                        onDragStopped = { velocity ->
                            // Determine swipe direction based on velocity
                            if (abs(velocity) > 200) {  // Adjust threshold as needed
                                val currentIndex = fullScreenWallpapers.indexOfFirst {
                                    it.itemId == currentItemId
                                }
                                if (velocity > 0) {  // Swipe right - previous wallpaper
                                    if (currentIndex > 0) {
                                        currentItemId = fullScreenWallpapers[currentIndex - 1].itemId
                                        wallpaperViewModel.setThumbnailImageByItemId(currentItemId, "HD")
                                    }
                                } else {  // Swipe left - next wallpaper
                                    if (currentIndex < fullScreenWallpapers.size - 1) {
                                        currentItemId = fullScreenWallpapers[currentIndex + 1].itemId
                                        wallpaperViewModel.setThumbnailImageByItemId(currentItemId, "HD")
                                    }
                                }
                            }
                        }
                    )
            ) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentImage)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .size(Size.ORIGINAL)
                        .build()
                )

                val painterState = painter.state

                // Show skeleton loader while loading
                if (painterState is AsyncImagePainter.State.Loading ||
                    painterState is AsyncImagePainter.State.Error) {
                    DarkShimmerSkeletonLoader(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column() {
                    val boxHeight = LocalConfiguration.current.screenHeightDp.dp * 1 / 8
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(boxHeight * 2)
                            .padding(horizontal = 26.dp)
                            .throttleClick {
                                viewModel.showTopBar()
                                navController?.popBackStack()
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isButtonVisible) {
                            GetBackButton() // left side
                            InfoButton(
                                onClick = {
                                    showInformation = !showInformation // Toggle the state
                                }
                            ) // right side
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(boxHeight*5)  // 6/8
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // No ripple effect
                            ) {
                                isButtonVisible = !isButtonVisible
                            }
                    ) {
                        if (showInformation && isButtonVisible) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter) // ✅ key part
                            ) {
                                val imageTitle = wallpaperViewModel.getImageInfoByItemId(currentItemId)
                                ImageInformation(imageTitle)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(boxHeight * 2)
                            .throttleClick {},
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (downloadBtnStatus == 1) {
                                Text("Downloading...")
                            } else if (downloadBtnStatus == 2) {
                                Text("Download Completed!")
                            }
                            if (isButtonVisible) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ShareButton(currentItemId, wallpaperViewModel, currentImage)
                                    Spacer(Modifier.size(16.dp))
                                    Column(
                                        modifier = Modifier.width(52.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (downloadBtnStatus != 2) {
                                            AnimatedFloatingActionButton(
                                                onClick = {
                                                    if (downloadBtnStatus == 0) {
                                                        if (isPremium) {
                                                            showPremiumModel = true
                                                        } else {
                                                            showNormalModel = true
                                                        }
                                                    }
                                                },
                                                isCurrentFreeDownload = isCurrentFreeDownload,
                                                modifier = Modifier.size(52.dp),
                                                isLoading = downloadBtnStatus == 1 || loadAdsBtnStatus,
                                                testTag = "download_button"
                                            )
                                        } else {
                                            Icon(
                                                painter = painterResource(R.drawable.test02),
                                                modifier = Modifier.size(50.dp),
                                                contentDescription = "finish",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                    Spacer(Modifier.size(16.dp))
                                    LikeButton(isLiked, dao, currentItemId, wallpaperViewModel, coroutineScope, currentImage)
                                }
                            }
                        }
                    }
                }

                if (isDialogVisible) {
                    ShowDownloadDialog(msg) { isDialogVisible = false }
                }
            }
        }
    )

    if (showNormalModel) {
        NormalModal(
            context = context,
            currentItemId = currentItemId,
            isCurrentFreeDownload = isCurrentFreeDownload,
            onDismissRequest = { showNormalModel = false },
            wallpaperViewModel = wallpaperViewModel,
            billingViewModel = billingViewModel,
            loadingAds = { wallpaperViewModel.updateLoadAdsBtnStatus(true) },
            showSubscriptions = {
                showNormalModel = false
                showSubscriptionMenu = true
            },
            onAdWatchedAndStartDownload = {
                Log.d("GDT","onAdWatchedAndStartDownload() click!!!!!!")
                wallpaperViewModel.updateDownloadBtnStatus(1)
                wallpaperViewModel.getDownloadListLinkByItemId(currentItemId)?.let {
                    downloadImage(context, it) { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    wallpaperViewModel.firebaseDownloadFreeEvent(currentItemId)
                    if (!BuildConfig.DEBUG_MODE) {
                        wallpaperViewModel.sendLogEvent(currentItemId, "download_free")
                    }
                }
            }
        )
    }

    if (showPremiumModel) {
        PremiumModal(
            billingViewModel = billingViewModel,
            onDismissRequest = { showPremiumModel = false },
            onAdWatchedAndStartDownload = {
                Log.d("GDT","onAdWatchedAndStartDownload() click!!!!!!")
                wallpaperViewModel.updateDownloadBtnStatus(1)
                wallpaperViewModel.getDownloadListLinkByItemId(currentItemId)?.let {
                    downloadImage(context, it) { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    wallpaperViewModel.firebaseDownloadFreeEvent(currentItemId)
                    if (!BuildConfig.DEBUG_MODE) {
                        wallpaperViewModel.sendLogEvent(currentItemId, "download_free")
                    }
                }
            }
        )
    }

    if (showSubscriptionMenu) {
        SubscriptionModal(
            context = context,
            onDismissRequest = { showSubscriptionMenu = false },
            billingViewModel = billingViewModel
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
    isCurrentFreeDownload: Boolean,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    testTag: String? = null
) {
    // Define a list of colors for the animation
    val colors = listOf(Color.White, Color.Transparent)

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
        modifier = modifier.then(
            if (testTag != null) {
                Modifier.testTag(testTag)
            } else {
                Modifier
            }
        ),
        containerColor = animatedColor.value,
        shape = CircleShape
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
                if (isCurrentFreeDownload) {
                    Image(
                        painterResource(R.drawable.download2),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(50.dp)
                    )
                } else {
                    Image(
                        painterResource(R.drawable.download_premium_1),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GetBackButton() {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Color.Black, shape = RoundedCornerShape(25.dp)),
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_back_left),
            modifier = Modifier
                .fillMaxSize(),
            contentDescription = "Back",
            tint = Color.White
        )
    }
}

@Composable
fun InfoButton(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Color.Black, shape = RoundedCornerShape(25.dp))
            .clickable { onClick() }, // Add clickable modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_info),
            modifier = Modifier
                .fillMaxSize(),
            contentDescription = "Info",
            tint = Color.White
        )
    }
}

@Composable
fun ImageInformation(imageTitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(
                color = Color.DarkGray.copy(alpha = 0.6f), // Light gray, 50% transparent
                shape = RoundedCornerShape(6.dp)
            )
    ) {

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = imageTitle,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewInfoScreen() {
    Column (modifier = Modifier.fillMaxWidth()) {
        ImageInformation("Test Information")
    }
}