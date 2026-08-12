package com.sacmaib80.sakuraq.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.data.AppData
import com.sacmaib80.sakuraq.app.domain.DateUtil
import com.sacmaib80.sakuraq.app.domain.Energy
import com.sacmaib80.sakuraq.app.domain.categoryById
import com.sacmaib80.sakuraq.app.ui.components.Pill
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import com.sacmaib80.sakuraq.app.ui.theme.NumFont
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont

private val EnergyColor = mapOf(
    Energy.LOW to Color(0xFFEF4444),
    Energy.MID to Color(0xFFF59E0B),
    Energy.HIGH to Color(0xFF22C55E),
)

/** โมดัล Random Quest: เลือกพลังงาน → สุ่ม → การ์ดเควส + เริ่มเลย (ตาม rqOverlay ของแอปจริง) */
@Composable
fun DiceOverlay(
    data: AppData,
    onRoll: (Energy) -> Unit,
    onStart: () -> Unit,
    onClear: () -> Unit,
    onClose: () -> Unit,
) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    var energy by remember { mutableStateOf(Energy.MID) }

    BackHandler { onClose() }

    // นับถอยหลัง cooldown
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(data.dice.cooldownUntil) {
        while (System.currentTimeMillis() < data.dice.cooldownUntil) {
            now = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000)
        }
        now = System.currentTimeMillis()
    }
    val cooldownLeft = data.dice.cooldownUntil - now
    val canRoll = cooldownLeft <= 0

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x701E193C))
            .bouncyClick(1f, onClose),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .shadow(22.dp, RoundedCornerShape(28.dp), ambientColor = Color(0x40312E81), spotColor = Color(0x55312E81))
                .clip(RoundedCornerShape(28.dp))
                .background(Sakura.card)
                .bouncyClick(1f) { /* กันแตะทะลุปิด */ }
                .padding(20.dp),
        ) {
            // ── header ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Sakura.accentSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    SqIcon(SqIconKind.Battery, size = 24.dp, tint = Sakura.accent)
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text(s.rqTitle, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink, fontFamily = ThaiFont)
                    Text(s.rqSub, fontSize = 11.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
                }
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Sakura.bg2)
                        .bouncyClick(0.85f, onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    SqIcon(SqIconKind.Cross, size = 14.dp, tint = Sakura.muted, stroke = 2.4f)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── เลือกพลังงาน ──
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EnergyCol(Energy.LOW, "LOW", s.rqLowDesc, s.rqLowSub, energy, Modifier.weight(1f)) { energy = it }
                EnergyCol(Energy.MID, "MID", s.rqMidDesc, s.rqMidSub, energy, Modifier.weight(1f)) { energy = it }
                EnergyCol(Energy.HIGH, "HIGH", s.rqHighDesc, s.rqHighSub, energy, Modifier.weight(1f)) { energy = it }
            }

            Spacer(Modifier.height(14.dp))

            // ── ปุ่มสุ่ม ──
            Box(
                Modifier
                    .fillMaxWidth()
                    .shadow(if (canRoll) 12.dp else 0.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x525B6AF0), spotColor = Color(0x525B6AF0))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (canRoll) Brush.linearGradient(listOf(Sakura.accent, Sakura.accent2))
                        else Brush.linearGradient(listOf(Sakura.line, Sakura.line)),
                    )
                    .bouncyClick(0.96f) {
                        if (canRoll) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onRoll(energy)
                        }
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (canRoll) s.rqRollBtn else s.rqCooldownShort.tmpl("time" to DateUtil.fmtClock(cooldownLeft / 1000)),
                    color = if (canRoll) Color.White else Sakura.muted,
                    fontSize = 15.5.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont,
                )
            }

            // ── ผลลัพธ์ ──
            AnimatedVisibility(
                visible = data.dice.hasOffer,
                enter = expandVertically(spring(stiffness = 240f, dampingRatio = 0.8f)) + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                val cat = categoryById(data.dice.offerCategory)
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(18.dp))
                    Text(s.rqNewQuest, fontSize = 12.sp, color = Sakura.accent, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        data.dice.offerName,
                        fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink,
                        fontFamily = ThaiFont, textAlign = TextAlign.Center, lineHeight = 24.sp,
                    )
                    Spacer(Modifier.height(9.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Pill(text = cat.label, bg = cat.soft, fg = cat.gradEnd)
                        Pill(text = "${data.dice.offerDuration} ${s.min}", bg = Sakura.bg2, fg = Sakura.ink2)
                        val e = Energy.entries.firstOrNull { it.key == data.dice.offerEnergy } ?: Energy.MID
                        Pill(text = e.name, bg = EnergyColor.getValue(e).copy(alpha = 0.14f), fg = EnergyColor.getValue(e))
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            Modifier
                                .weight(1f)
                                .shadow(10.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x4022C55E), spotColor = Color(0x4022C55E))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.verticalGradient(listOf(Color(0xFF4ADE80), Color(0xFF22C55E))))
                                .bouncyClick(0.95f) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onStart()
                                }
                                .padding(vertical = 13.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(s.rqStartNow, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, fontFamily = ThaiFont)
                        }
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Sakura.bg2)
                                .bouncyClick(0.95f, onClear)
                                .padding(horizontal = 16.dp, vertical = 13.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(s.rqClearQuest, color = Sakura.muted, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                s.rqCooldownHint,
                fontSize = 10.5.sp, color = Sakura.muted, fontFamily = ThaiFont,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun EnergyCol(
    value: Energy,
    title: String,
    desc: String,
    sub: String,
    selected: Energy,
    modifier: Modifier = Modifier,
    onPick: (Energy) -> Unit,
) {
    val color = EnergyColor.getValue(value)
    val active = selected == value
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (active) color.copy(alpha = 0.11f) else Sakura.card2)
            .border(1.5.dp, if (active) color.copy(alpha = 0.55f) else Sakura.line, RoundedCornerShape(16.dp))
            .bouncyClick(0.94f) { onPick(value) }
            .padding(vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(color))
        Spacer(Modifier.height(5.dp))
        Text(title, fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold, color = color, fontFamily = NumFont)
        Text(desc, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Sakura.ink2, fontFamily = ThaiFont)
        Text(sub, fontSize = 9.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
    }
}
