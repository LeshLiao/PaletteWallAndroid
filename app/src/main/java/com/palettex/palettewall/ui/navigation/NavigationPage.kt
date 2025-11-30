package com.palettex.palettewall.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.palettex.palettewall.ui.screens.home.BillingViewModel
import com.palettex.palettewall.ui.screens.home.FullscreenScreen
import com.palettex.palettewall.ui.screens.home.HomeViewModel
import com.palettex.palettewall.ui.screens.home.PaletteWallPage
import com.palettex.palettewall.ui.screens.home.SearchPage
import com.palettex.palettewall.ui.screens.home.SeeMorePage
import com.palettex.palettewall.ui.screens.home.TopBarViewModel
import com.palettex.palettewall.ui.screens.search.SearchViewModel

sealed class Screen(val route: String) {
    data object PaletteWallPage : Screen("PaletteWallPage")
    data object Search : Screen("search")
    data object Fullscreen : Screen("fullscreen/{itemId}")
    data object SeeMore : Screen("see_more/{catalog}")
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavigationPage(
    outerNav: NavHostController = rememberNavController(),
    wallpaperViewModel: HomeViewModel,
    billingViewModel: BillingViewModel,
    topViewModel: TopBarViewModel,
    isDarkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val customEasing = CubicBezierEasing(0.0f, 0.93f, 0.74f, 0.97f)
    val customEasingOut = CubicBezierEasing(0.95f, 0.05f, 0.95f, 0.23f)

    NavHost(
        navController = outerNav,
        startDestination = Screen.PaletteWallPage.route,
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
        composable(
            route = Screen.PaletteWallPage.route
        ) {
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
            route = Screen.SeeMore.route,
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
            route = Screen.Fullscreen.route,
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
            route = Screen.Search.route,
        ) { backStackEntry ->
            val searchViewModel: SearchViewModel = hiltViewModel()
            SearchPage(
                searchViewModel = searchViewModel,
                wallpaperViewModel = wallpaperViewModel,
                outerNav = outerNav
            )
        }
    }
}