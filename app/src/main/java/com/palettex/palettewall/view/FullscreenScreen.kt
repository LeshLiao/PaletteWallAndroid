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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.R
import com.palettex.palettewall.data.WallpaperDatabase
import com.palettex.palettewall.view.component.NormalModal
import com.palettex.palettewall.view.component.LikeButton
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
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val database = remember { WallpaperDatabase.getDatabase(context) }
    val dao = remember { database.likedWallpaperDao() }

    val dragState = rememberDraggableState { delta ->
        // Add horizontal drag gesture state
        // Handle drag delta
    }

    LaunchedEffect(itemId) {
        wallpaperViewModel.setThumbnailImageByItemId(currentItemId)
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
                                val currentIndex = wallpapers.indexOfFirst {
                                    it.itemId == currentItemId
                                }
                                if (velocity > 0) {  // Swipe right - previous wallpaper
                                    if (currentIndex > 0) {
                                        currentItemId = wallpapers[currentIndex - 1].itemId
                                        wallpaperViewModel.setThumbnailImageByItemId(currentItemId)
                                    }
                                } else {  // Swipe left - next wallpaper
                                    if (currentIndex < wallpapers.size - 1) {
                                        currentItemId = wallpapers[currentIndex + 1].itemId
                                        wallpaperViewModel.setThumbnailImageByItemId(currentItemId)
                                    }
                                }
                            }
                        }
                    )
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = currentImage),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column() {
                    val boxHeight = LocalConfiguration.current.screenHeightDp.dp * 1 / 8
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(boxHeight*2) // 1/8
//                            .border(1.dp,Color.White, RectangleShape)
                            .padding(start = 26.dp)
                            .throttleClick {
                                viewModel.showTopBar()
                                navController?.popBackStack()
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (isButtonVisible) {
                            GetBackButton()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(boxHeight*5)  // 6/8
                            // .border(1.dp,Color.White, RectangleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // No ripple effect
                            ) {
                                isButtonVisible = !isButtonVisible
                            }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(boxHeight * 2)
                            .throttleClick {},
                        contentAlignment = Alignment.Center
                    ) {
                        val isLiked by dao.isWallpaperLiked(itemId).collectAsState(initial = false)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (downloadBtnStatus == 1) {
                                Text("Downloading...")
                            }
                            if (isButtonVisible) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ShareButton(itemId, wallpaperViewModel)
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
                                                isLoading = downloadBtnStatus == 1 || loadAdsBtnStatus
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
                                    LikeButton(isLiked, dao, itemId, wallpaperViewModel, coroutineScope)
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
    isLoading: Boolean = false
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
            painter = painterResource(R.drawable.icon_back_crop),
            modifier = Modifier
                .fillMaxSize(),
            contentDescription = "Back",
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFullscreenScreen() {
    val context = LocalContext.current
    val mockWallpaperViewModel = WallpaperViewModel().apply {}
    val mockBillingViewModel = BillingViewModel(context).apply {}
    val mockTopBarViewModel = TopBarViewModel().apply {}
    FullscreenScreen(itemId = "mockItemId", null, mockWallpaperViewModel, mockBillingViewModel, mockTopBarViewModel)
}