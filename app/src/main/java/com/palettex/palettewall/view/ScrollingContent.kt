package com.palettex.palettewall.view
import TopBarViewModel
import android.widget.Space
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.res.colorResource
import com.palettex.palettewall.R

@Composable
fun ScrollingContent(viewModel: TopBarViewModel) {
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
            Text("", modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth())
        }
        item {
            LazyRow {
                repeat(5) { index ->
                    item {
                        Card(
                            modifier = Modifier.padding(8.dp,20.dp,8.dp,8.dp), // Modified: Removed background Modifier
                            elevation = CardDefaults.cardElevation(8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors( // Added: Setting background color using colors parameter
                                containerColor = colorResource(id = R.color.teal_200) // Added: Converted resource color to Compose Color
                            )
                        ) {
                            Text(
                                text = "Catalog $index",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

            }

        }
        items(50) {
            Text("Item #$it", modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth())
        }
    }
}
