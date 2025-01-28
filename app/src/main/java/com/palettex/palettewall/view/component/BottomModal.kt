package com.palettex.palettewall.view.component

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.palettex.palettewall.R
import kotlinx.coroutines.launch
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.data.PaletteRemoteConfig
import com.palettex.palettewall.viewmodel.WallpaperViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BottomModal(
    context: Context,
    onDismissRequest: () -> Unit = {},
    wallpaperViewModel: WallpaperViewModel,
    onAdWatchedAndStartDownload: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true  // Avoid partially expanded state
    )

    val appSettings by wallpaperViewModel.appSettings.collectAsState()
    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    var isAdReady by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    fun showRewardedAd() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // The ad was shown
                Log.d("GDT", "rewardedAd was shown.")
                if (!BuildConfig.DEBUG_MODE) {
                    wallpaperViewModel.sendLogEvent("0", "rewardedAd_was_shown")
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
                    wallpaperViewModel.sendLogEvent("0", "rewardedAd_failed_to_show")
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
                    Toast.makeText(context, "Msg: ${adError.message}, please try again.", Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

    ModalBottomSheet(
        containerColor = Color.Transparent,
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    if (!isLoading && !isAdReady) {
                        coroutineScope.launch {
                            // If no ads should be shown, skip ad loading
                            if (!PaletteRemoteConfig.shouldShowRewardAds()) {
                                onDismissRequest()
                                onAdWatchedAndStartDownload()
                            } else {
                                startLoadAd()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                .padding(horizontal = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.purple_500)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        var buttonTextId = R.string.no_ad_free_download
                        if (PaletteRemoteConfig.shouldShowRewardAds()) {
                            buttonTextId = R.string.show_ad_free_download
                        }
                        Text(text = stringResource(buttonTextId), color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
        Spacer(
            Modifier
                .windowInsetsBottomHeight(WindowInsets.navigationBarsIgnoringVisibility)
                .background(Color.White)
        )
    }
}
