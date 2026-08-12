package com.sacmaib80.sakuraq.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ไอคอนวาดเองทั้งชุด (ไม่มี emoji — ตามธรรมเนียมแอปจริงที่วาด CSS icon เอง)
 * ทุกตัววาดใน viewport 24×24 แล้วสเกลตามขนาดจริง
 */
enum class SqIconKind {
    Home, Sun, Chart, Calendar, Menu,
    Book, Briefcase, Dumbbell, Heart,
    Dice, Flame, Paw, Star, Sparkle, Target,
    Play, Pause, Stop, Plus, Check, Cross,
    ChevronRight, ChevronLeft, Trash, Globe, Bell, Battery, Pencil, Clock, Report, TrendUp, UserCheck,
}

@Composable
fun SqIcon(
    kind: SqIconKind,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = Color(0xFF1F2340),
    stroke: Float = 2f,
) {
    Canvas(modifier.size(size)) {
        val u = this.size.minDimension / 24f
        val sw = stroke * u
        drawIcon(kind, u, sw, tint)
    }
}

private fun DrawScope.p(x: Float, y: Float, u: Float) = Offset(x * u, y * u)

private fun DrawScope.strokePath(path: Path, tint: Color, sw: Float) =
    drawPath(path, tint, style = Stroke(sw, cap = StrokeCap.Round, join = StrokeJoin.Round))

