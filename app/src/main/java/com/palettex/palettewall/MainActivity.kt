package com.palettex.palettewall

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.palettex.palettewall.data.PaletteRemoteConfig
import com.palettex.palettewall.ui.theme.PaletteWallTheme
import com.palettex.palettewall.view.PaletteWallPage
import com.palettex.palettewall.viewmodel.AdManager
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val wallpaperViewModel: WallpaperViewModel by viewModels()
    private val topViewModel: TopBarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        initializeMobileAds()
        fetchFirebaseToken()
        initializeVersionName()
        initializeRemoteConfig()

        registerReceiver(
            downloadCompletedReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            RECEIVER_EXPORTED
        )

        setContent {
            PaletteWallTheme {
                PaletteWallPage(
                    wallpaperViewModel = wallpaperViewModel,
                    topViewModel = topViewModel
                )
            }
        }
    }

    private fun initializeMobileAds() {
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
        }
    }

    private fun initializeRemoteConfig() {
        Log.d("GDT","initializeRemoteConfig()")
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

        val intervalValue = if (BuildConfig.DEBUG_MODE) {
            0L // Fetch instantly for development/debug
        } else {
            3600L // Fetch every hour for production
        }

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = intervalValue
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("GDT", "Remote config fetch and activate succeeded!")
                    PaletteRemoteConfig.updateLocalValues()
                    Log.d("GDT", " - shouldShow BannerAds= ${PaletteRemoteConfig.shouldShowBannerAds()}")
                    Log.d("GDT", " - shouldShow RewardAds= ${PaletteRemoteConfig.shouldShowRewardAds()}")
                    wallpaperViewModel.setIsRemoteConfigInitialized(true)
                } else {
                    Log.d("GDT", "Remote config fetch failed: ${task.exception?.message}")
                }
            }
    }

    private fun fetchFirebaseToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("GDT", "FCM Token: $token")
                } else {
                    Log.w("GDT", "Fetching FCM registration token failed", task.exception)
                }
            }
    }

    private fun initializeVersionName() {
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            wallpaperViewModel.setVersionName(pInfo.versionName)
            Log.d("GDT", "version=${pInfo.versionName}")
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("GDT", "Version name not found", e)
        }
    }

    private val downloadCompletedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                Log.d("GDT", "Download complete with ID: $id")
                if (id != -1L) {
                    wallpaperViewModel.updateDownloadBtnStatus(2)
                    showToast()
                }
            }
        }
    }

    private fun showToast() {
        Toast.makeText(this, "Download completed successfully!!!",
            Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        if (wallpaperViewModel.isRemoteConfigInitialized.value) {
            AdManager.loadAdIfNeeded(wallpaperViewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadCompletedReceiver)
        AdManager.cleanup()
    }
}