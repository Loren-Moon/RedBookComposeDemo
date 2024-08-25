package com.loren.redbook.theme.view

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun CircleProgress(
    modifier: Modifier = Modifier,
    color: Color = Color.LightGray,
    progress: Float = 1f
) {
    Canvas(modifier = modifier.drawWithCache {
        val ringStrokeWidth = 3.dp.toPx()
        val radius = min(size.width, size.height) / 2 - ringStrokeWidth
        val topTopLeft = Offset(size.width / 2 - radius, size.height / 2 - radius)
        // 两个半圆弧形中间有一点间隔 取一个圆弧140度 左右各减少20度
        onDrawWithContent {
            val sweepAngle = 140f * progress
            // 绘制上半圆
            drawArc(
                color = color,
                startAngle = -160f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topTopLeft,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = ringStrokeWidth, cap = StrokeCap.Round)
            )
            // 绘制下半圆
            drawArc(
                color = color,
                startAngle = 20f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topTopLeft,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = ringStrokeWidth, cap = StrokeCap.Round)
            )
        }
    }) {}
}