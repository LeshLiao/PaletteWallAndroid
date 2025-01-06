package com.palettex.palettewall.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope
import androidx.compose.material3.ModalDrawerSheet
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun PaletteWallPage(
    wallpaperViewModel: WallpaperViewModel,
    topViewModel: TopBarViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = "Home",
) {
    val isFullScreen by wallpaperViewModel.isFullScreen.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isFullScreen,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor =  Color(0xCC000000)) {
                DrawerContent(navController, drawerState, wallpaperViewModel)
            }
        },
        scrimColor = Color(0x55000000),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black) // Set the background color to black
        ) {
            Scaffold(
//                modifier = Modifier.fillMaxSize().background(Color.Red),  // Add background color
                containerColor = Color.Black,  // Set container color to black
                topBar = { MyTopBarTest(topViewModel, coroutineScope, drawerState) },
                bottomBar = { MyBottomBarTest(topViewModel, wallpaperViewModel, navController) },
                snackbarHost = { SnackbarHost(snackBarHostState) }
            ) { innerPadding ->
                val customPadding = PaddingValues(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = 0.dp,
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    Modifier.padding(customPadding)
                ) {
                    composable("Home") {
//                        MainScreen(topViewModel, wallpaperViewModel)
                        ScrollingContent(topViewModel, navController, wallpaperViewModel)
                    }
                    composable("Favorite") {
                        FavoriteScreen("", navController, wallpaperViewModel, topViewModel)
                        // WallpaperScreen("",navController)
                    }
                    composable("AI") {
                        AIScreen("")
                    }
                    composable(
                        route = "fullscreen/{itemId}",
                        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("itemId")
                        if (itemId != null) {
                            FullscreenScreen(itemId, navController, wallpaperViewModel)
                        }
                    }
                }
                composable(
                    route = "fullscreen/{itemId}",
                    arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId")
                    if (itemId != null) {
                        FullscreenScreen(itemId, navController, wallpaperViewModel, topViewModel)
                    }
                }
            }
        }
    }
}