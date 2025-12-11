package com.palettex.palettewall.ui.screens.home

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.palettex.palettewall.ui.screens.filter.FilterPage
import kotlinx.coroutines.CoroutineScope

@Composable
fun PaletteWallPage(
    wallpaperViewModel: HomeViewModel,
    billingViewModel: BillingViewModel,
    topViewModel: TopBarViewModel,
    isDarkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    outerNav: NavController,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = "Home"
) {
    val appVersion by wallpaperViewModel.versionName.collectAsState()
    val isFullScreen by wallpaperViewModel.isFullScreen.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    var topOffset by remember { mutableStateOf(0.dp) }
    var bottomOffset by remember { mutableStateOf(0.dp) }
    val isPremium by billingViewModel.isPremium.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isFullScreen,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor =  if (isDarkModeEnabled) Color(0xCC000000) else Color(0xEEFFFFFF)) {
                DrawerContent(navController, drawerState, wallpaperViewModel, billingViewModel) { catalog ->
                    outerNav.navigate("see_more/$catalog")
                }
            }
        },
        scrimColor = Color(0x55000000),
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                HomeTopBar(
                    topViewModel,
                    wallpaperViewModel,
                    coroutineScope,
                    drawerState,
                    onBannerHeightMeasured= {topOffset = it}
                ) {
                    outerNav.navigate("search")
                }
             },
            bottomBar = { BottomBarBox(topViewModel, wallpaperViewModel, navController, billingViewModel) { bottomOffset = it } },
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { innerPadding ->
            val test = innerPadding

            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                composable("Home") {
                    MainPageContent(bottomOffset, topViewModel, outerNav, navController, wallpaperViewModel, billingViewModel)
                }
                composable("Carousel") {
                    FilterPage(topOffset, bottomOffset, navController, wallpaperViewModel, topViewModel)
                }
                composable("Favorite") {
                    LikeCollection(topViewModel, wallpaperViewModel, navController, topOffset, bottomOffset, billingViewModel)
                }
                composable("AboutUs") {
                    AboutUs(navController)
                }
                composable("Settings") {
                    SettingsPage(topOffset, bottomOffset, isDarkModeEnabled, isPremium, appVersion, onDarkModeToggle)
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