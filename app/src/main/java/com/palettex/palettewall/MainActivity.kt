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
import com.google.firebase.messaging.FirebaseMessaging
import com.palettex.palettewall.ui.theme.PaletteWallTheme
import com.palettex.palettewall.view.PaletteWallPage
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class MainActivity : ComponentActivity() {
    private val wallpaperViewModel: WallpaperViewModel by viewModels()
    private val topViewModel: TopBarViewModel by viewModels()

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]

                // Set a threshold to detect taps
                val threshold = 15.0f
                if (x > threshold || y > threshold || z > threshold) {
                    Log.d("GDT", "Tap gesture detected")
                    // Trigger actions, e.g., play next song or previous song
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Handle accuracy changes if needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        initializeMobileAds()
        fetchFirebaseToken()
        initializeVersionName()

        registerReceiver(downloadCompletedReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED)

        setContent {
            PaletteWallTheme {
                PaletteWallPage(
                    wallpaperViewModel = wallpaperViewModel,
                    topViewModel = topViewModel
                )
            }
        }

        // Initialize SensorManager and accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Register the accelerometer listener
        accelerometer?.let {
            sensorManager.registerListener(accelerometerListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun initializeMobileAds() {
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadCompletedReceiver)
    }
}
