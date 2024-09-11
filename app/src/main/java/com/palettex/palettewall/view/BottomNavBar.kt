package com.palettex.palettewall.view

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.palettex.palettewall.R
@Composable
fun BottomNavBar(navController: NavHostController) {
//    val items = listOf("Home", "Favorite", "AI")
    val items = listOf("Home", "Favorite")
    NavigationBar(
        modifier = Modifier.height(80.dp),
        containerColor = Color.Black // Set the background color to black
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (item) {
                            "Home" -> ImageVector.vectorResource(id = R.drawable.ic_home)
                            "Favorite" -> ImageVector.vectorResource(id = R.drawable.ic_favorite)
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
                }
            )
        }
    }
}
