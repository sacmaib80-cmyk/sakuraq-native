package com.sacmaib80.sakuraq.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sacmaib80.sakuraq.app.domain.fmtScore

/**
 * ตัวเลขวิ่งขึ้น 0 → target แล้วส่ง string ที่ format แล้วให้ render
 */
@Composable
fun CountUpValue(
    target: Double,
    durationMs: Int = 1000,
    content: @Composable (String) -> Unit,
) {
    var display by remember { mutableFloatStateOf(0f) }
    val anim = remember { Animatable(0f) }
    LaunchedEffect(target) {
        anim.snapTo(display)
        anim.animateTo(target.toFloat(), tween(durationMs, easing = FastOutSlowInEasing)) {
            display = value
        }
    }
    content(fmtScore(display.toDouble()))
}
