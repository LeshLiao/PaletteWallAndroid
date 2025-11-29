import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.palettex.palettewall.PaletteWallApplication
import com.palettex.palettewall.ui.components.getImageSourceFromAssets
import com.palettex.palettewall.ui.components.ProgressiveImageLoaderBest
import com.palettex.palettewall.ui.components.utility.throttleClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AutoScrollCarousel(
    items: List<String>, // List of image URLs
    modifier: Modifier = Modifier,
    autoScrollDelay: Long = 3000L, // Auto-scroll delay in milliseconds
    itemSpacing: Int = 16, // Spacing between items in dp
    onItemClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    var isUserScrolling by remember { mutableStateOf(false) }
    var isAutoScrolling by remember { mutableStateOf(false) }
    val imageLoader = remember { ImageLoader(context) }
    val imageCacheList = PaletteWallApplication.imageCacheList

    // Track scroll position to update indicator
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                currentIndex = index
            }
    }

    // Detect user interaction (manual scrolling)
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling && !isAutoScrolling) {
                    // This is user-initiated scroll, not programmatic
                    isUserScrolling = true
                }
            }
    }

    // Auto-scroll effect (only when user is not scrolling)
    LaunchedEffect(key1 = currentIndex, key2 = isUserScrolling) {
        if (isUserScrolling) {
            // User just scrolled manually, wait 5 seconds before resuming auto-scroll
            delay(5000L)
            isUserScrolling = false
        } else {
            // Normal auto-scroll interval
            delay(autoScrollDelay)
            val nextIndex = (currentIndex + 1) % items.size
            isAutoScrolling = true
            coroutineScope.launch {
                listState.animateScrollToItem(nextIndex)
                delay(300L) // Wait for animation to complete
                isAutoScrolling = false
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(top = 6.dp)
    ) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing.dp),
            contentPadding = PaddingValues(horizontal = (itemSpacing / 2).dp),
            modifier = Modifier.fillMaxWidth(),
            flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(
                lazyListState = listState
            )
        ) {

            items(items.size) { index ->
                val imageUrl = items[index]
                val imageSource = imageUrl.getImageSourceFromAssets(context, imageCacheList)
                CarouselItem(
                    imageUrl = imageSource,
                    modifier = Modifier
                        .fillParentMaxWidth(1f) // Takes full parent width minus padding
                        .padding(horizontal = (itemSpacing / 2).dp)
                        .aspectRatio(16f / 6f)
                        .throttleClick{
                            onItemClick(index)
                        },
                    imageLoader = imageLoader
                )
            }
        }

        // Indicator dots
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(items.size) { index ->
                val color = if (currentIndex % items.size == index) {
                    Color(0xFFCCCCCC) // Active indicator color
                } else {
                    Color.Gray.copy(alpha = 0.5f) // Inactive indicator color
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
            }
        }
    }
}

@Composable
private fun CarouselItem(
    imageUrl: String,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111111)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ProgressiveImageLoaderBest(
                blurImageUrl = "",
                fullImageSource = imageUrl,
                imageLoader = imageLoader
            )
        }
    }
}