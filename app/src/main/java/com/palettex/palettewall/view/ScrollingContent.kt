package com.palettex.palettewall.view
import TopBarViewModel
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
        items(50) {
            Row (modifier = Modifier.fillMaxWidth()) {
                Text("Item #$it", modifier = Modifier
                    .padding(16.dp)
                    )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",

                )
            }

        }
    }
}

@Composable
fun CounterExample() {
    // Declare a state variable to hold the count value
    var count by remember { mutableIntStateOf(0) }

    // UI that displays the count and a button to increase it
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Display the current count
        Text(
            text = "Count: $count",
        )

        // Button to increase the count
        Button(
            onClick = {
                count++  // Increase the count when button is clicked
            },
        ) {
            Text(text = "Increase Count")
        }
    }
}
