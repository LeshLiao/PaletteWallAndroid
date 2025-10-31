package com.palettex.palettewall.view

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@Composable
fun SettingsPage(
    topOffset: Dp,
    bottomOffset: Dp,
    isDarkModeEnabled: Boolean,
    isPremium: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val paletteXUrl = "https://play.google.com/store/apps/details?id=com.palettex.palettewall"

    Scaffold(
        topBar = {},
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val test = paddingValues
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = topOffset,
                                bottom = bottomOffset,
                                start = 16.dp,
                                end = 16.dp
                            )
                    ) {
                        // GENERAL Section
                        Text(
                            text = "GENERAL",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                Row() {
                                    if (isPremium) {
                                        Text(
                                            text = "✨ You are a Premium Member",
                                            color = Color(0xfffbad0b),
                                            fontWeight = FontWeight.W600,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                                        )
                                    }
                                }

                                // Dark Mode Toggle
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Dark Mode",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Switch(
                                        checked = isDarkModeEnabled,
                                        onCheckedChange = onDarkModeToggle,
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Color(0xFF34C759)
                                        )
                                    )
                                }

                                Divider(
                                    color = Color(0xFF3A3A3C),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Contact the Developers
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = "mailto:service@palettex.ca?subject=${
                                                    Uri.encode("PaletteWall Service")
                                                }".toUri()
                                            }
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Log.e("SettingsPage", "No email app found", e)
                                            }
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Contact the Developers",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }

                                Divider(
                                    color = Color(0xFF3A3A3C),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Rate Us On Play Store
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                paletteXUrl.toUri()
                                            )
                                            context.startActivity(intent)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Rate Us On Play Store",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }

                        // LEGAL Section
                        Text(
                            text = "LEGAL",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                // Privacy Policy
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                "https://www.palettex.ca/policy".toUri()
                                            )
                                            context.startActivity(intent)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Link",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Privacy Policy",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }

                                Divider(
                                    color = Color(0xFF3A3A3C),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Terms of Use
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                "https://www.palettex.ca/policy".toUri()
                                            )
                                            context.startActivity(intent)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Link",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Terms of Use",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(160.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark Theme")
@Composable
fun SettingsPagePreviewDark() {
    MaterialTheme {
        SettingsPage(
            topOffset = 1.dp,
            bottomOffset = 1.dp,
            isDarkModeEnabled = true,
            isPremium = true,
            onDarkModeToggle = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Light Theme")
@Composable
fun SettingsPagePreviewLight() {
    MaterialTheme {
        SettingsPage(
            topOffset = 1.dp,
            bottomOffset = 1.dp,
            isDarkModeEnabled = false,
            isPremium = false,
            onDarkModeToggle = {}
        )
    }
}