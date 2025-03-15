package com.palettex.palettewall.view.component


import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.palettex.palettewall.R
import com.palettex.palettewall.viewmodel.BillingViewModel
import com.palettex.palettewall.viewmodel.WallpaperViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PremiumModal(
    billingViewModel: BillingViewModel,
    onDismissRequest: () -> Unit = {},
    onAdWatchedAndStartDownload: () -> Unit
) {
    val isPremium by billingViewModel.isPremium.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true  // Avoid partially expanded state
    )

    ModalBottomSheet(
        containerColor = Color.Transparent,
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                CommonButton(
                    text = stringResource(R.string.premium_download),
//                    textColor = Color(0xfffbad0b),
                    textColor = Color.Blue,
                    backgroundColor = Color.White
                ) {
                    onDismissRequest()
                    onAdWatchedAndStartDownload()
                }
            }
            Spacer(Modifier.height(8.dp))
            CommonButton(stringResource(R.string.cancel)) { onDismissRequest() }
        }

        Spacer(
            Modifier
                .windowInsetsBottomHeight(WindowInsets.navigationBarsIgnoringVisibility)
                .background(Color.White)
        )
        Spacer(Modifier.height(8.dp))
    }
}