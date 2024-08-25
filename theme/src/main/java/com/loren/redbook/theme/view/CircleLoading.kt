package com.loren.redbook.theme.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleLoading(
    modifier: Modifier = Modifier,
    loadingSize: Dp = 28.dp,
    loadingColor: Color = Color.LightGray
) {
    val durationMillis = 500
    val progress by rememberInfiniteTransition("CircleLoadingProgress")
        .animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "HeaderProgress"
        )
    val rotate by rememberInfiniteTransition("CircleLoadingRotate")
        .animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "HeaderRotate"
        )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircleProgress(
            modifier = Modifier
                .size(loadingSize)
                .rotate(rotate), color = loadingColor, progress = progress
        )
    }
}