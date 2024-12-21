package com.palettex.palettewall.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ScrollingContent(
    viewModel: TopBarViewModel,
    navController: NavController,
    wallpaperViewModel: WallpaperViewModel,
) {
    val listState = rememberLazyListState()
    val lastScrollOffset = remember { mutableIntStateOf(0) }
    val appSettings by wallpaperViewModel.appSettings.collectAsState()
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()
    val currentCatalog by wallpaperViewModel.currentCatalog.collectAsState()

    // Pre-initialize the AdMobBannerView for early initialization
    val context = LocalContext.current
    val isBottomAdsLoaded by wallpaperViewModel.isBottomAdsLoaded.collectAsState()
    var showPopular by remember { mutableStateOf(false) }

    val adMobBannerView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = if (BuildConfig.DEBUG_MODE) {
                "ca-app-pub-3940256099942544/6300978111" // Test ad unit ID
            } else {
                "ca-app-pub-6980436502917839/4038861167" // Real ad unit ID
            }
        }
    }

    // Load the AdMob ad early
    LaunchedEffect(isBottomAdsLoaded, appSettings) {
        if (!isBottomAdsLoaded && appSettings.adsLevel > 3) {
            val adRequest = AdRequest.Builder().build()
            adMobBannerView.loadAd(adRequest)
            Log.d("GDT", "adMobBannerView.loadAd(adRequest)")
            // Update the ViewModel state when the ad is loaded
            adMobBannerView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("GDT", "adMobBannerView onAdLoaded()")
                    wallpaperViewModel.setBottomAdsLoaded(true)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("GDT", "Ad failed to load: ${adError.message}")
                    Toast.makeText(
                        context,
                        "Msg: ${adError.message}, please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { currentScrollOffset ->
                val delta = currentScrollOffset - lastScrollOffset.intValue

                // Scroll handling for top bar visibility
                viewModel.onScroll(delta.toFloat())

                // Check if scrolled to the top (first item and no offset)
                if (listState.firstVisibleItemIndex <= 1) {
                    viewModel.showTopBar()  // Call showTopBar when at the top
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

    LazyColumn(state = listState) {
        item { Spacer(modifier = Modifier.height(80.dp).fillMaxWidth()) }
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            CatalogRow(wallpaperViewModel)
        }

        if (showPopular) {
            item {
                Titles(
                    title = "Popular Collections",
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 2.dp)
                )
            }
            item {
                PopularWallpapers(viewModel, navController, wallpaperViewModel)
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
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .aspectRatio(0.5f)
                            .clickable {
                                viewModel.hideTopBar()
                                navController.navigate("fullscreen/${wallpaper.itemId}")
                            },
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = wallpaperViewModel.getThumbnailByItemId(wallpaper.itemId)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        // AdMobBannerView as the last item
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { adMobBannerView }
            )
            Spacer(modifier = Modifier.height(80.dp))
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