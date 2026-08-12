package com.sacmaib80.sakuraq.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sacmaib80.sakuraq.app.R
import kotlinx.coroutines.delay

/**
 * มาสคอตหมา — ถ้ามีเฟรมวิดีโอ (fox_01..fox_NN แกะจาก fox-idle.webm) จะเล่นเป็นวิดีโอ 8fps
 * ไม่มีก็ fallback ภาพนิ่ง + bob/breath/sway. glow ม่วงอุ่นๆ อยู่ข้างหลังเสมอ
 */
@Composable
fun Mascot(
    modifier: Modifier = Modifier,
    size: Dp = 190.dp,
    wave: Boolean = false,
) {
    val ctx = LocalContext.current
    // หาเฟรมวิดีโอที่ pack ไว้ (ชื่อ fox_01, fox_02, …) — เจอเท่าไหร่เล่นเท่านั้น
    val frames = remember {
        val list = mutableListOf<Int>()
        var i = 1
        while (true) {
            val id = ctx.resources.getIdentifier(String.format("fox_%02d", i), "drawable", ctx.packageName)
            if (id == 0) break
            list.add(id)
            i++
        }
        list
    }

    var frame by remember { mutableIntStateOf(0) }
    if (frames.size > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(142L) // 32 เฟรม / 4.561s = ความเร็วเท่าวิดีโอต้นฉบับ
                frame = (frame + 1) % frames.size
            }
        }
    }

    val t = rememberInfiniteTransition(label = "mascot")
    val glow by t.animateFloat(
        initialValue = 0.22f, targetValue = 0.34f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow",
    )
    val bob by t.animateFloat(
        initialValue = 0f, targetValue = if (frames.isNotEmpty()) -3f else -9f,
        animationSpec = infiniteRepeatable(tween(2100, easing = androidx.compose.animation.core.FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob",
    )
    val breath by t.animateFloat(
        initialValue = 1f, targetValue = if (frames.isNotEmpty()) 1.008f else 1.03f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "breath",
    )
    val sway by t.animateFloat(
        initialValue = if (wave && frames.isEmpty()) -2.6f else -1.2f,
        targetValue = if (wave && frames.isEmpty()) 2.6f else 1.2f,
        animationSpec = infiniteRepeatable(tween(if (wave && frames.isEmpty()) 1400 else 3600, easing = androidx.compose.animation.core.FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sway",
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF7C7DE8).copy(alpha = glow), Color.Transparent),
                    center = Offset(this.size.width / 2, this.size.height * 0.58f),
                    radius = this.size.minDimension * 0.55f,
                ),
                radius = this.size.minDimension * 0.55f,
                center = Offset(this.size.width / 2, this.size.height * 0.58f),
            )
        }
        val painterRes = when {
            frames.isNotEmpty() -> frames[frame]
            wave -> R.drawable.mascot_wave
            else -> R.drawable.mascot
        }
        Image(
            painter = painterResource(painterRes),
            contentDescription = null,
            modifier = Modifier
                .size(size * 0.88f)
                .graphicsLayer {
                    translationY = bob * density
                    scaleX = breath
                    scaleY = breath
                    rotationZ = if (frames.isNotEmpty()) 0f else sway
                },
        )
    }
}
