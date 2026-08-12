package com.sacmaib80.sakuraq.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.R
import com.sacmaib80.sakuraq.app.ui.AppViewModel
import com.sacmaib80.sakuraq.app.ui.QuestResult
import com.sacmaib80.sakuraq.app.ui.components.CelebrationParticles
import com.sacmaib80.sakuraq.app.ui.components.CountUpValue
import com.sacmaib80.sakuraq.app.ui.components.Mascot
import com.sacmaib80.sakuraq.app.ui.components.Pill
import com.sacmaib80.sakuraq.app.ui.components.SoftCard
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.domain.fmtScore
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import com.sacmaib80.sakuraq.app.ui.theme.NumFont
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ฉลองจบเควส: confetti + ซากุระ + มาสคอต pop + แต้ม count-up
 * render บน App level (บทเรียนจาก React: อย่าให้ unmount กลางคัน)
 */
@Composable
fun CelebrationOverlay(
    result: QuestResult,
    onDismiss: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    BackHandler { onDismiss() }

    // auto-dismiss 3.2s หรือแตะ
    LaunchedEffect(Unit) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        delay(3200)
        onDismiss()
    }

    val cardScale = remember { Animatable(0.6f) }
    val cardAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        coroutineScope {
            launch { cardAlpha.animateTo(1f, spring(stiffness = 300f)) }
            launch { cardScale.animateTo(1f, spring(stiffness = 260f, dampingRatio = 0.55f)) }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x521E193C))
            .bouncyClick(1f) { onDismiss() },
        contentAlignment = Alignment.Center,
    ) {
        CelebrationParticles(Modifier.fillMaxSize())

        SoftCard(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 34.dp)
                .graphicsLayer {
                    scaleX = cardScale.value
                    scaleY = cardScale.value
                    alpha = cardAlpha.value
                },
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Mascot(size = 130.dp, wave = true)
                Spacer(Modifier.height(2.dp))
                Text(result.questName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Sakura.ink2, fontFamily = ThaiFont)
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.celebrate_gained), fontSize = 12.sp, color = Sakura.muted, fontFamily = ThaiFont, fontWeight = FontWeight.SemiBold)
                CountUpValue(target = result.questScore, durationMs = 1100) { txt ->
                    Text(
                        "+$txt",
                        fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NumFont,
                        color = Sakura.accent,
                    )
                }
                if (result.bonus > 0) {
                    Spacer(Modifier.height(4.dp))
                    BonusPop(result.bonus)
                }
                Spacer(Modifier.height(14.dp))
                PowerUpBar(before = result.dayTotalBefore, after = result.dayTotalAfter)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.celebrate_tap), fontSize = 11.sp, color = Sakura.muted, fontFamily = ThaiFont)
            }
        }
    }
}

/** แถบ Power Up: แต้มวันนี้วิ่งจากก่อน → หลังเควสนี้ (พอร์ต homeResultScreen ของเว็บ) */
@Composable
private fun PowerUpBar(before: Double, after: Double) {
    val s = LocalStrings.current
    val frac = remember { Animatable((before / 100.0).toFloat().coerceIn(0f, 1f)) }
    LaunchedEffect(Unit) {
        delay(750)
        frac.animateTo((after / 100.0).toFloat().coerceIn(0f, 1f), spring(stiffness = 55f, dampingRatio = 0.9f))
    }
    Column(Modifier.fillMaxWidth()) {
        Row {
            Text(
                s.hrpPowerUp,
                fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF5B6AF0),
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${(frac.value * 100).toInt()}%",
                fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NumFont,
                color = Color(0xFF5B6AF0),
            )
        }
        Spacer(Modifier.height(5.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(11.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Color(0xFFEEF2FF)),
        ) {
            if (frac.value > 0.005f) {
                Box(
                    Modifier
                        .fillMaxWidth(frac.value)
                        .height(11.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFA78BFA), Color(0xFF5B5BD6)))),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            s.hrpDailyProgress.tmpl("score" to fmtScore(after)),
            fontSize = 10.5.sp, color = Color(0xFF8A90AD), fontFamily = ThaiFont,
        )
    }
}

@Composable
private fun BonusPop(bonus: Int) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(500)
        scale.animateTo(1f, spring(stiffness = 300f, dampingRatio = 0.5f))
    }
    Box(Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value }) {
        Pill(
            text = stringResource(R.string.celebrate_bonus, bonus),
            bg = Sakura.good.copy(alpha = 0.16f),
            fg = Color(0xFF059669),
        )
    }
}
