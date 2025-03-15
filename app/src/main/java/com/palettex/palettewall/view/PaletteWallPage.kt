package com.palettex.palettewall.view

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.palettex.palettewall.viewmodel.BillingViewModel

@Composable
fun PaletteWallPage(
    wallpaperViewModel: WallpaperViewModel,
    billingViewModel: BillingViewModel,
    topViewModel: TopBarViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = "Home",
) {
    val isFullScreen by wallpaperViewModel.isFullScreen.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    var topOffset by remember { mutableStateOf(0.dp) }
    var bottomOffset by remember { mutableStateOf(0.dp) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isFullScreen,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor =  Color(0xCC000000)) {
                DrawerContent(navController, drawerState, wallpaperViewModel, billingViewModel)
            }
        },
        scrimColor = Color(0x55000000),
    ) {
        Scaffold(
            containerColor = Color.Black,
            topBar = { HomeTopBar(topViewModel, wallpaperViewModel, coroutineScope, drawerState) { topOffset = it } },
            bottomBar = { BottomBarBox(topViewModel, wallpaperViewModel, navController, billingViewModel) { bottomOffset = it } },
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { innerPadding ->
            val test = innerPadding

            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                composable("Home") {
                    ScrollingContent(bottomOffset, topViewModel, navController, wallpaperViewModel, billingViewModel)
                }
                composable("Carousel") {
                    CarouselPage(topOffset, bottomOffset, navController, wallpaperViewModel, topViewModel)
                }
                composable("Favorite") {
                    LikeCollection(topViewModel, wallpaperViewModel, navController, topOffset, bottomOffset)
                }
                composable("AboutUs") {
                    AboutUs(navController)
                }
//                composable("AI") {
//                    AIScreen("")
//                }

                composable(
                    route = "fullscreen/{catalog}/{itemId}",
                    arguments = listOf(
                        navArgument("catalog") {
                            type = NavType.StringType
                            defaultValue = "" // Optional default value if not provided
                        },
                        navArgument("itemId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val catalog = backStackEntry.arguments?.getString("catalog")
                    val itemId = backStackEntry.arguments?.getString("itemId")
                    if (itemId != null) {
                        FullscreenScreen(
                            catalog ?: "",
                            itemId,
                            navController,
                            wallpaperViewModel,
                            billingViewModel,
                            topViewModel
                        )
                    }
                }
            }
        }
    }
}