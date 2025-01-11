package com.palettex.palettewall.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.palettex.palettewall.R
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel

@Composable
fun BottomBarBox(
    topViewModel: TopBarViewModel,
    wallpaperViewModel: WallpaperViewModel,
    navController: NavHostController,
    onHeightMeasured: (Dp) -> Unit
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = topViewModel.isTopBarVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 2000)
        ) + fadeIn(animationSpec = tween(durationMillis = 2000)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 2000)
        ) + fadeOut(animationSpec = tween(durationMillis = 2000))
    ) {
        val items = listOf("Home", "Carousel", "Favorite")
        NavigationBar(
//            modifier = Modifier.height(80.dp).border(1.dp,Color.Red),
//            modifier = Modifier.border(1.dp,Color.Red).wrapContentHeight(),
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    val navigationBarHeight = with(density) { coordinates.size.height.toDp() }
                    onHeightMeasured(navigationBarHeight)
                },
            containerColor = Color.Black // Set the background color to black
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = when (item) {
                                "Home" -> ImageVector.vectorResource(id = R.drawable.icon_home)
                                "Carousel" -> ImageVector.vectorResource(id = R.drawable.icon_search_alt)
                                "Favorite" -> ImageVector.vectorResource(id = R.drawable.icon_heart)
//                            "AI" -> ImageVector.vectorResource(id = R.drawable.ic_ai)
                                else -> ImageVector.vectorResource(id = R.drawable.ic_home)
                            },
                            contentDescription = item,
                            modifier = Modifier.size(25.dp), // Set the size of the icon
                            tint = Color.White // Set the icon color to white
                        )
                    },
//                label = { Text(item) },
//                selected = navController.currentDestination?.route == item,
                    selected = false,
                    onClick = {
                        navController.navigate(item) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        if (item == "Home") {
                            wallpaperViewModel.scrollToTop()
                        }
                    }
                )
            }
        }
    }
}