package com.palettex.palettewall

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.palettex.palettewall.ui.theme.PaletteWallTheme
import com.palettex.palettewall.view.AIScreen
import com.palettex.palettewall.view.BottomNavBar
import com.palettex.palettewall.view.FavoriteScreen
import com.palettex.palettewall.view.MainScreen
import com.palettex.palettewall.viewmodel.MainViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val wallpaperViewModel: WallpaperViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GDT","onCreate")
        enableEdgeToEdge()
        setContent {
            PaletteWallTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavBar(navController = navController)
                    }
                ) { innerPadding ->
                    // Apply custom padding values to reduce space
                    val customPadding = PaddingValues(
                        bottom = 80.dp, // Adjust as needed
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
//                        top = innerPadding.calculateTopPadding(),
                        top = 0.dp,
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                    )
                    NavHost(
                        navController = navController,
                        startDestination = "Home", // Set the default screen to Home
                        Modifier.padding(customPadding) // Add padding to avoid overlapping with BottomNavBar
                    ) {
                        composable("Home") {
                            MainScreen(viewModel.topBarViewModel, wallpaperViewModel)
                        }
                        composable("Favorite") {
                            FavoriteScreen("This is Favorite")
                        }
                        composable("AI") {
                            AIScreen("This is AI")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name! Room Test",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PaletteWallTheme {
        Greeting("Android")
    }
}