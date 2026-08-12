package com.sacmaib80.sakuraq.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * กราฟเส้นโค้งลื่น + เติมสีไล่เฉดใต้เส้น + จุดวันพีค — พอร์ตลุคกราฟหน้า History ของแอปจริง
 * เส้นวาดตัวเองจากซ้ายไปขวาตอนเข้าฉาก (PathMeasure)
 */
@Composable
fun BezierChart(
    values: List<Double>,
    modifier: Modifier = Modifier,
    maxValue: Double = 100.0,
    lineColor: Color = Color(0xFF5B6AF0),
    fillTop: Color = Color(0x665B6AF0),
    fillBottom: Color = Color(0x055B6AF0),
    goalValue: Double? = null,
    goalColor: Color = Color(0x33808BB8),
    animKey: Any? = null,
) {
    if (values.isEmpty()) return
    val progress = remember { Animatable(0f) }
    LaunchedEffect(animKey ?: values) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(1150, easing = FastOutSlowInEasing))
    }

    // จุดพีค = ค่าสูงกว่าค่าเฉลี่ย×1.45 และเป็น local max
    val avg = values.average()
    val spikes = values.indices.filter { i ->
        val v = values[i]
        v > 0 && v >= avg * 1.45 &&
            (i == 0 || values[i - 1] <= v) && (i == values.lastIndex || values[i + 1] <= v)
    }.toSet()

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val maxV = maxOf(maxValue, values.max(), 1.0)
        val padTop = h * 0.10f
        val usable = h - padTop - 6f

        fun px(i: Int) = if (values.size == 1) w / 2 else w * i / (values.size - 1)
        fun py(v: Double) = padTop + usable * (1f - (v / maxV).toFloat())

        val pts = values.mapIndexed { i, v -> Offset(px(i), py(v)) }

        // เส้นเป้าหมาย 100 (จางๆ dashed ด้วย segment สั้น)
        if (goalValue != null && goalValue <= maxV) {
            val gy = py(goalValue)
            var x = 0f
            while (x < w) {
                drawLine(goalColor, Offset(x, gy), Offset((x + 10f).coerceAtMost(w), gy), 2.4f, StrokeCap.Round)
                x += 18f
            }
        }

        // สร้าง path โค้งแบบ Catmull-Rom → cubic bezier
        val line = Path()
        line.moveTo(pts[0].x, pts[0].y)
        if (pts.size == 1) {
            line.lineTo(pts[0].x + 1, pts[0].y)
        } else {
            for (i in 0 until pts.size - 1) {
                val p0 = pts.getOrElse(i - 1) { pts[i] }
                val p1 = pts[i]
                val p2 = pts[i + 1]
                val p3 = pts.getOrElse(i + 2) { p2 }
                val c1 = Offset(p1.x + (p2.x - p0.x) / 6f, (p1.y + (p2.y - p0.y) / 6f).coerceIn(padTop * 0.4f, h))
                val c2 = Offset(p2.x - (p3.x - p1.x) / 6f, (p2.y - (p3.y - p1.y) / 6f).coerceIn(padTop * 0.4f, h))
                line.cubicTo(c1.x, c1.y, c2.x, c2.y, p2.x, p2.y)
            }
        }

        val t = progress.value

        // พื้นที่ใต้เส้น
        val fill = Path().apply {
            addPath(line)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            fill,
            Brush.verticalGradient(listOf(fillTop.copy(alpha = fillTop.alpha * t), fillBottom)),
        )

        // เส้น (วาดบางส่วนตาม progress)
        val pm = PathMeasure()
        pm.setPath(line, false)
        val partial = Path()
        pm.getSegment(0f, pm.length * t, partial, true)
        // เงาเรืองใต้เส้น
        drawPath(partial, lineColor.copy(alpha = 0.28f), style = Stroke(9f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(partial, lineColor, style = Stroke(4f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // จุดพีค
        pts.forEachIndexed { i, pt ->
            val reveal = if (values.size == 1) 1f else (t * (values.size - 1) - i).coerceIn(0f, 1f)
            if (reveal <= 0f) return@forEachIndexed
            if (i in spikes) {
                drawCircle(Color.White, 6.5f * reveal, pt)
                drawCircle(lineColor, 4.2f * reveal, pt)
            }
        }
        // จุดปลายเส้น (วันล่าสุด)
        if (t > 0.02f) {
            val endPos = pm.getPosition(pm.length * t)
            drawCircle(Color.White, 7f, endPos)
            drawCircle(lineColor, 4.6f, endPos)
        }
    }
}
