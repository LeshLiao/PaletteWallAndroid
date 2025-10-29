package com.palettex.palettewall.view

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsPage(
    topOffset: Dp,
    bottomOffset: Dp,
) {
    val listState = rememberLazyListState()
    val isDarkModeEnabled = remember { mutableStateOf(true) }

    Scaffold(
        topBar = {},
        containerColor = Color.Black
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
                            .padding(top = topOffset, bottom = bottomOffset, start = 16.dp, end = 16.dp)
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
                                containerColor = Color(0xFF2C2C2E)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
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
                                        color = Color(0xFFFFD700)
                                    )
                                    Switch(
                                        checked = isDarkModeEnabled.value,
                                        onCheckedChange = { isDarkModeEnabled.value = it },
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
                                        .clickable { /* Handle contact click */ }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Contact the Developers",
                                        fontSize = 16.sp,
                                        color = Color(0xFFFFD700)
                                    )
                                }

                                Divider(
                                    color = Color(0xFF3A3A3C),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Rate Us On App Store
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { /* Handle rate us click */ }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Rate Us On App Store",
                                        fontSize = 16.sp,
                                        color = Color(0xFFFFD700)
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
                                containerColor = Color(0xFF2C2C2E)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                // Privacy Policy
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { /* Handle privacy policy click */ }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Link",
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Privacy Policy",
                                        fontSize = 16.sp,
                                        color = Color(0xFFFFD700)
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
                                        .clickable { /* Handle terms click */ }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Link",
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Terms of Use",
                                        fontSize = 16.sp,
                                        color = Color(0xFFFFD700)
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsPagePreview() {
    MaterialTheme {
        SettingsPage(1.dp, 1.dp)
    }
}