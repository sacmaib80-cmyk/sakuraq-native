package com.sacmaib80.sakuraq.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val CONFETTI_COLORS = listOf(
    Color(0xFF5B6AF0), Color(0xFFA78BFA), Color(0xFFF472B6),
    Color(0xFF34D399), Color(0xFFFBBF24), Color(0xFF60A5FA),
)

private class Confetto(
    val vx: Float, val vy: Float,
    val color: Color,
    val w: Float, val h: Float,
    val spin: Float,
    val lifeMs: Float,
    val delayMs: Float,
)

private class Petal(
    val x0: Float,
    val fallSpeed: Float,
    val swayAmp: Float,
    val swayFreq: Float,
    val spin: Float,
    val sizeDp: Float,
    val delayMs: Float,
)

/**
 * กลีบซากุระลอยเอื่อยๆ ทั่วหน้า Home (ambient — วนลูปตลอด, จำนวนน้อย ไม่กวนสายตา)
 */
@Composable
fun AmbientPetals(modifier: Modifier = Modifier, count: Int = 7) {
    val petals = remember {
        List(count) {
            Petal(
                x0 = Random.nextFloat(),
                fallSpeed = 0.028f + Random.nextFloat() * 0.030f,
                swayAmp = 0.03f + Random.nextFloat() * 0.045f,
                swayFreq = 0.25f + Random.nextFloat() * 0.35f,
                spin = (Random.nextFloat() - 0.5f) * 120f,
                sizeDp = 7f + Random.nextFloat() * 6f,
                delayMs = Random.nextFloat() * 14000f,
            )
        }
    }
    var elapsedMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> elapsedMs = (now - start) / 1_000_000 }
        }
    }
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        petals.forEach { pt ->
            val t = elapsedMs + pt.delayMs
            val sec = t / 1000f
            // วนลูป: ตำแหน่ง y ไหลตาม fmod ของช่วง -0.12..1.12
            val span = 1.24f
            val yFrac = -0.12f + ((pt.fallSpeed * sec) % span + span) % span
            val x = (pt.x0 + sin(sec * pt.swayFreq * 2f * Math.PI.toFloat()) * pt.swayAmp) * w
            val y = yFrac * h
            val s = pt.sizeDp * density
            val alpha = when {
                yFrac < 0f -> 0f
                yFrac < 0.08f -> yFrac / 0.08f
                yFrac > 0.92f -> ((1.12f - yFrac) / 0.2f).coerceIn(0f, 1f)
                else -> 1f
            } * 0.55f
            rotate(degrees = pt.spin * sec, pivot = Offset(x, y)) {
                drawOval(
                    color = Color(0xFFF6B7D2).copy(alpha = alpha),
                    topLeft = Offset(x - s / 2, y - s * 0.32f),
                    size = Size(s, s * 0.64f),
                )
            }
        }
    }
}

/**
 * เอฟเฟกต์ฉลอง: confetti พุ่งกระจายจากกลางจอ + กลีบซากุระร่วงแกว่ง
 * Canvas เดียว ขับเวลาด้วย withFrameNanos (60fps)
 */
@Composable
fun CelebrationParticles(modifier: Modifier = Modifier) {
    val confetti = remember {
        List(28) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = 0.25f + Random.nextFloat() * 0.55f
            Confetto(
                vx = cos(angle) * speed,
                vy = sin(angle) * speed * 0.85f - 0.25f,
                color = CONFETTI_COLORS[it % CONFETTI_COLORS.size],
                w = 3.5f + Random.nextFloat() * 3.5f,
                h = 2.2f + Random.nextFloat() * 2.6f,
                spin = (Random.nextFloat() - 0.5f) * 1080f,
                lifeMs = 1400f + Random.nextFloat() * 600f,
                delayMs = Random.nextFloat() * 120f,
            )
        }
    }
    val petals = remember {
        List(12) {
            Petal(
                x0 = Random.nextFloat(),
                fallSpeed = 0.12f + Random.nextFloat() * 0.10f,
                swayAmp = 0.04f + Random.nextFloat() * 0.05f,
                swayFreq = 0.8f + Random.nextFloat() * 0.8f,
                spin = (Random.nextFloat() - 0.5f) * 260f,
                sizeDp = 9f + Random.nextFloat() * 7f,
                delayMs = Random.nextFloat() * 900f,
            )
        }
    }

    var elapsedMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> elapsedMs = (now - start) / 1_000_000 }
        }
    }

    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // confetti burst จากจุดกลางค่อนบน
        confetti.forEach { c ->
            val t = elapsedMs - c.delayMs
            if (t < 0 || t > c.lifeMs) return@forEach
            val p = t / c.lifeMs
            val ease = 1f - (1f - p) * (1f - p)
            val gravity = 0.55f * p * p
            val x = (0.5f + c.vx * ease * 0.6f) * w
            val y = (0.42f + c.vy * ease * 0.6f + gravity) * h
            val alpha = (if (p > 0.7f) (1f - p) / 0.3f else 1f).coerceIn(0f, 1f)
            val cw = c.w * density
            val ch = c.h * density
            rotate(degrees = c.spin * p, pivot = Offset(x, y)) {
                drawRect(
                    color = c.color.copy(alpha = alpha),
                    topLeft = Offset(x - cw / 2, y - ch / 2),
                    size = Size(cw, ch),
                )
            }
        }

        // กลีบซากุระร่วง
        petals.forEach { pt ->
            val t = elapsedMs - pt.delayMs
            if (t < 0) return@forEach
            val sec = t / 1000f
            val yFrac = -0.08f + pt.fallSpeed * sec
            if (yFrac > 1.1f) return@forEach
            val x = (pt.x0 + sin(sec * pt.swayFreq * 2f * Math.PI.toFloat()) * pt.swayAmp) * w
            val y = yFrac * h
            val s = pt.sizeDp * density
            rotate(degrees = pt.spin * sec, pivot = Offset(x, y)) {
                drawOval(
                    color = Color(0xFFF9A8D4).copy(alpha = 0.85f),
                    topLeft = Offset(x - s / 2, y - s * 0.32f),
                    size = Size(s, s * 0.64f),
                )
            }
        }
    }
}
