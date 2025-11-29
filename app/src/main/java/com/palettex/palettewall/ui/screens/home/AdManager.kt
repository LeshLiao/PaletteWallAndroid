package com.palettex.palettewall.ui.screens.home

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.palettex.palettewall.BuildConfig
import com.palettex.palettewall.data.PaletteRemoteConfig

// Create a singleton object to manage the ad state
object AdManager {
    private var adMobBannerView: AdView? = null
    private var isRequestAdLoaded = false
    private var lastLoadTime: Long = 0
    // private const val REFRESH_INTERVAL = 60_000 * 2 // 2 minutes in milliseconds
    private var refreshInterval: Int = 60_000 * 2 // 2 minutes in milliseconds

    fun getOrCreateAd(context: Context): AdView {
        if (adMobBannerView == null) {
            adMobBannerView = AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = when {
                    BuildConfig.DEBUG_MODE || PaletteRemoteConfig.isBannerDebugMode() -> {
                        "ca-app-pub-3940256099942544/6300978111" // Test ad unit ID
                    }
                    PaletteRemoteConfig.shouldShowBannerAds() -> {
                        PaletteRemoteConfig.getBannerAdUnitId() // Production ad unit ID
                    }
                    else -> {
                        "" // No ads mode
                    }
                }
            }
        }
        return adMobBannerView!!
    }

    private fun shouldRefreshAd(): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime - lastLoadTime >= refreshInterval
    }

    fun loadAdIfNeeded(viewModel: HomeViewModel, forceReload: Boolean = false) {
        if ((!isRequestAdLoaded || forceReload || shouldRefreshAd()) &&
            PaletteRemoteConfig.shouldShowBannerAds()) {
            adMobBannerView?.let { adView ->
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
                Log.d("GDT","adView.loadAd(adRequest)")
                lastLoadTime = System.currentTimeMillis()

                adView.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        isRequestAdLoaded = true
                        refreshInterval = 60_000 * 2
                        viewModel.setBottomAdsLoaded(true)
                        Log.d("GDT","Ad Banner onAdLoaded()")
                        if (!BuildConfig.DEBUG_MODE) {
                            viewModel.sendLogEvent("0", "Ad_Banner_onAdLoaded_success")
                        }
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        isRequestAdLoaded = true // prevent multiple ad requests
                        refreshInterval = 60_000 * 1 // retry it after 1 minute
                        viewModel.setBottomAdsLoaded(false)
                        Log.e("GDT", "Ad Banner onAdFailedToLoad:${adError.message}")
                        if (!BuildConfig.DEBUG_MODE) {
                            viewModel.sendLogEvent("0", "Ad_Banner_onAdFailedToLoad:${adError.message}")
                        }
                    }
                }
            }
        }
    }

    fun cleanup() {
        adMobBannerView?.destroy()
        adMobBannerView = null
        isRequestAdLoaded = false
        lastLoadTime = 0
    }
}

