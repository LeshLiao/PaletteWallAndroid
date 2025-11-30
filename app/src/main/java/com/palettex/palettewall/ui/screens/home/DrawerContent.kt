package com.palettex.palettewall.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.palettex.palettewall.ui.components.utility.throttleClick
import com.palettex.palettewall.ui.screens.home.BillingViewModel
import com.palettex.palettewall.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: HomeViewModel,
    billingViewModel: BillingViewModel,
    onClickCatalog: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val versionName by viewModel.versionName.collectAsState()
    val isPremium by billingViewModel.isPremium.collectAsState()

    DrawerContentUI(
        versionName = versionName,
        isPremium = isPremium,
        onAboutUsClick = {
            navController.navigate("AboutUs")
            scope.launch { drawerState.close() }
        },
        onVersionClick = {
            scope.launch { drawerState.close() }
        },
        onCatalogClick = { catalog ->
            scope.launch { drawerState.close() }
            onClickCatalog(catalog)
        },
        onCloseClick = {
            scope.launch { drawerState.close() }
        }
    )
}

@Composable
fun DrawerContentUI(
    versionName: String,
    isPremium: Boolean,
    onAboutUsClick: () -> Unit = {},
    onVersionClick: () -> Unit = {},
    onCatalogClick: (String) -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.padding(start = 8.dp).verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = "CATEGORIES",
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        val catalogs = listOf(
            "flowers",
            "mountains",
            "forest",
            "stars",
            "sunset",
            "sky",
            "clouds",
            "flower",
            "abstract",
            "water",
            "night",
            "architecture",
            "road",
            "mountain",
            "leaves"
        )

        catalogs.forEach { catalog ->
            CatalogItem(catalog) {
                onCatalogClick(catalog)
            }
        }

        Text(
            text = "About Us",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onAboutUsClick() }
        )

        if (isPremium) {
            Text(
                text = "✨ You are a Premium Member",
                color = Color(0xfffbad0b),
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Close (X)",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(16.dp)
                .width(100.dp)
                .clickable { onCloseClick() }
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun CatalogItem(
    name: String,
    onClick: (String) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = name.replaceFirstChar { it.uppercase() },
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 28.dp, vertical = 10.dp)
                .throttleClick {
                    onClick(name)
                }
        )
    }
}

@Preview(
    showBackground = true,
    name = "Free User",
    widthDp = 300,
    //heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DrawerContentPreviewFree() {
    MaterialTheme {
        DrawerContentUI(
            versionName = "1.0.0",
            isPremium = true
        )
    }
}