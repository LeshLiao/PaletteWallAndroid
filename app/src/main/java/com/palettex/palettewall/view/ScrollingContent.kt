package com.palettex.palettewall.view
import TopBarViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.R
import com.palettex.palettewall.model.Wallpaper
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random

@Composable
fun ScrollingContent(viewModel: TopBarViewModel, navController: NavController) {

    val listState = rememberLazyListState()
    val lastScrollOffset = remember { mutableStateOf(0) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { currentScrollOffset ->
                val delta = currentScrollOffset - lastScrollOffset.value
                viewModel.onScroll(delta.toFloat())
                lastScrollOffset.value = currentScrollOffset
            }
    }

    LazyColumn(state = listState) {
        item {
            Spacer(modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth())
        }

        item {
            LazyRow {
                repeat(5) { index ->
                    item {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .padding(
                                        top = 16.dp,
                                        bottom = 0.dp
                                    ) // Adjusted padding to leave space for the text
                                    .fillMaxWidth(), // Optional: Make the card take full width of the column
                                elevation = CardDefaults.cardElevation(8.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorResource(id = R.color.teal_200)
                                )
                            ) {
                                // Your card content here
                                Text(
                                    text = "",
                                    modifier = Modifier.padding(100.dp,36.dp,0.dp,0.dp) // Adjust padding inside the card as needed
                                )
                            }

                            Text(
                                text = "Catalog $index",
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp) // Adjust padding as needed
                            )
                        }
                    }
                }
            }
        }

        val wallpapers = List(12) {
            val size = Random.nextInt(200, 300) // Generates a random value between 200 (inclusive) and 300 (inclusive)
            Wallpaper("https://picsum.photos/$size")
        }

        items(wallpapers.chunked(3)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { wallpaper ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.75f)
                            .clickable {
                                val encodedUrl = URLEncoder.encode(
                                    wallpaper.imageUrl,
                                    StandardCharsets.UTF_8.name()
                                )
                                navController.navigate("fullscreen/$encodedUrl")
                            },

                        shape = RoundedCornerShape(8.dp)
                    ) {
                        // Replace with your image loading logic
                        Image(
                            painter = rememberAsyncImagePainter(model = wallpaper.imageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}