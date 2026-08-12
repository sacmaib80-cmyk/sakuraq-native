package com.sacmaib80.sakuraq.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * วงแหวนนับถอยหลัง Canvas — gradient ตามหมวด, glide นุ่มระหว่าง tick 250ms
 * progress: 0..1 (ส่วนที่เหลือ)
 */
@Composable
fun ProgressRing(
    progress: Float,
    gradStart: Color,
    gradEnd: Color,
    modifier: Modifier = Modifier,
    size: Dp = 286.dp,
    stroke: Dp = 18.dp,
    trackColor: Color = Color(0x14555F9E),
    glow: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(300, easing = LinearEasing),
        label = "ringProgress",
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            val inset = sw / 2
            val arcSize = Size(this.size.width - sw, this.size.height - sw)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = trackColor,
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize,
                style = Stroke(sw, cap = StrokeCap.Round),
            )
            if (animated > 0.004f) {
                // หมุน -90° ให้เริ่มที่ 12 นาฬิกา และ sweepGradient เนียนตรงหัวท้าย
                if (glow) {
                    rotate(-90f) {
                        drawArc(
                            color = gradEnd.copy(alpha = 0.30f),
                            startAngle = 0f,
                            sweepAngle = 360f * animated,
                            useCenter = false,
                            topLeft = topLeft, size = arcSize,
                            style = Stroke(sw * 2.1f, cap = StrokeCap.Round),
                        )
                    }
                }
                rotate(-90f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            0f to gradStart,
                            animated.coerceAtLeast(0.05f) to gradEnd,
                            1f to gradStart,
                        ),
                        startAngle = 0f,
                        sweepAngle = 360f * animated,
                        useCenter = false,
                        topLeft = topLeft, size = arcSize,
                        style = Stroke(sw, cap = StrokeCap.Round),
                    )
                }
            }
        }
        content()
    }
}
