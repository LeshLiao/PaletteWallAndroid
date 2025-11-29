package com.palettex.palettewall

import com.palettex.palettewall.domain.utils.handleImageInfo
import org.junit.Assert.assertEquals
import org.junit.Test

class ImagesUtilsTest {

    @Test
    fun `handleImageInfo returns correct string with both color and catalog tags`() {
        val name = "Sunset Wallpaper"
        val tags = listOf("#FF0000%50", "#00FF00", "Nature", "Landscape")

        val result = handleImageInfo(name, tags)

        val expected = """
            Sunset Wallpaper
            Colors: #FF0000, #00FF00
            Catalog: Nature, Landscape
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun `handleImageInfo returns only name when no tags`() {
        val result = handleImageInfo("Wallpaper", emptyList())

        assertEquals("Wallpaper", result)
    }

    @Test
    fun `handleImageInfo handles null name`() {
        val tags = listOf("#123456", "Abstract")

        val result = handleImageInfo(null, tags)

        val expected = """
            Colors: #123456
            Catalog: Abstract
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun `handleImageInfo removes duplicate tags`() {
        val name = "Test Image"
        val tags = listOf("#AAAAAA", "#AAAAAA", "Nature", "Nature")

        val result = handleImageInfo(name, tags)

        val expected = """
            Test Image
            Colors: #AAAAAA
            Catalog: Nature
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun `handleImageInfo strips color percent values`() {
        val name = "Color Test"
        val tags = listOf("#FF0000%60", "#00FF00%90")

        val result = handleImageInfo(name, tags)

        val expected = """
            Color Test
            Colors: #FF0000, #00FF00
        """.trimIndent()

        assertEquals(expected, result)
    }
}
