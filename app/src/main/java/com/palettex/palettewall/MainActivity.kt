package com.palettex.palettewall

import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.view.MyBottomBarTest
import com.palettex.palettewall.view.MyTopBarTest
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
        setContent {
            PaletteWallTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val navController = rememberNavController()

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
                        bottomBar = { MyBottomBarTest(topViewModel, navController) }
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
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope() // Create a coroutine scope to handle drawer operations

    Column () {
        Spacer(modifier = Modifier.height(56.dp))
        Text(
            text = "About Us",
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    navController.navigate("Favorite")
                    scope.launch {
                        drawerState.close()
                    }
                }
        )
//        Text(text = "Item 2", modifier = Modifier.padding(16.dp))
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Close (X)",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .width(100.dp)
                .clickable {
                    // Close the drawer using the coroutine scope
                    scope.launch {
                        drawerState.close()
                    }
                }
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
}
