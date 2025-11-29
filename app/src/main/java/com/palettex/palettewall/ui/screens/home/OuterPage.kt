package com.palettex.palettewall.ui.screens.home

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.palettex.palettewall.ui.screens.home.BillingViewModel
import com.palettex.palettewall.ui.screens.home.TopBarViewModel
import com.palettex.palettewall.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OuterPage(
    outerNav: NavHostController = rememberNavController(),
    wallpaperViewModel: HomeViewModel,
    billingViewModel: BillingViewModel,
    topViewModel: TopBarViewModel,
    isDarkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val customEasing = CubicBezierEasing(0.0f, 0.93f, 0.74f, 0.97f)
    val customEasingOut = CubicBezierEasing(0.95f, 0.05f, 0.95f, 0.23f)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        val test = innerPadding

        NavHost(
            navController = outerNav,
            startDestination = "PaletteWallPage",
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(500, easing = customEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(600, easing = customEasingOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(500, easing = customEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(500, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) {
            composable("PaletteWallPage") {
                PaletteWallPage(
                    wallpaperViewModel = wallpaperViewModel,
                    billingViewModel = billingViewModel,
                    topViewModel = topViewModel,
                    isDarkModeEnabled,
                    onDarkModeToggle,
                    outerNav = outerNav
                )
            }
            composable(
                route = "see_more/{catalog}",
                arguments = listOf(
                    navArgument("catalog") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val catalog = backStackEntry.arguments?.getString("catalog")
                if (catalog != null) {
                    SeeMorePage(
                        catalog,
                        wallpaperViewModel,
                        outerNav
                    )
                }
            }
            composable(
                route = "fullscreen/{itemId}",
                arguments = listOf(
                    navArgument("itemId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")
                if (itemId != null) {
                    FullscreenScreen(
                        "",
                        itemId,
                        outerNav,
                        wallpaperViewModel,
                        billingViewModel,
                        topViewModel
                    )
                }
            }
            composable(
                route = "search",
            ) { backStackEntry ->
                SearchPage(
                    wallpaperViewModel,
                    outerNav
                )
            }
        }
    }
}