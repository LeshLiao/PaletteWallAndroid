package com.palettex.palettewall.view.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


@Composable
fun ImageSkeletonLoader(
    modifier: Modifier = Modifier
) {
    // Create an infinitely repeating animation
    val transition = rememberInfiniteTransition(label = "skeleton")

    // Animate the alpha value between 0.3f and 0.9f
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "skeleton"
    )

    // Create a gradient brush that moves with the animation
    val shimmerColors = listOf(
        Color.DarkGray.copy(alpha = 0.3f),
        Color.DarkGray.copy(alpha = 0.5f),
        Color.DarkGray.copy(alpha = 0.3f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200, translateAnim - 200),
        end = Offset(translateAnim, translateAnim)
    )

    // Draw the skeleton shape
    Box(
        modifier = modifier
            .clip(RectangleShape)
            .background(brush)
    )
}


// Style 1: Gradient Pulse Skeleton
@Composable
fun GradientPulseSkeletonLoader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "pulse")

    // Animate the alpha pulse
    val pulseAnim by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Create a radial gradient that pulses
    val brush = Brush.radialGradient(
        colors = listOf(
            Color(0xFF303030),
            Color(0xFF1A1A1A)
        ),
        center = Offset(0.5f, 0.5f),
        radius = 0.5f + pulseAnim
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
    )
}

// Style 2: Wave Effect Skeleton
@Composable
fun WaveSkeletonLoader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "wave")

    val waveAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val shimmerColors = listOf(
        Color(0xFF1F1F1F),
        Color(0xFF3D3D3D),
        Color(0xFF1F1F1F)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(waveAnim - 200, 0f),
        end = Offset(waveAnim, 600f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
    )
}

// Style 3: Fade In/Out Skeleton
@Composable
fun FadeSkeletonLoader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "fade")

    val alphaAnim by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fade"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.DarkGray)
            .alpha(alphaAnim)
    )
}

// Style 4: Dots Loading Skeleton
@Composable
fun DotsSkeletonLoader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "dots")

    val scale1 by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val scale2 by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val scale3 by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .graphicsLayer(scaleX = scale1, scaleY = scale1)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .graphicsLayer(scaleX = scale2, scaleY = scale2)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .graphicsLayer(scaleX = scale3, scaleY = scale3)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
        }
    }
}

// Style 5: Dark Shimmer Skeleton (More subtle)
@Composable
fun DarkShimmerSkeletonLoader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "darkShimmer")

    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "darkShimmer"
    )

    val shimmerColors = listOf(
        Color(0xFF0A0A0A),
        Color(0xFF1D1D1D),
        Color(0xFF0A0A0A),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 500, translateAnim - 500),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
    )
}

// Style 6: Instagram-like Skeleton
@Composable
fun InstagramSkeletonLoader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "instagram")

    val alphaAnim by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "instagram"
    )

    Surface(
        modifier = modifier
            .alpha(alphaAnim),
        color = Color(0xFF262626), // Instagram-like dark background
        shape = RoundedCornerShape(4.dp),
        tonalElevation = 2.dp
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}