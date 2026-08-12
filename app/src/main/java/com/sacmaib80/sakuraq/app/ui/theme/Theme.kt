@file:OptIn(ExperimentalTextApi::class)

package com.sacmaib80.sakuraq.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.R

// ── SakuraQ design tokens (pastel lavender — จาก React :root) ──
object Sakura {
    val bg = Color(0xFFF4F6FF)
    val bg2 = Color(0xFFEEF2FF)
    val bg3 = Color(0xFFFBF4FF)
    val accent = Color(0xFF5B6AF0)
    val accent2 = Color(0xFF7C7DE8)
    val accent3 = Color(0xFFA78BFA)
    val accentSoft = Color(0x245B6AF0)
    val ink = Color(0xFF1F2340)
    val ink2 = Color(0xFF4A4F6B)
    val muted = Color(0xFF8A90AD)
    val line = Color(0xFFE7E9F7)
    val card = Color(0xFFFFFFFF)
    val card2 = Color(0xFFFBFCFF)
    val good = Color(0xFF34D399)
    val warn = Color(0xFFF59E0B)
    val bad = Color(0xFFF43F5E)
    val pink = Color(0xFFF472B6)

    val radius = 26.dp
    val radiusSm = 16.dp
    val cardShape = RoundedCornerShape(26.dp)
    val innerShape = RoundedCornerShape(16.dp)

    val ctaBrush = Brush.linearGradient(listOf(accent, accent2))
    val trackBrush = Brush.horizontalGradient(listOf(accent, accent2, accent3))
    val bgBrush = Brush.verticalGradient(listOf(bg, bg2))
}

private fun thai(weight: Int, fw: FontWeight) = Font(
    R.font.notosansthai, weight = fw,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

private fun jakarta(weight: Int, fw: FontWeight) = Font(
    R.font.plusjakartasans, weight = fw,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val ThaiFont = FontFamily(
    thai(400, FontWeight.Normal),
    thai(500, FontWeight.Medium),
    thai(600, FontWeight.SemiBold),
    thai(700, FontWeight.Bold),
    thai(800, FontWeight.ExtraBold),
)

/** ฟอนต์ตัวเลขใหญ่ (นาฬิกา/คะแนน) */
val NumFont = FontFamily(
    jakarta(600, FontWeight.SemiBold),
    jakarta(700, FontWeight.Bold),
    jakarta(800, FontWeight.ExtraBold),
)

// กันสระบน/วรรณยุกต์ไทยโดน clip: เปิด font padding + lineHeight กว้าง
private val NoClip = PlatformTextStyle(includeFontPadding = true)

private fun style(size: Int, weight: FontWeight, lineHeight: Float = 1.5f) = TextStyle(
    fontFamily = ThaiFont,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * lineHeight).sp,
    color = Sakura.ink,
    platformStyle = NoClip,
)

val SakuraTypography = Typography(
    displayLarge = style(40, FontWeight.ExtraBold, 1.35f),
    displayMedium = style(32, FontWeight.ExtraBold, 1.35f),
    headlineLarge = style(26, FontWeight.Bold, 1.4f),
    headlineMedium = style(22, FontWeight.Bold, 1.4f),
    headlineSmall = style(19, FontWeight.Bold, 1.4f),
    titleLarge = style(17, FontWeight.Bold),
    titleMedium = style(15, FontWeight.SemiBold),
    titleSmall = style(13, FontWeight.SemiBold),
    bodyLarge = style(15, FontWeight.Normal),
    bodyMedium = style(13.5f.toInt(), FontWeight.Normal),
    bodySmall = style(12, FontWeight.Normal),
    labelLarge = style(14, FontWeight.SemiBold),
    labelMedium = style(12, FontWeight.SemiBold),
    labelSmall = style(11, FontWeight.Medium),
)

private val SakuraColors: ColorScheme = lightColorScheme(
    primary = Sakura.accent,
    onPrimary = Color.White,
    secondary = Sakura.accent2,
    tertiary = Sakura.accent3,
    background = Sakura.bg,
    onBackground = Sakura.ink,
    surface = Sakura.card,
    onSurface = Sakura.ink,
    surfaceVariant = Sakura.bg2,
    onSurfaceVariant = Sakura.ink2,
    outline = Sakura.line,
    error = Sakura.bad,
)

@Composable
fun SakuraTheme(content: @Composable () -> Unit) {
    // light เท่านั้น v1 (ตรงกับตัวตนแอป)
    isSystemInDarkTheme() // อ่านทิ้งไว้เฉยๆ — บังคับ light
    MaterialTheme(
        colorScheme = SakuraColors,
        typography = SakuraTypography,
        content = content,
    )
}
