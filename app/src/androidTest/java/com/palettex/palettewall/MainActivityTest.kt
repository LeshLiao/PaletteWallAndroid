package com.palettex.palettewall

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppTitleOnLaunch() {
        // Wait for the app to initialize
        Thread.sleep(5000)

        // Verify that the app title "PaletteX" is displayed
        composeTestRule.onNodeWithText("PaletteX")
            .assertIsDisplayed()
            .assertExists()
    }

    @Test
    fun testPopularWallpaperClick() {
        // Wait for the app to initialize
        Thread.sleep(5000)

        // Click the first popular wallpaper card
        composeTestRule.onAllNodesWithTag("popular_wallpaper_card")
            .get(0)
            .assertIsDisplayed()
            .performClick()

        // Wait for the fullscreen view to load
        Thread.sleep(5000)

        // Verify the download button is visible
        composeTestRule.onNodeWithTag("download_button")
            .assertIsDisplayed()
            .assertIsEnabled()

        // Click the download button
        composeTestRule.onNodeWithTag("download_button")
            .performClick()

        // Wait for download to start
        Thread.sleep(2000)

        // Verify we're still in the fullscreen view
        composeTestRule.onNodeWithTag("download_button")
            .assertIsDisplayed()
    }
}