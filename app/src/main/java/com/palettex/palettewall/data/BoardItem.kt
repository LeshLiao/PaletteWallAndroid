package com.palettex.palettewall.data

data class BoardItem(
    val id: Int,
    val key: String,
    val title: String,
    val action: String,
    val value: String,
    val photoUrl: String,
    val ratioWidth: Int,
    val ratioHeight: Int,
)