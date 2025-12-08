package com.palettex.palettewall.ui.components

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.R
import com.palettex.palettewall.data.PaletteRemoteConfig
import com.palettex.palettewall.ui.screens.home.BillingViewModel
import com.palettex.palettewall.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NormalModal(
    context: Context,
    currentItemId: String,
    isCurrentFreeDownload: Boolean,
    onDismissRequest: () -> Unit = {},
    wallpaperViewModel: HomeViewModel,
    billingViewModel: BillingViewModel,
    loadingAds: () -> Unit,
    showPayment: () -> Unit,
    onAdWatchedAndStartDownload: () -> Unit
) {
    val isPremium by billingViewModel.isPremium.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true  // Avoid partially expanded state
    )

    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    var isAdReady by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    fun showRewardedAd() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // The ad was shown
                Log.d("GDT", "rewardedAd was shown.")
                if (!BuildConfig.DEBUG_MODE) {
                    wallpaperViewModel.sendLogEvent(currentItemId, "click_download:Ad_RewardedAd_was_shown")
                }
            }

            override fun onAdDismissedFullScreenContent() {
                // The ad was closed
                onAdWatchedAndStartDownload() // start download image
                Log.d("GDT", "rewardedAd was dismissed, start download.")
                rewardedAd = null  // Set rewardedAd to null after it is closed
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("GDT", "rewardedAd failed to show: ${adError.message}")
                Toast.makeText(context, "rewardedAd failed to show and Start to Download..", Toast.LENGTH_SHORT).show()
                if (!BuildConfig.DEBUG_MODE) {
                    wallpaperViewModel.sendLogEvent(currentItemId, "click_download:Ad_RewardedAd_failed_to_show:${adError.message}")
                }
                onAdWatchedAndStartDownload() // TODO: Temp Testing
                rewardedAd = null  // Set rewardedAd to null after failure
            }
        }

        rewardedAd?.show(context as Activity) { rewardItem ->
            // onAdWatched()
            onDismissRequest()  // Dismiss the modal after ad is watched
        }
    }

    fun startLoadAd() {
        isLoading = true

        // Get ad unit ID based on remote config mode
        // BuildConfig.DEBUG_MODE
        val adUnitId = when {
            BuildConfig.DEBUG_MODE || PaletteRemoteConfig.isDebugMode() -> {
                "ca-app-pub-3940256099942544/5224354917" // Test ad unit ID
            }
            PaletteRemoteConfig.shouldShowRewardAds() -> {
                PaletteRemoteConfig.getAdUnitId() // Production ad unit ID
            }
            else -> {
                "" // No ads mode
            }
        }

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("GDT", "RewardedAd onAdLoaded()...")
                    rewardedAd = ad
                    isAdReady = true
                    isLoading = false  // Stop loading once ad is ready
                    showRewardedAd() // Automatically show the ad once it's loaded
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("GDT", "Ad failed to load: ${adError.message}")
                    rewardedAd = null
                    isLoading = false  // Stop loading if failed
                    Toast.makeText(context, "Msg: ${adError.message}, Skip Ads! Start Download...", Toast.LENGTH_SHORT).show()
                    onAdWatchedAndStartDownload()
                }
            }
        )
    }

    ModalBottomSheet(
        containerColor = Color.Transparent,
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.5f), // Let gray area cover top system bar
        windowInsets = WindowInsets.navigationBars // Let gray area cover top system bar
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isCurrentFreeDownload) {
                val coroutineScope = rememberCoroutineScope()
                if (PaletteRemoteConfig.shouldShowRewardAds()) {
                    CommonButton(stringResource(R.string.show_ad_free_download)) {
                        if (!isLoading && !isAdReady) {
                            coroutineScope.launch {
                                // If no ads should be shown, skip ad loading
                                if (!PaletteRemoteConfig.shouldShowRewardAds()) {
                                    onDismissRequest()
                                    onAdWatchedAndStartDownload()
                                } else {
                                    onDismissRequest()
                                    loadingAds()
                                    startLoadAd()
                                }
                            }
                        }
                    }
                    // NOTE: Change it to one time purchase instead of Subscription
                    // Spacer(Modifier.height(1.dp))
                    // CommonButton(stringResource(R.string.go_premium)) { showSubscriptions() }
                    Spacer(Modifier.height(8.dp))
                    CommonButton(stringResource(R.string.cancel)) { onDismissRequest() }
                } else {
                    CommonButton(stringResource(R.string.no_ad_free_download)) {
                        if (!isLoading && !isAdReady) {
                            coroutineScope.launch {
                                if (!BuildConfig.DEBUG_MODE) {
                                    wallpaperViewModel.sendLogEvent(currentItemId, "click_download:download_immediately")
                                }
                                // If no ads should be shown, skip ad loading
                                if (!PaletteRemoteConfig.shouldShowRewardAds()) {
                                    onDismissRequest()
                                    onAdWatchedAndStartDownload()
                                } else {
                                    onDismissRequest()
                                    loadingAds()
                                    startLoadAd()
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    CommonButton(stringResource(R.string.cancel)) { onDismissRequest() }
                }
            } else {
                Spacer(Modifier.height(1.dp))
                CommonButton(stringResource(R.string.get_this_wallpaper)) { showPayment() }
                Spacer(Modifier.height(8.dp))
                CommonButton(stringResource(R.string.cancel)) { onDismissRequest() }
            }
        }

        Spacer(
            Modifier
                .windowInsetsBottomHeight(WindowInsets.navigationBarsIgnoringVisibility)
                .background(Color.White)
        )
        Spacer(Modifier.height(8.dp))
    }
}