private fun DrawScope.drawIcon(kind: SqIconKind, u: Float, sw: Float, tint: Color) {
    fun o(x: Float, y: Float) = p(x, y, u)
    val stroke = Stroke(sw, cap = StrokeCap.Round, join = StrokeJoin.Round)

    when (kind) {
        SqIconKind.Home -> {
            val body = Path().apply {
                moveTo(5.2f * u, 11.4f * u)
                lineTo(5.2f * u, 18.4f * u)
                quadraticBezierTo(5.2f * u, 20f * u, 6.8f * u, 20f * u)
                lineTo(17.2f * u, 20f * u)
                quadraticBezierTo(18.8f * u, 20f * u, 18.8f * u, 18.4f * u)
                lineTo(18.8f * u, 11.4f * u)
            }
            strokePath(body, tint, sw)
            val roof = Path().apply {
                moveTo(3.6f * u, 12.2f * u)
                lineTo(12f * u, 4.6f * u)
                lineTo(20.4f * u, 12.2f * u)
            }
            strokePath(roof, tint, sw)
            val door = Path().apply {
                moveTo(9.9f * u, 20f * u)
                lineTo(9.9f * u, 15.4f * u)
                quadraticBezierTo(9.9f * u, 14.2f * u, 11.1f * u, 14.2f * u)
                lineTo(12.9f * u, 14.2f * u)
                quadraticBezierTo(14.1f * u, 14.2f * u, 14.1f * u, 15.4f * u)
                lineTo(14.1f * u, 20f * u)
            }
            strokePath(door, tint, sw * 0.9f)
        }

        SqIconKind.Sun -> {
            drawCircle(tint, 3.9f * u, o(12f, 12f), style = stroke)
            for (i in 0 until 8) {
                val a = Math.toRadians(i * 45.0)
                val c = kotlin.math.cos(a).toFloat()
                val s = kotlin.math.sin(a).toFloat()
                drawLine(tint, o(12f + c * 6.6f, 12f + s * 6.6f), o(12f + c * 9.2f, 12f + s * 9.2f), sw, StrokeCap.Round)
            }
        }

        SqIconKind.Chart -> {
            val line = Path().apply {
                moveTo(4f * u, 16.6f * u)
                lineTo(9.4f * u, 10.8f * u)
                lineTo(13.4f * u, 14f * u)
                lineTo(20f * u, 6.6f * u)
            }
            strokePath(line, tint, sw)
            drawCircle(tint, 1.7f * u, o(20f, 6.6f))
            drawCircle(tint, 1.35f * u, o(4f, 16.6f))
        }

        SqIconKind.Calendar -> {
            drawRoundRect(tint, o(3.8f, 5.6f), Size(16.4f * u, 14.6f * u), CornerRadius(3f * u), stroke)
            drawLine(tint, o(8.3f, 3.4f), o(8.3f, 7.6f), sw, StrokeCap.Round)
            drawLine(tint, o(15.7f, 3.4f), o(15.7f, 7.6f), sw, StrokeCap.Round)
            drawLine(tint, o(3.8f, 10.2f), o(20.2f, 10.2f), sw * 0.9f, StrokeCap.Round)
            drawCircle(tint, 1.25f * u, o(8.6f, 14.4f))
            drawCircle(tint, 1.25f * u, o(13.2f, 14.4f))
        }

        SqIconKind.Menu -> {
            drawLine(tint, o(4.6f, 7.2f), o(19.4f, 7.2f), sw, StrokeCap.Round)
            drawLine(tint, o(4.6f, 12f), o(19.4f, 12f), sw, StrokeCap.Round)
            drawLine(tint, o(4.6f, 16.8f), o(14.6f, 16.8f), sw, StrokeCap.Round)
        }

        SqIconKind.Book -> {
            val left = Path().apply {
                moveTo(12f * u, 6.4f * u)
                cubicTo(10.2f * u, 4.6f * u, 6.8f * u, 4.4f * u, 4.6f * u, 5.4f * u)
                lineTo(4.6f * u, 17.6f * u)
                cubicTo(6.8f * u, 16.6f * u, 10.2f * u, 16.8f * u, 12f * u, 18.6f * u)
            }
            val right = Path().apply {
                moveTo(12f * u, 6.4f * u)
                cubicTo(13.8f * u, 4.6f * u, 17.2f * u, 4.4f * u, 19.4f * u, 5.4f * u)
                lineTo(19.4f * u, 17.6f * u)
                cubicTo(17.2f * u, 16.6f * u, 13.8f * u, 16.8f * u, 12f * u, 18.6f * u)
            }
            strokePath(left, tint, sw)
            strokePath(right, tint, sw)
            drawLine(tint, o(12f, 6.6f), o(12f, 18.4f), sw * 0.8f, StrokeCap.Round)
        }

        SqIconKind.Briefcase -> {
            drawRoundRect(tint, o(3.8f, 8.6f), Size(16.4f * u, 11f * u), CornerRadius(2.6f * u), stroke)
            val handle = Path().apply {
                moveTo(9.2f * u, 8.6f * u)
                lineTo(9.2f * u, 6.6f * u)
                quadraticBezierTo(9.2f * u, 5f * u, 10.8f * u, 5f * u)
                lineTo(13.2f * u, 5f * u)
                quadraticBezierTo(14.8f * u, 5f * u, 14.8f * u, 6.6f * u)
                lineTo(14.8f * u, 8.6f * u)
            }
            strokePath(handle, tint, sw)
            drawLine(tint, o(3.8f, 13f), o(20.2f, 13f), sw * 0.85f, StrokeCap.Round)
            drawCircle(tint, 1.3f * u, o(12f, 13f))
        }

        SqIconKind.Dumbbell -> {
            drawLine(tint, o(8.2f, 12f), o(15.8f, 12f), sw, StrokeCap.Round)
            drawRoundRect(tint, o(5.6f, 8f), Size(2.7f * u, 8f * u), CornerRadius(1.2f * u))
            drawRoundRect(tint, o(15.7f, 8f), Size(2.7f * u, 8f * u), CornerRadius(1.2f * u))
            drawRoundRect(tint, o(3.2f, 9.8f), Size(1.9f * u, 4.4f * u), CornerRadius(0.9f * u))
            drawRoundRect(tint, o(18.9f, 9.8f), Size(1.9f * u, 4.4f * u), CornerRadius(0.9f * u))
        }

        SqIconKind.Heart -> {
            val heart = Path().apply {
                moveTo(12f * u, 19.4f * u)
                cubicTo(9.2f * u, 17f * u, 5.6f * u, 14.4f * u, 4.5f * u, 11.3f * u)
                cubicTo(3.5f * u, 8.5f * u, 5.1f * u, 5.6f * u, 8f * u, 5.6f * u)
                cubicTo(9.8f * u, 5.6f * u, 11.2f * u, 6.7f * u, 12f * u, 8f * u)
                cubicTo(12.8f * u, 6.7f * u, 14.2f * u, 5.6f * u, 16f * u, 5.6f * u)
                cubicTo(18.9f * u, 5.6f * u, 20.5f * u, 8.5f * u, 19.5f * u, 11.3f * u)
                cubicTo(18.4f * u, 14.4f * u, 14.8f * u, 17f * u, 12f * u, 19.4f * u)
                close()
            }
            drawPath(heart, tint)
        }

        SqIconKind.Dice -> {
            drawRoundRect(tint, o(4f, 4f), Size(16f * u, 16f * u), CornerRadius(4.6f * u), stroke)
            drawCircle(tint, 1.5f * u, o(9f, 9f))
            drawCircle(tint, 1.5f * u, o(15f, 9f))
            drawCircle(tint, 1.5f * u, o(9f, 15f))
            drawCircle(tint, 1.5f * u, o(15f, 15f))
        }

        SqIconKind.Flame -> {
            val flame = Path().apply {
                moveTo(12f * u, 3.4f * u)
                cubicTo(12.7f * u, 6f * u, 17f * u, 8.2f * u, 17f * u, 12.6f * u)
                cubicTo(17f * u, 16.1f * u, 14.8f * u, 20f * u, 12f * u, 20f * u)
                cubicTo(9.2f * u, 20f * u, 7f * u, 16.1f * u, 7f * u, 12.6f * u)
                cubicTo(7f * u, 10.2f * u, 8.4f * u, 8.6f * u, 9.5f * u, 7.3f * u)
                cubicTo(10.5f * u, 6.1f * u, 11.5f * u, 5f * u, 12f * u, 3.4f * u)
                close()
            }
            drawPath(flame, tint)
            val inner = Path().apply {
                moveTo(12f * u, 12f * u)
                cubicTo(12.4f * u, 13.6f * u, 14.2f * u, 14f * u, 14.2f * u, 16.2f * u)
                cubicTo(14.2f * u, 17.8f * u, 13.2f * u, 19f * u, 12f * u, 19f * u)
                cubicTo(10.8f * u, 19f * u, 9.8f * u, 17.8f * u, 9.8f * u, 16.2f * u)
                cubicTo(9.8f * u, 14f * u, 11.6f * u, 13.6f * u, 12f * u, 12f * u)
                close()
            }
            drawPath(inner, Color.White.copy(alpha = 0.55f))
        }

        SqIconKind.Paw -> {
            drawOval(tint, o(8.6f, 11.6f), Size(6.8f * u, 6.4f * u))
            drawCircle(tint, 1.75f * u, o(6.7f, 10.4f))
            drawCircle(tint, 1.75f * u, o(10.1f, 7.9f))
            drawCircle(tint, 1.75f * u, o(13.9f, 7.9f))
            drawCircle(tint, 1.75f * u, o(17.3f, 10.4f))
        }

        SqIconKind.Star -> {
            val star = Path()
            val cx = 12f * u
            val cy = 12.6f * u
            val rOut = 9f * u
            val rIn = 3.9f * u
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) rOut else rIn
                val a = Math.toRadians(-90.0 + i * 36.0)
                val x = cx + (r * kotlin.math.cos(a)).toFloat()
                val y = cy + (r * kotlin.math.sin(a)).toFloat()
                if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
            }
            star.close()
            drawPath(star, tint)
        }

        SqIconKind.Sparkle -> {
            val sp = Path().apply {
                moveTo(12f * u, 3.4f * u)
                quadraticBezierTo(13.2f * u, 10.8f * u, 20.6f * u, 12f * u)
                quadraticBezierTo(13.2f * u, 13.2f * u, 12f * u, 20.6f * u)
                quadraticBezierTo(10.8f * u, 13.2f * u, 3.4f * u, 12f * u)
                quadraticBezierTo(10.8f * u, 10.8f * u, 12f * u, 3.4f * u)
                close()
            }
            drawPath(sp, tint)
        }

        SqIconKind.Target -> {
            drawCircle(tint, 7.8f * u, o(12f, 12f), style = stroke)
            drawCircle(tint, 4.4f * u, o(12f, 12f), style = Stroke(sw * 0.85f))
            drawCircle(tint, 1.7f * u, o(12f, 12f))
        }

        SqIconKind.Play -> {
            val tri = Path().apply {
                moveTo(9.2f * u, 6.6f * u)
                lineTo(9.2f * u, 17.4f * u)
                quadraticBezierTo(9.2f * u, 18.6f * u, 10.3f * u, 18f * u)
                lineTo(17.6f * u, 13.1f * u)
                quadraticBezierTo(18.6f * u, 12.5f * u, 17.6f * u, 11.7f * u)
                lineTo(10.3f * u, 6f * u)
                quadraticBezierTo(9.2f * u, 5.4f * u, 9.2f * u, 6.6f * u)
                close()
            }
            drawPath(tri, tint)
        }

        SqIconKind.Pause -> {
            drawRoundRect(tint, o(7.4f, 5.8f), Size(3.2f * u, 12.4f * u), CornerRadius(1.5f * u))
            drawRoundRect(tint, o(13.4f, 5.8f), Size(3.2f * u, 12.4f * u), CornerRadius(1.5f * u))
        }

        SqIconKind.Stop -> {
            drawRoundRect(tint, o(6.6f, 6.6f), Size(10.8f * u, 10.8f * u), CornerRadius(2.6f * u))
        }

        SqIconKind.Plus -> {
            drawLine(tint, o(12f, 5.4f), o(12f, 18.6f), sw, StrokeCap.Round)
            drawLine(tint, o(5.4f, 12f), o(18.6f, 12f), sw, StrokeCap.Round)
        }

        SqIconKind.Check -> {
            val path = Path().apply {
                moveTo(5.2f * u, 12.8f * u)
                lineTo(10f * u, 17.4f * u)
                lineTo(18.8f * u, 7f * u)
            }
            strokePath(path, tint, sw)
        }

        SqIconKind.Cross -> {
            drawLine(tint, o(6.6f, 6.6f), o(17.4f, 17.4f), sw, StrokeCap.Round)
            drawLine(tint, o(17.4f, 6.6f), o(6.6f, 17.4f), sw, StrokeCap.Round)
        }

        SqIconKind.ChevronRight -> {
            val path = Path().apply {
                moveTo(9.4f * u, 6f * u)
                lineTo(15.4f * u, 12f * u)
                lineTo(9.4f * u, 18f * u)
            }
            strokePath(path, tint, sw)
        }

        SqIconKind.ChevronLeft -> {
            val path = Path().apply {
                moveTo(14.6f * u, 6f * u)
                lineTo(8.6f * u, 12f * u)
                lineTo(14.6f * u, 18f * u)
            }
            strokePath(path, tint, sw)
        }

        SqIconKind.Trash -> {
            drawLine(tint, o(4.6f, 7f), o(19.4f, 7f), sw, StrokeCap.Round)
            val lid = Path().apply {
                moveTo(9.4f * u, 7f * u)
                lineTo(9.4f * u, 5.4f * u)
                quadraticBezierTo(9.4f * u, 4.4f * u, 10.4f * u, 4.4f * u)
                lineTo(13.6f * u, 4.4f * u)
                quadraticBezierTo(14.6f * u, 4.4f * u, 14.6f * u, 5.4f * u)
                lineTo(14.6f * u, 7f * u)
            }
            strokePath(lid, tint, sw)
            val body = Path().apply {
                moveTo(6.4f * u, 7f * u)
                lineTo(7.2f * u, 18.2f * u)
                quadraticBezierTo(7.3f * u, 19.8f * u, 8.9f * u, 19.8f * u)
                lineTo(15.1f * u, 19.8f * u)
                quadraticBezierTo(16.7f * u, 19.8f * u, 16.8f * u, 18.2f * u)
                lineTo(17.6f * u, 7f * u)
            }
            strokePath(body, tint, sw)
            drawLine(tint, o(10.2f, 10.4f), o(10.2f, 16.4f), sw * 0.85f, StrokeCap.Round)
            drawLine(tint, o(13.8f, 10.4f), o(13.8f, 16.4f), sw * 0.85f, StrokeCap.Round)
        }

        SqIconKind.Globe -> {
            drawCircle(tint, 8f * u, o(12f, 12f), style = stroke)
            drawOval(tint, o(8.4f, 4f), Size(7.2f * u, 16f * u), style = Stroke(sw * 0.85f))
            drawLine(tint, o(4f, 12f), o(20f, 12f), sw * 0.85f, StrokeCap.Round)
        }

        SqIconKind.Bell -> {
            val bell = Path().apply {
                moveTo(6f * u, 16.4f * u)
                lineTo(6f * u, 15.4f * u)
                quadraticBezierTo(7.4f * u, 13.8f * u, 7.4f * u, 10.6f * u)
                cubicTo(7.4f * u, 7.4f * u, 9.4f * u, 5.2f * u, 12f * u, 5.2f * u)
                cubicTo(14.6f * u, 5.2f * u, 16.6f * u, 7.4f * u, 16.6f * u, 10.6f * u)
                quadraticBezierTo(16.6f * u, 13.8f * u, 18f * u, 15.4f * u)
                lineTo(18f * u, 16.4f * u)
                close()
            }
            strokePath(bell, tint, sw)
            drawLine(tint, o(12f, 3.4f), o(12f, 5.2f), sw, StrokeCap.Round)
            val clapper = Path().apply {
                moveTo(10.3f * u, 18.8f * u)
                quadraticBezierTo(12f * u, 20.4f * u, 13.7f * u, 18.8f * u)
            }
            strokePath(clapper, tint, sw)
        }

        SqIconKind.Battery -> {
            drawRoundRect(tint, o(3.4f, 8f), Size(15f * u, 8f * u), CornerRadius(2.2f * u), stroke)
            drawRoundRect(tint, o(19.4f, 10.4f), Size(1.9f * u, 3.2f * u), CornerRadius(0.8f * u))
            drawRoundRect(tint, o(5.6f, 10f), Size(3.1f * u, 4f * u), CornerRadius(0.9f * u))
            drawRoundRect(tint, o(9.6f, 10f), Size(3.1f * u, 4f * u), CornerRadius(0.9f * u))
        }

        SqIconKind.Pencil -> {
            val body = Path().apply {
                moveTo(14.2f * u, 5.8f * u)
                lineTo(18.2f * u, 9.8f * u)
                lineTo(9f * u, 19f * u)
                lineTo(4.6f * u, 19.4f * u)
                lineTo(5f * u, 15f * u)
                close()
            }
            strokePath(body, tint, sw)
            drawLine(tint, o(12.4f, 7.6f), o(16.4f, 11.6f), sw * 0.85f, StrokeCap.Round)
        }

        SqIconKind.Clock -> {
            drawCircle(tint, 8f * u, o(12f, 12f), style = stroke)
            drawLine(tint, o(12f, 7.6f), o(12f, 12.2f), sw, StrokeCap.Round)
            drawLine(tint, o(12f, 12.2f), o(15.2f, 14.2f), sw, StrokeCap.Round)
        }

        SqIconKind.Report -> {
            drawRoundRect(tint, o(5f, 3.8f), Size(14f * u, 16.4f * u), CornerRadius(2.6f * u), stroke)
            drawLine(tint, o(8.4f, 9f), o(15.6f, 9f), sw * 0.9f, StrokeCap.Round)
            drawLine(tint, o(8.4f, 12.6f), o(15.6f, 12.6f), sw * 0.9f, StrokeCap.Round)
            drawLine(tint, o(8.4f, 16.2f), o(12.8f, 16.2f), sw * 0.9f, StrokeCap.Round)
        }

        SqIconKind.TrendUp -> {
            val path = Path().apply {
                moveTo(3.8f * u, 16.8f * u)
                lineTo(10f * u, 10.6f * u)
                lineTo(13.6f * u, 14.2f * u)
                lineTo(20.2f * u, 7.2f * u)
            }
            strokePath(path, tint, sw)
            val arrow = Path().apply {
                moveTo(15.4f * u, 7.2f * u)
                lineTo(20.2f * u, 7.2f * u)
                lineTo(20.2f * u, 12f * u)
            }
            strokePath(arrow, tint, sw)
        }

        SqIconKind.UserCheck -> {
            drawCircle(tint, 3.2f * u, o(9.4f, 8f), style = stroke)
            val shoulder = Path().apply {
                moveTo(3.8f * u, 19.2f * u)
                quadraticBezierTo(3.8f * u, 14.2f * u, 9.4f * u, 14.2f * u)
                quadraticBezierTo(12.4f * u, 14.2f * u, 13.8f * u, 15.6f * u)
            }
            strokePath(shoulder, tint, sw)
            val check = Path().apply {
                moveTo(14.6f * u, 12.4f * u)
                lineTo(17f * u, 14.8f * u)
                lineTo(21f * u, 10.2f * u)
            }
            strokePath(check, tint, sw)
        }
    }
}
