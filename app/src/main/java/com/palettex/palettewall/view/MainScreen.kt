package com.palettex.palettewall.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel

@Composable
fun MainScreen(viewModel: TopBarViewModel, wallpaperViewModel: WallpaperViewModel ) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        NavHost(navController = navController, startDestination = "main") {
            composable("main") {
                Box {
                    ScrollingContent(viewModel, navController, wallpaperViewModel)
                }
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
    }
}
