package com.sacmaib80.sakuraq.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.data.Session
import com.sacmaib80.sakuraq.app.data.SessionStatus
import com.sacmaib80.sakuraq.app.domain.DateUtil
import com.sacmaib80.sakuraq.app.domain.categoryById
import com.sacmaib80.sakuraq.app.ui.components.ProgressRing
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.components.enterStagger
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import com.sacmaib80.sakuraq.app.ui.theme.NumFont
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont
import kotlin.math.abs

private val NightTop = Color(0xFF1C1045)
private val NightMid = Color(0xFF0E0827)
private val NightBottom = Color(0xFF08061A)
private val ArcPurple = Color(0xFF5B6AF0)
private val ArcPurple2 = Color(0xFF7C7DE8)

private fun catIcon(id: String?): SqIconKind = when (id) {
    "Learning" -> SqIconKind.Book
    "Work/Project" -> SqIconKind.Briefcase
    "Physical" -> SqIconKind.Dumbbell
    else -> SqIconKind.Heart
}

/** จอจับเวลา — ธีมราตรีม่วงตามแอปจริง: glow หมุนรอบวง, arc เรืองแสง, ปุ่ม ghost */
@Composable
fun RunScreen(
    session: Session,
    nowMs: Long,
    sessionN: Int,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onExtend: (Int) -> Unit,
    onStop: () -> Unit,
) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    val cat = categoryById(session.category)
    val paused = session.status == SessionStatus.PAUSED

    val totalMs = session.plannedMin * 60000L
    val remainingMs = if (paused) session.remainingMs else session.targetEndTs - nowMs
    val overtime = remainingMs < 0
    val progress = (remainingMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)

    var overtimeFired by remember { mutableStateOf(false) }
    LaunchedEffect(overtime) {
        if (overtime && !overtimeFired) {
            overtimeFired = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // back ระหว่างเควส = pause (ไม่ให้หลุดจอ)
    BackHandler { if (!paused) onPause() }

    val anim = rememberInfiniteTransition(label = "night")
    val glowAngle by anim.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "glowSpin",
    )
    val orbPulse by anim.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "orb",
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NightTop, NightMid, NightBottom))),
    ) {
        // แสง ambient ฟุ้งบนจอ
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF5B5BD6).copy(alpha = 0.16f * orbPulse), Color.Transparent),
                    center = Offset(size.width / 2, size.height * 0.16f),
                    radius = size.width * 0.62f,
                ),
                radius = size.width * 0.62f,
                center = Offset(size.width / 2, size.height * 0.16f),
            )
        }

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(54.dp))

            // ── หมวด (ชิปแก้วมืด) + ชื่อเควส ──
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.enterStagger(0)) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0x12FFFFFF))
                        .border(1.dp, Color(0x475B5BD6), RoundedCornerShape(99.dp))
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SqIcon(catIcon(session.category), size = 15.dp, tint = Color(0xFFC4B5FD))
                    Spacer(Modifier.width(7.dp))
                    Text(cat.label, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC4B5FD), fontFamily = ThaiFont)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    session.questName.ifBlank { cat.label },
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF0EEFF), fontFamily = ThaiFont,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 30.dp),
                )
                Spacer(Modifier.height(5.dp))
                if (paused || overtime) {
                    Text(
                        if (paused) s.runPausedBadge else s.runOvertimeBadge,
                        fontSize = 12.5.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont,
                        color = if (paused) Color(0xFFFBBF24) else Color(0xFFFF7B93),
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // ── วงแหวน + glow หมุน ──
            Box(contentAlignment = Alignment.Center, modifier = Modifier.enterStagger(1)) {
                Box(
                    Modifier
                        .size(318.dp)
                        .graphicsLayer {
                            rotationZ = glowAngle
                            alpha = if (paused) 0.22f else 1f
                        }
                        .background(
                            Brush.sweepGradient(
                                listOf(Color.Transparent, Color(0x2E7882FF), Color.Transparent, Color.Transparent),
                            ),
                            CircleShape,
                        ),
                )
                ProgressRing(
                    progress = if (overtime) 1f else progress,
                    gradStart = if (overtime) Color(0xFFF43F5E) else if (paused) Color(0xFFF59E0B) else ArcPurple,
                    gradEnd = if (overtime) Color(0xFFE11D48) else if (paused) Color(0xFFD97706) else ArcPurple2,
                    size = 284.dp,
                    stroke = 15.dp,
                    trackColor = Color(0x245B5BD6),
                    glow = true,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val secs = abs(remainingMs) / 1000
                        Text(
                            (if (overtime) "+" else "") + DateUtil.fmtClock(secs),
                            fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NumFont,
                            color = if (overtime) Color(0xFFFF8099) else Color(0xFFF0EEFF),
                        )
                        Spacer(Modifier.height(9.dp))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(Color(0x1F5B5BD6))
                                .border(1.dp, Color(0x335B5BD6), RoundedCornerShape(99.dp))
                                .padding(horizontal = 14.dp, vertical = 5.dp),
                        ) {
                            Text(
                                s.runSessionN.tmpl("n" to sessionN),
                                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA5B4FC), fontFamily = ThaiFont,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── คอนโทรล: +5 +10 | Pause orb | Stop ──
            Row(
                Modifier
                    .padding(horizontal = 24.dp)
                    .enterStagger(2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GhostPill("+5", s.min) { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onExtend(5) }
                GhostPill("+10", s.min) { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onExtend(10) }

                // ปุ่มกลม pause/resume
                Box(
                    Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            if (paused) Brush.linearGradient(listOf(Color(0xFF34D399), Color(0xFF22C55E)))
                            else Brush.linearGradient(listOf(Color(0xFF5B5BD6), Color(0xFF7C7DE8))),
                        )
                        .border(3.dp, Color(0x14FFFFFF), CircleShape)
                        .bouncyClick(0.88f) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (paused) onResume() else onPause()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    AnimatedContent(
                        targetState = paused,
                        transitionSpec = { (scaleIn(initialScale = 0.55f, animationSpec = spring(stiffness = 480f, dampingRatio = 0.6f)) + fadeIn()).togetherWith(fadeOut()) },
                        label = "pauseIcon",
                    ) { p ->
                        SqIcon(if (p) SqIconKind.Play else SqIconKind.Pause, size = 27.dp, tint = Color.White)
                    }
                }

                // stop ghost แดง
                Column(
                    Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0x14FF3C3C))
                        .border(1.dp, Color(0x33FF5050), RoundedCornerShape(99.dp))
                        .bouncyClick(0.9f) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStop()
                        }
                        .padding(horizontal = 17.dp, vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SqIcon(SqIconKind.Stop, size = 15.dp, tint = Color(0xD9FF6464))
                    Text("Stop", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xD9FF6464), fontFamily = NumFont)
                }
            }

            Spacer(Modifier.height(46.dp))
        }
    }
}

@Composable
private fun GhostPill(big: String, small: String, onClick: () -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(Color(0x0FFFFFFF))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(99.dp))
            .bouncyClick(0.9f, onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(big, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = NumFont)
        Text(small, fontSize = 10.sp, color = Color(0x80FFFFFF), fontFamily = ThaiFont)
    }
}
