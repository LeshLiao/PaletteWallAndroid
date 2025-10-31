package com.palettex.palettewall.view.component

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.palettex.palettewall.R
import com.palettex.palettewall.viewmodel.BillingViewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubscriptionModal(
    context: Context?,
    onDismissRequest: () -> Unit = {},
    billingViewModel: BillingViewModel
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

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        containerColor = Color.Transparent,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.5f), // Let gray area cover top system bar
        windowInsets = WindowInsets.navigationBars // Let gray area cover top system bar
    ) {
        SubscriptionOptions(
            weeklyPrice = weeklyPrice,
            monthlyPrice = monthlyPrice,
            onWeeklySubscribe = {
                context?.let {
                    billingViewModel.launchBillingFlow(it as Activity, "weekly_premium")
                }
                onDismissRequest()
            },
            onMonthlySubscribe = {
                context?.let {
                    billingViewModel.launchBillingFlow(it as Activity, "monthly_premium")
                }
                onDismissRequest()
            },
            onCancel = onDismissRequest
        )

        Spacer(
            Modifier
                .windowInsetsBottomHeight(WindowInsets.navigationBarsIgnoringVisibility)
                .background(Color.White)
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun SubscriptionOptions(
    weeklyPrice: String,
    monthlyPrice: String,
    onWeeklySubscribe: () -> Unit,
    onMonthlySubscribe: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Title with gold accent
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            text = "Premium Plans",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFfac826),
            textAlign = TextAlign.Center
        )

        // Weekly subscription card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(1.dp, Color(0xFFFBD38D), shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Premium",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Try Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD97706)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (weeklyPrice.isNotEmpty()) "$weeklyPrice/week" else "Loading...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFFFB9D0B)
                )

                Text(
                    text = "Weekly billing, cancel anytime",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                // Feature list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactFeatureItem(text = "No ads")
                    CompactFeatureItem(text = "Unlimited downloads")
                }

                // Button
                CommonButton(
                    text = "Subscribe Weekly",
                    textColor = Color.White,
                    backgroundColor = Color(0xFFFBAD0B),
                    onClick = onWeeklySubscribe,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                )
            }
        }

        // Monthly subscription card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(1.dp, Color(0xFF22C55E), shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monthly Premium",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SAVE 25%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (monthlyPrice.isNotEmpty()) "$monthlyPrice/month" else "Loading...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF16A34A)
                )

                Text(
                    text = "Monthly billing, cancel anytime",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                // Feature list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactFeatureItem(text = "All weekly features")
                    CompactFeatureItem(text = "25% savings")
                }

                // Button
                CommonButton(
                    text = "Subscribe Monthly",
                    textColor = Color.White,
                    backgroundColor = Color(0xFF16A34A),
                    onClick = onMonthlySubscribe,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                )
            }
        }

        // Add the new professional policy text component
        PrivacyPolicyText()

        // Cancel button
        CommonButton(
            text = stringResource(R.string.cancel),
            backgroundColor = Color(0xFF333333),
            textColor = Color.White,
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(46.dp)
        )
    }
}

@Composable
fun PrivacyPolicyText() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        // Styled Policy Text
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF222222)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(6.dp)
            ) {
                PolicyItem(
                    text = "Payment will be charged to your Google Play account upon confirmation." +
                            " Subscriptions automatically renew unless canceled at least 24 hours" +
                            " before the end of the current period. Your account will be charged" +
                            " for renewal within 24 hours prior to the end of the current period." +
                            " You can manage and cancel your subscriptions by going to your" +
                            " account settings on the Google Play store."
                )
            }
        }
    }
}

@Composable
private fun PolicyItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 2.dp,8.dp,2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .padding(top = 4.dp)
                .background(Color(0xFF888888), shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFFAAAAAA),
            textAlign = TextAlign.Justify,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun CompactFeatureItem(text: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(12.dp)
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = text,
                fontSize = 11.sp,
                color = Color.DarkGray
            )
        }
    }
}

// You'll need to update your CommonButton if it doesn't accept a modifier parameter
// Here's a sample modification if needed:
@Composable
fun CommonButton(
    text: String,
    textColor: Color = Color.Black,
    backgroundColor: Color = Color.LightGray,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF333333 // Black color in ARGB format
)
@Composable
fun SubscriptionOptionsPreview() {
    SubscriptionOptions(
        weeklyPrice = "$4.99",
        monthlyPrice = "$14.99",
        onWeeklySubscribe = {},
        onMonthlySubscribe = {},
        onCancel = {}
    )
}