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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubscriptionModal(
    context: Context,
    onDismissRequest: () -> Unit = {},
    billingViewModel: BillingViewModel,
) {
    val isPremium by billingViewModel.isPremium.collectAsState()
    var weeklyPrice by remember { mutableStateOf("") }
    var monthlyPrice by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        billingViewModel.getSubscriptionPrices { prices ->
            weeklyPrice = prices["weekly_premium"] ?: ""
            monthlyPrice = prices["monthly_premium"] ?: ""
        }
    }

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
            val coroutineScope = rememberCoroutineScope()
            Column {
                CommonButton(
                    text = if (weeklyPrice.isNotEmpty()) "Weekly Premium $weeklyPrice/week" else "Weekly Premium",
                    textColor = Color.White,
                    backgroundColor = Color(0xfffbad0b)
                ) {
                    billingViewModel.launchBillingFlow(context as Activity, "weekly_premium")
                    onDismissRequest()
                }
                Spacer(modifier = Modifier.height(1.dp))
                CommonButton(
                    text = if (monthlyPrice.isNotEmpty()) "Monthly Premium $monthlyPrice/month" else "Monthly Premium",
                    textColor = Color.White,
                    backgroundColor = Color(0xfffbad0b)
                ) {
                    billingViewModel.launchBillingFlow(context as Activity, "monthly_premium")
                    onDismissRequest()
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