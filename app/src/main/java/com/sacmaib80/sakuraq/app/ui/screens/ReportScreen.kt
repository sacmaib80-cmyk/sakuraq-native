package com.sacmaib80.sakuraq.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.data.AppData
import com.sacmaib80.sakuraq.app.domain.BonusInputs
import com.sacmaib80.sakuraq.app.domain.DateUtil
import com.sacmaib80.sakuraq.app.domain.ReportMode
import com.sacmaib80.sakuraq.app.domain.Rules
import com.sacmaib80.sakuraq.app.domain.ScoreEngine
import com.sacmaib80.sakuraq.app.domain.categoryById
import com.sacmaib80.sakuraq.app.domain.fmtScore
import com.sacmaib80.sakuraq.app.ui.components.Pill
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import com.sacmaib80.sakuraq.app.ui.theme.NumFont
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont

/**
 * Report 2 สเต็ปตามแอปจริง: บันทึกหลัก → เลือกประเภท (ปกติ / เปลี่ยนวิธีทำ +4 / ยืนยันพฤติกรรม +6)
 * back = ถอยสเต็ป, สเต็ปแรก back = การ์ดยืนยันทิ้ง
 */
@Composable
fun ReportScreen(
    data: AppData,
    seedMinutes: Int,
    onDiscard: () -> Unit,
    onSave: (minutes: Int, note: String, from: String, to: String, proof: String, mode: ReportMode) -> Unit,
) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    val session = data.session
    val cat = categoryById(session.category)

    var step by rememberSaveable { mutableIntStateOf(0) }
    var note by rememberSaveable { mutableStateOf("") }
    var from by rememberSaveable { mutableStateOf("") }
    var to by rememberSaveable { mutableStateOf("") }
    var proof by rememberSaveable { mutableStateOf("") }
    var modeSel by rememberSaveable { mutableStateOf(ReportMode.NORMAL.name) }
    var confirmDiscard by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    val mode = ReportMode.valueOf(modeSel)
    val minutes = seedMinutes
    val minuteLocked = session.elapsedMs < 60_000L

    BackHandler {
        when {
            confirmDiscard -> confirmDiscard = false
            step > 0 -> step = 0
            else -> confirmDiscard = true
        }
    }

    val day = data.days[DateUtil.localDateKey()]
    val qn = session.questName.trim().ifEmpty { "Quest" }
    val projected = ScoreEngine.scoreQuest(day, qn, minutes, BonusInputs(note, from, to, proof, mode))
    val bonusUnlocked = minutes >= Rules.BONUS_MIN_MINUTES

    Box(Modifier.fillMaxSize().background(Sakura.bg)) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .imePadding(),
        ) {
            Spacer(Modifier.height(16.dp))

            // ── back ──
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Sakura.card)
                    .bouncyClick(0.9f) {
                        if (step > 0) step = 0 else confirmDiscard = true
                    },
                contentAlignment = Alignment.Center,
            ) {
                SqIcon(SqIconKind.ChevronLeft, size = 18.dp, tint = Sakura.ink2, stroke = 2.4f)
            }

            Spacer(Modifier.height(12.dp))
            Text(s.hrpReportLabel, fontSize = 12.5.sp, color = Sakura.accent, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
            Spacer(Modifier.height(3.dp))
            Text(s.hrpHowWas, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink, fontFamily = ThaiFont)
            Spacer(Modifier.height(7.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Pill(text = "${cat.label} • $qn", bg = cat.soft, fg = cat.gradEnd)
            }
            Spacer(Modifier.height(6.dp))
            // สรุปนาที + คะแนนสด
            AnimatedContent(
                targetState = projected.questScore,
                transitionSpec = { (scaleIn(initialScale = 1.18f, animationSpec = spring(stiffness = 400f, dampingRatio = 0.65f)) + fadeIn()).togetherWith(fadeOut()) },
                label = "sum",
            ) { sc ->
                Text(
                    s.hrpSummary.tmpl("min" to minutes, "score" to fmtScore(sc)) +
                        (if (projected.bonus > 0) "  (+${projected.bonus})" else ""),
                    fontSize = 13.sp, color = Sakura.ink2, fontWeight = FontWeight.SemiBold, fontFamily = ThaiFont,
                )
            }
            if (minuteLocked) {
                Spacer(Modifier.height(4.dp))
                Text(s.hrpLockedMin, fontSize = 11.5.sp, color = Sakura.bad, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
            } else if (!bonusUnlocked) {
                Spacer(Modifier.height(4.dp))
                Text(s.hrpBonusNeedMin.tmpl("n" to Rules.BONUS_MIN_MINUTES), fontSize = 11.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
            }

            Spacer(Modifier.height(16.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    val dir = if (targetState > initialState) 1 else -1
                    (slideInHorizontally(spring(stiffness = 300f, dampingRatio = 0.8f)) { it / 3 * dir } + fadeIn())
                        .togetherWith(slideOutHorizontally { -it / 4 * dir } + fadeOut())
                },
                label = "reportStep",
            ) { st ->
                if (st == 0) {
                    // ── STEP 1: บันทึกหลัก ──
                    Column {
                        Text(s.hrpMainNote, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Sakura.ink2, fontFamily = ThaiFont)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { Text(s.hrpNotePh, color = Sakura.muted, fontFamily = ThaiFont, fontSize = 13.5.sp) },
                            textStyle = TextStyle(fontSize = 14.5.sp, fontFamily = ThaiFont, color = Sakura.ink, lineHeight = 22.sp),
                            minLines = 5,
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Sakura.accent,
                                unfocusedBorderColor = Sakura.line,
                                focusedContainerColor = Sakura.card,
                                unfocusedContainerColor = Sakura.card,
                                cursorColor = Sakura.accent,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (bonusUnlocked) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "+2: ${s.hrpNormalSub}",
                                fontSize = 11.sp, color = if (note.trim().length >= 20) Color(0xFF059669) else Sakura.muted,
                                fontFamily = ThaiFont, fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Spacer(Modifier.height(18.dp))
                        PrimaryBtn(text = s.next, enabled = !minuteLocked) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            step = 1
                        }
                        Spacer(Modifier.height(90.dp))
                    }
                } else {
                    // ── STEP 2: เลือกประเภทรายงาน ──
                    Column {
                        Text(s.hrpInsightQ, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink, fontFamily = ThaiFont)
                        Spacer(Modifier.height(11.dp))

                        ChoiceCard(SqIconKind.Report, s.hrpNormalTitle, s.hrpNormalSub, null, mode == ReportMode.NORMAL) {
                            modeSel = ReportMode.NORMAL.name
                        }
                        Spacer(Modifier.height(9.dp))
                        ChoiceCard(SqIconKind.TrendUp, s.hrpMethodTitle, s.hrpMethodSub, "+4", mode == ReportMode.METHOD) {
                            modeSel = ReportMode.METHOD.name
                        }
                        AnimatedVisibility(
                            visible = mode == ReportMode.METHOD,
                            enter = expandVertically(spring(stiffness = 280f, dampingRatio = 0.85f)) + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                        ) {
                            Column {
                                Spacer(Modifier.height(9.dp))
                                SmallField(s.hrpFromPh, from) { from = it }
                                Spacer(Modifier.height(8.dp))
                                SmallField(s.hrpToPh, to) { to = it }
                            }
                        }
                        Spacer(Modifier.height(9.dp))
                        ChoiceCard(SqIconKind.UserCheck, s.hrpBehaviorTitle, s.hrpBehaviorSub, "+6", mode == ReportMode.BEHAVIOR) {
                            modeSel = ReportMode.BEHAVIOR.name
                        }
                        AnimatedVisibility(
                            visible = mode == ReportMode.BEHAVIOR,
                            enter = expandVertically(spring(stiffness = 280f, dampingRatio = 0.85f)) + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                        ) {
                            Column {
                                Spacer(Modifier.height(9.dp))
                                SmallField(s.hrpProofPh, proof, multiline = true) { proof = it }
                            }
                        }

                        Spacer(Modifier.height(18.dp))
                        PrimaryBtn(text = s.hrpSubmit, enabled = !minuteLocked && !saving) {
                            saving = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSave(minutes, note, from, to, proof, mode)
                        }
                        Spacer(Modifier.height(10.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .bouncyClick(0.97f) { confirmDiscard = true }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(s.hrpDiscardTitle, fontSize = 13.sp, color = Sakura.muted, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
                        }
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }

        // ── การ์ดยืนยันทิ้ง ──
        AnimatedVisibility(
            visible = confirmDiscard,
            enter = fadeIn(spring(stiffness = 400f)),
            exit = fadeOut(),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x8A1E193C))
                    .bouncyClick(1f) { confirmDiscard = false },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    Modifier
                        .padding(horizontal = 44.dp)
                        .shadow(20.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Sakura.card)
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier.size(52.dp).clip(CircleShape).background(Sakura.bad.copy(alpha = 0.13f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        SqIcon(SqIconKind.Trash, size = 24.dp, tint = Sakura.bad)
                    }
                    Spacer(Modifier.height(11.dp))
                    Text(s.hrpDiscardTitle, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink, fontFamily = ThaiFont)
                    Spacer(Modifier.height(3.dp))
                    Text(s.hrpDiscardSub, fontSize = 12.5.sp, color = Sakura.muted, fontFamily = ThaiFont, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(15.dp))
                                .background(Sakura.bg2)
                                .bouncyClick(0.95f) { confirmDiscard = false }
                                .padding(vertical = 13.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(s.cancel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Sakura.ink2, fontFamily = ThaiFont)
                        }
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(15.dp))
                                .background(Sakura.bad)
                                .bouncyClick(0.95f) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onDiscard()
                                }
                                .padding(vertical = 13.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(s.hrpDiscardConfirm, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = ThaiFont)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryBtn(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .shadow(if (enabled) 13.dp else 0.dp, RoundedCornerShape(19.dp), ambientColor = Color(0x525B6AF0), spotColor = Color(0x525B6AF0))
            .clip(RoundedCornerShape(19.dp))
            .background(
                if (enabled) Brush.linearGradient(listOf(Sakura.accent, Sakura.accent2))
                else Brush.linearGradient(listOf(Sakura.line, Sakura.line)),
            )
            .bouncyClick(0.96f) { if (enabled) onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = if (enabled) Color.White else Sakura.muted, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
    }
}

@Composable
private fun ChoiceCard(
    icon: SqIconKind,
    title: String,
    sub: String,
    bonusTag: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(if (selected) Sakura.accentSoft else Sakura.card)
            .border(1.5.dp, if (selected) Sakura.accent else Sakura.line, RoundedCornerShape(17.dp))
            .bouncyClick(0.975f, onClick)
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected) Sakura.accent else Sakura.bg2),
            contentAlignment = Alignment.Center,
        ) {
            SqIcon(icon, size = 19.dp, tint = if (selected) Color.White else Sakura.ink2)
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont)
                if (bonusTag != null) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(99.dp)).background(Sakura.good.copy(alpha = 0.16f)).padding(horizontal = 7.dp, vertical = 1.dp),
                    ) {
                        Text(bonusTag, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF059669), fontFamily = NumFont)
                    }
                }
            }
            Text(sub, fontSize = 11.5.sp, color = Sakura.muted, fontFamily = ThaiFont, lineHeight = 15.sp)
        }
        Box(
            Modifier
                .size(21.dp)
                .clip(CircleShape)
                .background(if (selected) Sakura.accent else Sakura.bg2),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) SqIcon(SqIconKind.Check, size = 11.dp, tint = Color.White, stroke = 3f)
        }
    }
}

@Composable
private fun SmallField(hint: String, value: String, multiline: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(hint, color = Sakura.muted, fontFamily = ThaiFont, fontSize = 12.5.sp) },
        textStyle = TextStyle(fontSize = 13.5.sp, fontFamily = ThaiFont, color = Sakura.ink),
        singleLine = !multiline,
        minLines = if (multiline) 2 else 1,
        shape = RoundedCornerShape(13.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Sakura.accent,
            unfocusedBorderColor = Sakura.line,
            focusedContainerColor = Sakura.card2,
            unfocusedContainerColor = Sakura.card2,
            cursorColor = Sakura.accent,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}
