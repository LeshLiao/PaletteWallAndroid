package com.palettex.palettewall.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.palettex.palettewall.R

/*
  Download the font from Google Fonts
  https://fonts.google.com/specimen/Philosopher
  Place the .ttf files in res/font/ directory
 */

val PhilosopherFontFamily = FontFamily(
    Font(R.font.philosopher_regular, FontWeight.Normal),
    Font(R.font.philosopher_bold, FontWeight.Bold),
    Font(R.font.philosopher_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.philosopher_bold_italic, FontWeight.Bold, FontStyle.Italic)
)