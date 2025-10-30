package com.palettex.palettewall.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ImageLoader
import com.palettex.palettewall.PaletteWallApplication
import com.palettex.palettewall.R
import com.palettex.palettewall.utils.getImageSourceFromAssets
import com.palettex.palettewall.view.component.ProgressiveImageLoaderBest
import com.palettex.palettewall.viewmodel.SearchViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel

@Composable
fun SearchPage(
    wallpaperViewModel: WallpaperViewModel,
    outerNav: NavController
) {
    val context = LocalContext.current
    val searchViewModel: SearchViewModel = viewModel()
    val keyboardController = LocalSoftwareKeyboardController.current

    val listState = rememberLazyListState()
    val imageLoader = remember { ImageLoader(context) }
    val imageCacheList = PaletteWallApplication.imageCacheList

    val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by searchViewModel.searchResults.collectAsStateWithLifecycle()
    val isLoading by searchViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by searchViewModel.errorMessage.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {},
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                state = listState
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(16.dp),
                            onClick = {
                                outerNav.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            text = "Search",
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchViewModel.updateSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = {
                            Text(
                                text = "Search wallpapers...",
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchViewModel.clearSearch() }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.primary,
                            unfocusedTextColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                            }
                        )
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Error message
                errorMessage?.let { error ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = error,
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Empty state
                if (!isLoading && searchQuery.isEmpty() && searchResults.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Search for wallpapers",
                                color = Color.Gray,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enter keywords to find wallpapers",
                                color = Color.DarkGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Search results grid
                if (!isLoading && searchResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "${searchResults.size} results",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    itemsIndexed(
                        items = searchResults.chunked(3),
                        key = { index, _ -> "search_row_$index" }
                    ) { index, rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { wallpaper ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .aspectRatio(0.5f)
                                        .clickable {
                                            wallpaperViewModel.initFullScreenDataSourceByList(searchResults)
                                            outerNav.navigate("fullscreen/${wallpaper.itemId}")
                                        },
                                ) {
                                    val imageUrl = wallpaper.imageList.firstOrNull {
                                        it.type == "LD" && it.link.isNotEmpty()
                                    }?.link ?: ""

                                    val blurImageUrl = wallpaper.imageList.firstOrNull {
                                        it.type == "BL" && it.link.isNotEmpty()
                                    }?.link ?: ""

                                    val imageSource = imageUrl.getImageSourceFromAssets(context, imageCacheList)
                                    val blurSource = blurImageUrl.getImageSourceFromAssets(context, imageCacheList)

                                    Box(modifier = Modifier.fillMaxSize()) {
                                        ProgressiveImageLoaderBest(
                                            blurImageUrl = blurSource,
                                            fullImageSource = imageSource,
                                            imageLoader = imageLoader
                                        )
                                    }

                                    if (!wallpaper.freeDownload) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(6.dp)
                                        ) {
                                            Image(
                                                painterResource(R.drawable.diamond),
                                                contentDescription = "",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Fill remaining space if row has less than 3 items
                            if (rowItems.size < 3) {
                                repeat(3 - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}