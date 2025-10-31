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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.palettex.palettewall.data.PaletteRemoteConfig
import com.palettex.palettewall.ui.theme.PaletteWallTheme
import com.palettex.palettewall.view.OuterPage
import com.palettex.palettewall.viewmodel.AdManager
import com.palettex.palettewall.viewmodel.BillingViewModel
import com.palettex.palettewall.viewmodel.SettingsViewModel
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity" + "_GDT"
    }
    private val wallpaperViewModel: WallpaperViewModel by viewModels()
    private val topViewModel: TopBarViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val billingViewModel: BillingViewModel by viewModels {
        BillingViewModel.Factory(this)
    }

    // App update related variables
    private lateinit var appUpdateManager: AppUpdateManager
    private val UPDATE_REQUEST_CODE = 100

    // Setup the app update listener
    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app
            showUpdateCompletedNotification()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the AppUpdateManager
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(installStateUpdatedListener)

        enableEdgeToEdge()
        initializeServices()

        // Check for app updates
        checkForAppUpdates()

        registerReceiver(
            downloadCompletedReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            RECEIVER_EXPORTED
        )

        setContent {
            val isDarkTheme by settingsViewModel.isDarkThemeEnabled.collectAsState()

            PaletteWallTheme(darkTheme = isDarkTheme) {
                OuterPage(
                    wallpaperViewModel = wallpaperViewModel,
                    billingViewModel = billingViewModel,
                    topViewModel = topViewModel,
                    isDarkModeEnabled = isDarkTheme,
                    onDarkModeToggle = { it ->
                        settingsViewModel.toggleDarkTheme(it)
                    }
                )
            }
        }
    }

    private fun checkForAppUpdates() {
        Log.d(TAG, "Checking for app updates...")
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.d(TAG, "Update availability: ${appUpdateInfo.updateAvailability()}")

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // First try flexible update
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    startFlexibleUpdate(appUpdateInfo)
                }
                // If flexible update is not allowed, try immediate update
                else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    startImmediateUpdate(appUpdateInfo)
                }
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to check for updates: ${e.message}")
        }
    }

    private fun startFlexibleUpdate(appUpdateInfo: AppUpdateInfo) {
        Log.d(TAG, "Starting flexible update flow")
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                this,
                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                UPDATE_REQUEST_CODE
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start flexible update flow: ${e.message}")
        }
    }

    private fun startImmediateUpdate(appUpdateInfo: AppUpdateInfo) {
        Log.d(TAG, "Starting immediate update flow")
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                this,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                UPDATE_REQUEST_CODE
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start immediate update flow: ${e.message}")
        }
    }

    private fun showUpdateCompletedNotification() {
        // Show a toast notification
        Toast.makeText(
            this,
            "Update downloaded! Restart to apply the update.",
            Toast.LENGTH_LONG
        ).show()

        // Offer to install the update
        appUpdateManager.completeUpdate()
    }

    private fun initializeServices() {
        lifecycleScope.launch {
            billingViewModel.isPremium.collect { isPremium ->
                if (!isPremium) {
                    initializeMobileAds()
                }
                fetchFirebaseToken()
                initializeVersionName()
                initializeRemoteConfig()
            }
        }
    }

    private fun initializeMobileAds() {
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
        }
    }

    private fun initializeRemoteConfig() {
        Log.d(TAG,"initializeRemoteConfig()")
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
                    Log.d(TAG, "Remote config fetch and activate succeeded!")
                    PaletteRemoteConfig.updateLocalValues()
                    Log.d(TAG, " - shouldShow BannerAds= ${PaletteRemoteConfig.shouldShowBannerAds()}")
                    Log.d(TAG, " - shouldShow RewardAds= ${PaletteRemoteConfig.shouldShowRewardAds()}")
                    wallpaperViewModel.setIsRemoteConfigInitialized(true)
                } else {
                    Log.d(TAG, "Remote config fetch failed: ${task.exception?.message}")
                }
            }
    }

    private fun fetchFirebaseToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "FCM Token: $token")
                } else {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                }
            }
    }

    private fun initializeVersionName() {
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            wallpaperViewModel.setVersionName(pInfo.versionName.toString())
            Log.d(TAG, "version=${pInfo.versionName}")
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Version name not found", e)
        }
    }

    private val downloadCompletedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                Log.d(TAG, "Download complete with ID: $id")
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

        // Check for any pending updates or unfinished update flows
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            // If an update has been downloaded but not installed yet
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateCompletedNotification()
            }
            // If an immediate update is in progress, resume the update
            else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startImmediateUpdate(appUpdateInfo)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadCompletedReceiver)
        AdManager.cleanup()

        // Unregister the listener to prevent memory leaks
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}