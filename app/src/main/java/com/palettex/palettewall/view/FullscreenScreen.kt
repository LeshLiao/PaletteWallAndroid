package com.palettex.palettewall.view

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.material3.icons.Icons
//import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.palettex.palettewall.R
import com.palettex.palettewall.view.component.BottomModal
import com.palettex.palettewall.viewmodel.AndroidDownloader
import com.palettex.palettewall.viewmodel.TopBarViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun FullscreenScreen(
    itemId: String,
    navController: NavController?,
    viewModel: TopBarViewModel?,
    wallpaperViewModel: WallpaperViewModel?
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val debounceTime = 1000L // Time in milliseconds to prevent double clicks
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) } // Manage dialog visibility
    var msg by remember { mutableStateOf("") }
    var ThumbnailImage: String? = wallpaperViewModel?.getThumbnailByItemId(itemId)
    var DownloadImage: String? = wallpaperViewModel?.getDownloadListLinkByItemId(itemId)
    var showModel by remember { mutableStateOf(false) }





    Scaffold(
        topBar = {},
        content = { paddingValues ->
            val test = paddingValues
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastClickTime > debounceTime) {
                            navController?.popBackStack()
                            lastClickTime = currentTime
                        }
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = ThumbnailImage),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                FloatingActionButton(
                    onClick = {
                        showModel = true
//                        downloadImage(context, DownloadImage) { myMsg ->
//                            msg = myMsg
//                            isDialogVisible = true // Show dialog after download starts
//                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 50.dp),
                    containerColor = Color.Transparent,  // Set background to transparent
                    shape = CircleShape,  // Makes the button round
                ) {
                    Card(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                    ) {
                        Image(
                            painterResource(R.drawable.download2),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Show the dialog when download starts
                if (isDialogVisible) {
                    ShowDownloadDialog (msg) { isDialogVisible = false } // Dismiss dialog on Confirm
                }
            }
        }
    )

    if (showModel) {
        BottomModal(
            context = context,
            onDismissRequest = { showModel = false },
            onAdWatched = {
                // Call downloadImage() after ad is watched
                wallpaperViewModel?.getDownloadListLinkByItemId(itemId)?.let {
                    Log.d("GDT","onAdWatched")
                    downloadImage(context, it) { msg ->
                        // Handle the download completion message
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

fun downloadImage(context: Context, imageUrl: String?, onDownloadEnqueued: (String) -> Unit) {
    if (imageUrl == null) {
        Toast.makeText(context, "Image URL not found", Toast.LENGTH_SHORT).show()
        return
    }
    val downloader = AndroidDownloader(context)
    downloader.downloadFile(imageUrl)

//    val request = DownloadManager.Request(Uri.parse(imageUrl))
//        .setTitle("Downloading Image")
//        .setDescription("Saving image to device")
//        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        .setAllowedOverMetered(true)
//        .setAllowedOverRoaming(true)
//        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg") // Save in Downloads folder
//
//    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//    val downloadId = downloadManager.enqueue(request)

    // Pass the downloadId to track the download
//    onDownloadEnqueued("Download Image.")
}

@Preview(showBackground = true)
@Composable
fun PreviewFullscreenScreen() {
    FullscreenScreen(
        "https://firebasestorage.googleapis.com/v0/b/palettex-37930.appspot.com/o/images%2Fitems%2F100047%2F210cc724-df6e-4ef1-91f9-61413cec25fe100047-1.jpg?alt=media&token=562929f0-f622-4134-b591-8b708e367919",
        null,
        null,
        null
    )
}
