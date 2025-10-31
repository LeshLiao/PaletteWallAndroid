package com.palettex.palettewall.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palettex.palettewall.view.utility.throttleClick

@Composable
fun CommonButton(
    text: String,
    textColor: Color = Color.Blue,
    backgroundColor: Color = Color.White,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .throttleClick { onClick() }
                .fillMaxWidth()
                .height(46.dp)
                .padding(horizontal = 10.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.W500,
                fontSize = 16.sp,
            )
        }
    }
}
