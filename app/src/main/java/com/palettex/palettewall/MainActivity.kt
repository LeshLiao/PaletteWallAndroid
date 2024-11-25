package com.palettex.palettewall

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.palettex.palettewall.ui.theme.PaletteWallTheme
import com.palettex.palettewall.view.AIScreen
import com.palettex.palettewall.view.DrawerContent
import com.palettex.palettewall.view.FavoriteScreen
import com.palettex.palettewall.view.MainScreen
import com.palettex.palettewall.view.MyBottomBarTest
import com.palettex.palettewall.view.MyTopBarTest
import com.palettex.palettewall.viewmodel.MainViewModel
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val wallpaperViewModel: WallpaperViewModel by viewModels()
    private val topViewModel: TopBarViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GDT","onCreate")
        enableEdgeToEdge()

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

        // Dynamically register the receiver for ACTION_DOWNLOAD_COMPLETE
        registerReceiver(downloadCompletedReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            RECEIVER_EXPORTED
        )

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    Log.w("GDT", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                Log.d("GDT", "FCM Token: $token")
            }

        try {
            val pInfo: PackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0)
            val version = pInfo.versionName
            wallpaperViewModel.setVersionName(version)
            Log.d("GDT","version="+ version)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


        setContent {
            PaletteWallTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val navController = rememberNavController()
                val isFullScreen by wallpaperViewModel.isFullScreen.collectAsState()

                // Create SnackbarHostState to handle Snackbar actions
                val snackbarHostState = remember { SnackbarHostState() }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = !isFullScreen,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerShape = MaterialTheme.shapes.medium,
                            drawerTonalElevation = 4.dp,
                            drawerContainerColor = Color.Black.copy(alpha = 0.7f),
                            drawerContentColor = Color.White,

                        ) {
                            DrawerContent(navController = navController,drawerState = drawerState, viewModel = wallpaperViewModel)
                        }
                    },
                    scrimColor = Color(0x99000000), // Semi-transparent black scrim
                ) {
                    Scaffold(
                        topBar = { MyTopBarTest(topViewModel, scope, drawerState) },
                        bottomBar = { MyBottomBarTest(topViewModel, navController) },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        // Apply custom padding values to reduce space
                        val customPadding = PaddingValues(
//                            bottom = 80.dp, // Adjust as needed
                            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
//                        top = innerPadding.calculateTopPadding(),
                            top = 0.dp,
                            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                        )
                        NavHost(
                            navController = navController,
                            startDestination = "Home", // Set the default screen to Home
                            Modifier.padding(customPadding) // Add padding to avoid overlapping with BottomNavBar
                        ) {
                            composable("Home") {
                                MainScreen(topViewModel, wallpaperViewModel)
                            }
                            composable("Favorite") {
                                FavoriteScreen("This is Favorite", navController)
                            }
                            composable("AI") {
                                AIScreen("This is AI")
                            }
                            composable("FullScreenImage") {
//                                FullScreen2(navController, "https://www.palettex.ca/images/items/100002/100002.jpg")
                            }
                        }
                    }
                }
            }
        }
    }

    // Define your BroadcastReceiver as a field in the MainActivity
    private val downloadCompletedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                Log.d("GDT", "Download complete with ID: $id")

                if (id != -1L) {
                    wallpaperViewModel.updateDownloadBtnStatus(2)
                    showDownloadCompleted()
                }
            }
        }
    }

    private fun showDownloadCompleted() {
        Toast.makeText(this, "Download completed successfully!!!", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver to prevent memory leaks
        unregisterReceiver(downloadCompletedReceiver)
    }
}
