package com.palettex.palettewall.utils

fun handleImageInfo(name: String?, tags: List<String>): String {
    val colorTags = tags
        .filter { it.contains("#") }
        .map { it.substringBefore("%") }
        .distinct()
        .joinToString(", ")

    val catalogTags = tags
        .filter { !it.contains("#") }
        .distinct()
        .joinToString(", ")

    val builder = StringBuilder()

    if (!name.isNullOrEmpty()) {
        builder.append(name)
    }

    if (colorTags.isNotEmpty()) {
        if (builder.isNotEmpty()) builder.append("\n")
        builder.append("Colors: $colorTags")
    }

    if (catalogTags.isNotEmpty()) {
        builder.append("\nCatalog: $catalogTags")
    }

    return builder.toString()
}
