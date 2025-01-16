package com.palettex.palettewall.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
//import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdSize
//import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.data.PaletteRemoteConfig
import com.palettex.palettewall.data.WallpaperDatabase
import com.palettex.palettewall.view.component.LikeButton
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdListener
import com.facebook.ads.AdSettings
import com.facebook.ads.AdSize
import com.facebook.ads.AdView

@Composable
fun ScrollingContent(
    bottomOffset: Dp,
    topViewModel: TopBarViewModel,
    navController: NavController,
    wallpaperViewModel: WallpaperViewModel,
    context: Context = LocalContext.current
) {
    val listState = rememberLazyListState()
    val lastScrollOffset = remember { mutableIntStateOf(0) }
    val appSettings by wallpaperViewModel.appSettings.collectAsState()
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()
    val currentCatalog by wallpaperViewModel.currentCatalog.collectAsState()

    // Pre-initialize the AdMobBannerView for early initialization
    val isBottomAdsLoaded by wallpaperViewModel.isBottomAdsLoaded.collectAsState()
    var showPopular by remember { mutableStateOf(false) }
    val scrollToTopTrigger by wallpaperViewModel.scrollToTopTrigger.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val database = remember { WallpaperDatabase.getDatabase(context) }
    val dao = remember { database.likedWallpaperDao() }

//    val adMobBannerView = remember {
//        AdView(context).apply {
//            setAdSize(AdSize.BANNER)
//            adUnitId = when {
//                BuildConfig.DEBUG_MODE || PaletteRemoteConfig.isBannerDebugMode() -> {
//                    "ca-app-pub-3940256099942544/6300978111" // Test ad unit ID
//                }
//                PaletteRemoteConfig.shouldShowBannerAds() -> {
//                    PaletteRemoteConfig.getBannerAdUnitId() // Production ad unit ID
//                }
//                else -> {
//                    "" // No ads mode
//                }
//            }
//        }
//    }
//
//    // Load the AdMob ad early
//    LaunchedEffect(isBottomAdsLoaded, appSettings) {
//        if (!isBottomAdsLoaded && PaletteRemoteConfig.shouldShowBannerAds()) {
//            val adRequest = AdRequest.Builder().build()
//            adMobBannerView.loadAd(adRequest)
//            Log.d("GDT", "adMobBannerView.loadAd(adRequest)")
//            // Update the ViewModel state when the ad is loaded
//            adMobBannerView.adListener = object : AdListener() {
//                override fun onAdLoaded() {
//                    super.onAdLoaded()
//                    Log.d("GDT", "adMobBannerView onAdLoaded()")
//                    wallpaperViewModel.setBottomAdsLoaded(true)
//                }
//
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    Log.d("GDT", "Ad failed to load: ${adError.message}")
//                    Toast.makeText(
//                        context,
//                        "Msg: ${adError.message}, please try again.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { currentScrollOffset ->
                val delta = currentScrollOffset - lastScrollOffset.intValue

                // Scroll handling for top bar visibility
                topViewModel.onScroll(delta.toFloat())

                // Check if scrolled to the top (first item and no offset)
                if (listState.firstVisibleItemIndex <= 1) {
                    topViewModel.showTopBar()  // Call showTopBar when at the top
                }

                lastScrollOffset.intValue = currentScrollOffset
            }
    }

    LaunchedEffect(wallpapers) {
        if (currentCatalog == "Wallpapers") {
            showPopular = true
        } else {
            showPopular = false
        }
    }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger) {
            listState.animateScrollToItem(0)
            wallpaperViewModel.setScrollToTopTrigger(false)
        }
    }

    // Initialize Facebook Banner Ad
    val bannerAd = remember {

        // Set test device before creating the ad
        // You'll get this from logcat (logcat filter:HASHED)

//        val isDebug = BuildConfig.DEBUG
        val isDebug = true

        if (isDebug) {
            AdSettings.setTestMode(true)
//            AdSettings.addTestDevice("6986c1bd-db50-4bc0-8211-d5ef3bc1445e")
            AdSettings.addTestDevice("b904b469-8553-4eb4-a087-e65534af9b7e")
        }

        // Use test placement ID for debug builds
//        val placementId = if (BuildConfig.DEBUG) {
        val placementId = if (isDebug) {
            "IMG_16_9_APP_INSTALL#3970198999966685_3970215613298357" // This is the test format
        } else {
            "3970198999966685_3970215613298357" // Your real placement ID
        }

        AdView(context, placementId, AdSize.BANNER_HEIGHT_50).apply {
            val adListener = object : AdListener {
                override fun onError(ad: Ad, error: AdError) {
                    Log.e("GDT", "Error loading ad: ${error.errorMessage}")
                    Log.e("GDT", "Error code: ${error.errorCode}")
                    // Handle specific error codes
                    when (error.errorCode) {
                        AdError.NO_FILL_ERROR_CODE -> {
                            // Handle no fill error - maybe try to reload after delay
                        }
                        AdError.NETWORK_ERROR_CODE -> {
                            // Handle network errors
                        }
                    }
                }

                override fun onAdLoaded(ad: Ad) {
                    Log.d("GDT", "Ad loaded successfully")
                    wallpaperViewModel.setBottomAdsLoaded(true)
                }

                override fun onAdClicked(ad: Ad) {
                    Log.d("GDT", "Ad clicked")
                }

                override fun onLoggingImpression(ad: Ad) {
                    Log.d("GDT", "Ad impression logged")
                }
            }

            loadAd(buildLoadAdConfig().withAdListener(adListener).build())
        }
    }
    // Clean up the ad when the composition is disposed
    DisposableEffect(Unit) {
        onDispose {
            bannerAd.destroy()
        }
    }

    LazyColumn(state = listState) {
        item { Spacer(modifier = Modifier.height(80.dp).fillMaxWidth()) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { CatalogRow(wallpaperViewModel) }

        if (showPopular) {
            item {
                Titles(
                    title = "Popular Collections",
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 2.dp)
                )
            }
            item {
                PopularWallpapers(topViewModel, navController, wallpaperViewModel)
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        itemsIndexed(wallpapers.chunked(3)) { index,rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                rowItems.forEach { wallpaper ->
                    Box (
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .aspectRatio(0.5f)
                            .clickable {
                                topViewModel.hideTopBar()
                                navController.navigate("fullscreen/${wallpaper.itemId}")
//                                Log.d("GDT","itemId=${wallpaper.itemId}")
                            },
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = wallpaperViewModel.getThumbnailByItemId(wallpaper.itemId)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        val isLiked by dao.isWallpaperLiked(wallpaper.itemId).collectAsState(initial = false)
                        if (isLiked) {
                            Box (
                                modifier = Modifier
                                    .align(Alignment.BottomEnd) // Place the button at the bottom-end
                                    .padding(2.dp) // Add padding if needed
                            ) {
                                LikeButton(isLiked, dao, wallpaper.itemId, wallpaperViewModel, coroutineScope)
                            }
                        }
                    }
                }

                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            if (PaletteRemoteConfig.shouldShowBannerAds()) {
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { bannerAd }
                )
            }
            Spacer(modifier = Modifier.height(bottomOffset))
        }
    }
}

@Composable
fun Titles(title: String, modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = Color.White,
        )
        Text(
            text = ">",
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = Color.White,
        )
    }
}