package com.palettex.palettewall.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.data.WallpaperDatabase
import com.palettex.palettewall.model.WallpaperItem
import com.palettex.palettewall.view.component.ColorPaletteMatrix
import com.palettex.palettewall.view.component.LikeButton
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun CarouselPage(
    topOffset: Dp,
    bottomOffset: Dp,
    navController: NavController,
    wallpaperViewModel: WallpaperViewModel,
    topViewModel: TopBarViewModel,
) {
    val context: Context = LocalContext.current
    val allWallpapers by wallpaperViewModel.wallpapers.collectAsState()
    val filterWallpapers by wallpaperViewModel.filterWallpapers.collectAsState()
    val firstSelectedColor by wallpaperViewModel.firstSelectedColor.collectAsState()
    val secondSelectedColor by wallpaperViewModel.secondSelectedColor.collectAsState()
    var colorSelectedList by remember { mutableStateOf<List<Color>>(emptyList()) }
    var colorBrowseList by remember { mutableStateOf<List<Color>>(emptyList()) }

    LaunchedEffect(Unit) {
        topViewModel.showTopBar()
        if (filterWallpapers.isEmpty()) {
            wallpaperViewModel.updateFilteredWallpapers()
        }
    }

    // Update selected colors when they change
    LaunchedEffect(firstSelectedColor, secondSelectedColor) {
        val newList = mutableListOf<Color>()
        firstSelectedColor?.let { newList.add(it) }
        secondSelectedColor?.let { newList.add(it) }
        colorSelectedList = newList
    }

    Column {
        Spacer(modifier = Modifier.height(topOffset))
        ColorPaletteMatrix(wallpaperViewModel)
//        ColorInfoDisplay(colorSelectedList)
//        ColorInfoDisplay(colorBrowseList)
        Box(modifier = Modifier.fillMaxSize()) {
            WallpaperCarousel2(
                filterWallpapers = filterWallpapers,
                wallpaperViewModel = wallpaperViewModel,
                bottomOffset = bottomOffset,
                onWallpaperSelected = { itemId ->
                    topViewModel.hideTopBar()
                    navController.navigate("fullscreen/${itemId}")
                },
                onColorTagsChanged = { tags ->
                    colorBrowseList = tags
                }
            )
        }
    }
}

@Composable
fun ColorInfoDisplay(colorList: List<Color>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        if (colorList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Black)
            )
        } else {
            colorList.forEach { color ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color)
                ) {
                    Text(
                        text = colorToARGBString(color),
                        color = Color.White,
                        modifier = Modifier.padding(4.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun colorToARGBString(color: Color): String {
    return String.format(
        "#%08X",
        (color.alpha * 255).toInt() shl 24 or
                ((color.red * 255).toInt() shl 16) or
                ((color.green * 255).toInt() shl 8) or
                (color.blue * 255).toInt()
    )
}

@Composable
fun WallpaperCarousel2(
//    wallpapers: List<String>,
    filterWallpapers: List<WallpaperItem>,
    wallpaperViewModel: WallpaperViewModel,
    bottomOffset: Dp,
    onWallpaperSelected: (String) -> Unit,
    onColorTagsChanged: (List<Color>) -> Unit = {}
) {
    // Check if the list is empty first
    if (filterWallpapers.isEmpty()) { return }

    val middlePageIndex = filterWallpapers.size / 2
    val pagerState = rememberPagerState(
        initialPage = middlePageIndex,
        pageCount = { filterWallpapers.size }
    )

    LaunchedEffect(pagerState.currentPage, filterWallpapers) {
        val currentWallpaper = filterWallpapers[pagerState.currentPage]
        val colorTags = currentWallpaper.tags
            .filter { it.startsWith("#") }

        val colorList = colorTags.map { hexString ->
            Color(android.graphics.Color.parseColor(hexString))
        }
//        Log.d("GDT", "current(${currentWallpaper.itemId}):=${colorTags.joinToString(", ")}")
        onColorTagsChanged(colorList) // Notify parent composable
    }

    val scope = rememberCoroutineScope()

    val context: Context = LocalContext.current
    val database = remember { WallpaperDatabase.getDatabase(context) }
    val dao = remember { database.likedWallpaperDao() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 90.dp),
            pageSpacing = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                //.height(LocalConfiguration.current.screenHeightDp.dp / 2)
                .weight(1f)
        ) { page ->
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue
            val scale = 1f - (pageOffset * 0.15f).coerceIn(0f, 0.15f)
            val alpha = 1f - (pageOffset * 0.5f).coerceIn(0f, 0.5f)
            val itemId = filterWallpapers[page].itemId
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .aspectRatio(0.50f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
                    .clickable { onWallpaperSelected(itemId) }
            ) {
                val imageUrl = wallpaperViewModel.getThumbnailByItemId(itemId)
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Wallpaper ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                val isLiked by dao.isWallpaperLiked(itemId).collectAsState(initial = false)

                Box (
                    modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp)
                ) {
                    LikeButton(isLiked, dao, itemId, wallpaperViewModel, coroutineScope)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Thumbnail LazyRow
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(vertical = 8.dp)
        ) {
            items(filterWallpapers.size) { index ->
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(
                            width = 2.dp,
                            color = if (pagerState.currentPage == index) Color.LightGray
                            else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                ) {
                    val imageUrl = wallpaperViewModel.getThumbnailByItemId(filterWallpapers[index].itemId)
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Thumbnail ${index + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(bottomOffset))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTestCarousel() {
    val navController = rememberNavController()
    val mockWallpaperViewModel = WallpaperViewModel().apply {}
    val mockTopBarViewModel = TopBarViewModel().apply {}
    CarouselPage(100.dp, 100.dp, navController , mockWallpaperViewModel, mockTopBarViewModel)
}