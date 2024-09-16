package com.palettex.palettewall

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.palettex.palettewall.ui.theme.PaletteWallTheme
import com.palettex.palettewall.view.AIScreen
import com.palettex.palettewall.view.BottomNavBar
import com.palettex.palettewall.view.FavoriteScreen
import com.palettex.palettewall.view.MainScreen
import com.palettex.palettewall.viewmodel.MainViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import com.palettex.palettewall.viewmodel.TopBarViewModel
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.MobileAds
import com.palettex.palettewall.view.DrawerContent
import com.palettex.palettewall.view.MyBottomBarTest
import com.palettex.palettewall.view.MyTopBarTest
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



        setContent {
            PaletteWallTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val navController = rememberNavController()

                // Create SnackbarHostState to handle Snackbar actions
                val snackbarHostState = remember { SnackbarHostState() }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerShape = MaterialTheme.shapes.medium,
                            drawerTonalElevation = 4.dp,
                            drawerContainerColor = Color.Black.copy(alpha = 0.7f),
                            drawerContentColor = Color.White,

                        ) {
                            DrawerContent(navController = navController,drawerState = drawerState )
                        }
                    },
                    gesturesEnabled = false,
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
                // Trigger Snackbar on download complete
                wallpaperViewModel.downloadCompleteEvent.observe(this) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Download completed successfully!")
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
                Log.d("DownloadReceiver", "Download complete with ID: $id")

                if (id != -1L) {
                    wallpaperViewModel.updateDownloadBtnStatus(2)
//                    Toast.makeText(context, "MainActivity: Downloaded successfully!!!", Toast.LENGTH_SHORT).show()
                    wallpaperViewModel.notifyDownloadComplete()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver to prevent memory leaks
        unregisterReceiver(downloadCompletedReceiver)
    }
}
