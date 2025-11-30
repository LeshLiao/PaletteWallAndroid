package com.palettex.palettewall.ui.components.utility

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.throttleClick(
    throttleInterval: Long = 1000L,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > throttleInterval) {
                lastClickTime = currentTime
                onClick()
            }
        }
    )
}