package com.palettex.palettewall.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, viewModel: WallpaperViewModel) {
    val scope = rememberCoroutineScope() // Create a coroutine scope to handle drawer operations
    val versionName by viewModel.versionName.collectAsState()

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
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Build Version: $versionName",
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    scope.launch {
                        drawerState.close()
                    }
                }
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Close (X)",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .width(100.dp)
                .clickable {
                    scope.launch {
                        drawerState.close()
                    }
                }
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
}
