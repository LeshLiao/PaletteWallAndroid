package com.palettex.palettewall

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppTitleOnLaunch() {
        // Wait for the app to initialize with a more reliable approach
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("PaletteX")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Verify that the app title "PaletteX" is displayed
        composeTestRule.onNodeWithText("PaletteX")
            .assertIsDisplayed()
            .assertExists()
    }

    @Test
    fun testPopularWallpaperClick() {
        // Wait for wallpaper cards to load
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithTag("wallpaper_card")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Click the first popular wallpaper card
        composeTestRule.onAllNodesWithTag("wallpaper_card")
            .onFirst()
            .assertIsDisplayed()
            .performClick()

        // Wait for the fullscreen view to load
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithTag("download_button")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Verify the download button is visible
        composeTestRule.onNodeWithTag("download_button")
            .assertIsDisplayed()
            .assertIsEnabled()

        // Click the download button
        composeTestRule.onNodeWithTag("download_button")
            .performClick()

        // Wait a bit for download to process
        composeTestRule.waitForIdle()

        // Verify we're still in the fullscreen view
        composeTestRule.onNodeWithTag("download_button")
            .assertExists()
    }
}