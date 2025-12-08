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
    fun testStartApps() {
        // Wait for app initialization
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("PaletteX")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun testScrollingInWallpaperList() {
        // Wait for app initialization
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("PaletteX")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitForIdle()

        // Wait for content to load
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithTag("wallpaper_card")
                .fetchSemanticsNodes()
                .size >= 3
        }

        composeTestRule.waitForIdle()


        for (i in 1..10) {
            composeTestRule.onNodeWithTag("wallpaper_list")
                .performScrollToIndex(i)

            composeTestRule.waitForIdle()
            Thread.sleep(500)
        }
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

        // Wait for the "Go Premium" button to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText(composeTestRule.activity.getString(R.string.show_ad_free_download))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        Thread.sleep(2000)
    }

    @Test
    fun testSharingWallpaperClick() {
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

        // Click the download button
        composeTestRule.onNodeWithTag("download_button")
            .performClick()

        // Wait a bit for download to process
        composeTestRule.waitForIdle()

        // Wait for the sharing button to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("testTag_sharing")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onAllNodesWithTag("testTag_sharing")
            .onFirst()
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(2000)
    }

    @Test
    fun testColorPicker() {
        // Wait for app initialization
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("PaletteX")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitForIdle()

        Thread.sleep(3000)

        composeTestRule.onNodeWithTag("testTag_Carousel")
            .performClick()

        composeTestRule.waitForIdle()

        Thread.sleep(3000)

        // filter red color
        composeTestRule.onNodeWithTag("testTag_colorBox_0xFFFF0000")
            .performClick()
        composeTestRule.waitForIdle()

        Thread.sleep(3000)

        composeTestRule.onNodeWithTag("testTag_colorBox_0xFFFF0000")
            .performClick()
        composeTestRule.waitForIdle()

        // filter Green color
        composeTestRule.onNodeWithTag("testTag_colorBox_0xFF008000")
            .performClick()
        composeTestRule.waitForIdle()

        Thread.sleep(3000)

        composeTestRule.onNodeWithTag("testTag_colorBox_0xFF008000")
            .performClick()
        composeTestRule.waitForIdle()

        // filter Blue color
        composeTestRule.onNodeWithTag("testTag_colorBox_0xFF0000CD")
            .performClick()
        composeTestRule.waitForIdle()

        Thread.sleep(3000)

        composeTestRule.onNodeWithTag("testTag_colorBox_0xFF0000CD")
            .performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun testDarkTheme() {
        // Wait for app initialization
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("PaletteX")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("testTag_Settings")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("testTag_dark_theme_switch")
            .performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("testTag_Home")
            .performClick()
        composeTestRule.waitForIdle()

        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("testTag_Settings")
            .performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("testTag_dark_theme_switch")
            .performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("testTag_dark_theme_switch")
            .performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("testTag_dark_theme_switch")
            .performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)
    }
}