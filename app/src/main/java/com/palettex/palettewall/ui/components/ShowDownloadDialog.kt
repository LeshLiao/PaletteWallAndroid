package com.palettex.palettewall.ui.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ShowDownloadDialog(msg: String, onDismiss: () -> Unit) {
    AlertDialog(
        containerColor = Color.DarkGray,
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Download",
                color = Color.White // Set title text color to white
            )
        },
        text = {
            Text(
                text = msg,
                color = Color.White // Set content text color to white
            )
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("OK")
            }
        },

    )
}
