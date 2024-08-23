package com.palettex.palettewall.view

import TopBarViewModel
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun MainScreen(viewModel: TopBarViewModel, wallpaperViewModel: WallpaperViewModel ) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(navController = navController, startDestination = "main") {
            composable("main") {
                Box {
                    ScrollingContent(viewModel, navController, wallpaperViewModel)
                    TopBar(viewModel)
                }
            }
            composable(
                route = "fullscreen/{encodedUrl}",
                arguments = listOf(navArgument("encodedUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedUrl = backStackEntry.arguments?.getString("encodedUrl")
                val imageUrl = encodedUrl?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) }
                imageUrl?.let { FullscreenScreen(it, navController) }
            }
        }
    }
}
