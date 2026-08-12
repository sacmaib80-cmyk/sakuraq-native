package com.sacmaib80.sakuraq.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.ui.components.SoftCard
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.components.enterStagger
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont
import kotlinx.coroutines.delay

/** แท็บเมนู: ภาษา / คู่มือ / แจ้งเตือน / ล้างข้อมูล / เกี่ยวกับ (ตาม topMenu ของแอปจริง ตัด gate/break/store) */
@Composable
fun MenuScreen(
    language: String,
    versionName: String,
    onSetLanguage: (String) -> Unit,
    onClearToday: () -> Unit,
    onResetAll: () -> Unit,
) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    var guideOpen by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(18.dp))
        Text("Menu", fontSize = 25.sp, color = Sakura.ink, fontWeight = FontWeight.ExtraBold, fontFamily = ThaiFont, modifier = Modifier.enterStagger(0))
        Spacer(Modifier.height(14.dp))

        // ── ภาษา ──
        SoftCard(Modifier.fillMaxWidth().enterStagger(1), shape = Sakura.innerShape) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MenuIcon(SqIconKind.Globe)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(s.menuLangTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont)
                    Text(s.menuLangSub, fontSize = 11.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
                }
                LangChip("ไทย", language != "en") { onSetLanguage("th") }
                Spacer(Modifier.width(6.dp))
                LangChip("EN", language == "en") { onSetLanguage("en") }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── คู่มือ ──
        SoftCard(Modifier.fillMaxWidth().enterStagger(2), shape = Sakura.innerShape) {
            Row(
                Modifier.fillMaxWidth().bouncyClick(0.985f) { guideOpen = !guideOpen },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MenuIcon(SqIconKind.Report)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(s.menuGuideTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont)
                    Text(s.menuGuideSub, fontSize = 11.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
                }
                val chev by animateFloatAsState(if (guideOpen) 90f else 0f, spring(stiffness = 300f), label = "chev")
                SqIcon(SqIconKind.ChevronRight, size = 16.dp, tint = Sakura.muted, modifier = Modifier.rotate(chev))
            }
            AnimatedVisibility(
                visible = guideOpen,
                enter = expandVertically(spring(stiffness = 260f, dampingRatio = 0.85f)) + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    s.guideLines.forEach { (title, body) ->
                        Row(Modifier.padding(bottom = 10.dp)) {
                            Box(
                                Modifier.padding(top = 6.dp).size(7.dp).clip(CircleShape).background(Sakura.accent),
                            )
                            Spacer(Modifier.width(9.dp))
                            Column {
                                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont)
                                Text(body, fontSize = 12.sp, color = Sakura.ink2, fontFamily = ThaiFont, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── แจ้งเตือน (ข้อมูล) ──
        SoftCard(Modifier.fillMaxWidth().enterStagger(3), shape = Sakura.innerShape) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MenuIcon(SqIconKind.Bell)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(s.menuNotiTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont)
                    Text(s.menuNotiSub, fontSize = 11.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
                }
                Text("ON", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.good, fontFamily = ThaiFont)
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Data Management ──
        SoftCard(Modifier.fillMaxWidth().enterStagger(4), shape = Sakura.innerShape) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MenuIcon(SqIconKind.Trash)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(s.menuDataTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont)
                    Text(s.menuDataSub, fontSize = 11.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
                }
            }
            Spacer(Modifier.height(12.dp))
            DangerRow(
                title = s.menuClearToday, sub = s.menuClearTodaySub, confirmText = s.menuTapAgain,
                color = Sakura.warn,
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClearToday()
            }
            Spacer(Modifier.height(8.dp))
            DangerRow(
                title = s.menuResetAll, sub = s.menuResetAllSub, confirmText = s.menuResetAllConfirm,
                color = Sakura.bad,
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onResetAll()
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── เกี่ยวกับแอป ──
        SoftCard(Modifier.fillMaxWidth().enterStagger(5), shape = Sakura.innerShape) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MenuIcon(SqIconKind.Sparkle)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(s.menuAboutTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont)
                    Text("${s.menuAboutSub} • v$versionName", fontSize = 11.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun MenuIcon(kind: SqIconKind) {
    Box(
        Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(Sakura.accentSoft),
        contentAlignment = Alignment.Center,
    ) {
        SqIcon(kind, size = 20.dp, tint = Sakura.accent)
    }
}

@Composable
private fun LangChip(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(if (active) Sakura.accent else Sakura.bg2)
            .bouncyClick(0.92f, onClick)
            .padding(horizontal = 13.dp, vertical = 7.dp),
    ) {
        Text(
            label, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont,
            color = if (active) Color.White else Sakura.ink2,
        )
    }
}

/** ปุ่มอันตรายแบบแตะ 2 ครั้งยืนยัน (เลี่ยง confirm dialog — บทเรียนจากเว็บ) */
@Composable
private fun DangerRow(title: String, sub: String, confirmText: String, color: Color, onConfirm: () -> Unit) {
    var armed by remember { mutableStateOf(false) }
    LaunchedEffect(armed) {
        if (armed) {
            delay(2600)
            armed = false
        }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(if (armed) color.copy(alpha = 0.14f) else Sakura.bg2)
            .bouncyClick(0.97f) {
                if (armed) { armed = false; onConfirm() } else armed = true
            }
            .padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                title, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont,
                color = if (armed) color else Sakura.ink,
            )
            Text(
                if (armed) confirmText else sub,
                fontSize = 11.sp, fontFamily = ThaiFont,
                color = if (armed) color else Sakura.muted,
            )
        }
        SqIcon(SqIconKind.ChevronRight, size = 14.dp, tint = if (armed) color else Sakura.muted)
    }
}
