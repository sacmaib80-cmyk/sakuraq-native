package com.sacmaib80.sakuraq.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.data.Plan
import com.sacmaib80.sakuraq.app.domain.CATEGORIES
import com.sacmaib80.sakuraq.app.domain.DateUtil
import com.sacmaib80.sakuraq.app.domain.categoryById
import com.sacmaib80.sakuraq.app.ui.PlanError
import com.sacmaib80.sakuraq.app.ui.components.SoftCard
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.components.enterStagger
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import com.sacmaib80.sakuraq.app.ui.theme.NumFont
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont

private fun schedCatIcon(id: String): SqIconKind = when (id) {
    "Learning" -> SqIconKind.Book
    "Work/Project" -> SqIconKind.Briefcase
    "Physical" -> SqIconKind.Dumbbell
    else -> SqIconKind.Heart
}

/** แท็บ Plan ตามเว็บ: timeline วันนี้ (เริ่ม/เสร็จ) + เขียนการ์ดพรุ่งนี้ + timeline พรุ่งนี้ */
@Composable
fun ScheduleScreen(
    plans: List<Plan>,
    onAdd: (name: String, category: String, durationMin: Int, startHHMM: String, dateKey: String) -> PlanError?,
    onDelete: (String) -> Unit,
    onToggleDone: (String) -> Unit,
    onStart: (String) -> Unit,
) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    val todayKey = DateUtil.localDateKey()
    val tomorrowKey = DateUtil.addDays(todayKey, 1)

    val todayPlans = plans.filter { it.dateKey == todayKey }.sortedBy { DateUtil.parseHHMMtoMin(it.startHHMM) }
    val tomorrowPlans = plans.filter { it.dateKey == tomorrowKey }.sortedBy { DateUtil.parseHHMMtoMin(it.startHHMM) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .imePadding(),
    ) {
        Spacer(Modifier.height(18.dp))

        // ── หัว: ชื่อ + วันที่พรุ่งนี้ ──
        Row(Modifier.enterStagger(0), verticalAlignment = Alignment.Bottom) {
            Text(s.schedTitle, fontSize = 25.sp, color = Sakura.ink, fontWeight = FontWeight.ExtraBold, fontFamily = ThaiFont)
            Spacer(Modifier.weight(1f))
            Text(
                "${s.schedDays[DateUtil.weekdayIndex(tomorrowKey)]} ${DateUtil.dayOfMonth(tomorrowKey)}/${DateUtil.monthValue(tomorrowKey)}",
                fontSize = 12.5.sp, color = Sakura.muted, fontFamily = ThaiFont, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 5.dp),
            )
        }

        Spacer(Modifier.height(14.dp))

        // ── วันนี้ ──
        if (todayPlans.isNotEmpty()) {
            SectionLabel(s.schedToday)
            Spacer(Modifier.height(8.dp))
            todayPlans.forEachIndexed { i, p ->
                PlanRow(p = p, stagger = i + 1, isToday = true, onDelete = onDelete, onToggleDone = onToggleDone, onStart = onStart)
                Spacer(Modifier.height(9.dp))
            }
            Spacer(Modifier.height(10.dp))
        }

        // ── ฟอร์มเขียนการ์ด ──
        WriteCardForm(onAdd = { n, c, d, t -> onAdd(n, c, d, t, tomorrowKey) })

        Spacer(Modifier.height(16.dp))

        // ── พรุ่งนี้ ──
        SectionLabel(s.schedTomorrow)
        Spacer(Modifier.height(8.dp))
        if (tomorrowPlans.isEmpty()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .enterStagger(2)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Sakura.card2)
                    .padding(vertical = 22.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(s.schedEmptyTomorrow, fontSize = 12.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
            }
        } else {
            tomorrowPlans.forEachIndexed { i, p ->
                PlanRow(p = p, stagger = (i + 2).coerceAtMost(7), isToday = false, onDelete = onDelete, onToggleDone = onToggleDone, onStart = onStart)
                Spacer(Modifier.height(9.dp))
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(Sakura.accent))
        Spacer(Modifier.width(7.dp))
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink2, fontFamily = ThaiFont)
    }
}

/** การ์ดแผน 1 ใบ: เวลาซ้าย + จุดสี + เนื้อหา + ปุ่ม (วันนี้: เริ่ม/เสร็จ, พรุ่งนี้: ลบ) */
@Composable
private fun PlanRow(
    p: Plan,
    stagger: Int,
    isToday: Boolean,
    onDelete: (String) -> Unit,
    onToggleDone: (String) -> Unit,
    onStart: (String) -> Unit,
) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    val cat = categoryById(p.category)

    Row(Modifier.fillMaxWidth().enterStagger(stagger), verticalAlignment = Alignment.CenterVertically) {
        // เวลา
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(44.dp)) {
            Text(p.startHHMM, fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink2, fontFamily = NumFont)
            Text(DateUtil.endHHMM(p.startHHMM, p.durationMin), fontSize = 10.5.sp, color = Sakura.muted, fontFamily = NumFont)
        }
        Spacer(Modifier.width(9.dp))
        Box(
            Modifier.size(11.dp).clip(CircleShape).background(cat.color)
                .border(2.5.dp, cat.color.copy(alpha = 0.25f), CircleShape),
        )
        Spacer(Modifier.width(9.dp))
        SoftCard(
            Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            padding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        p.name,
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont,
                        color = if (p.done) Sakura.muted else Sakura.ink, maxLines = 1,
                    )
                    Text(
                        "${cat.label} • " + s.schedMinUnit.tmpl("min" to p.durationMin),
                        fontSize = 11.sp, color = Sakura.muted, fontFamily = ThaiFont,
                    )
                }
                if (isToday) {
                    // เสร็จ (toggle)
                    Box(
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (p.done) Sakura.good else Sakura.bg2)
                            .bouncyClick(0.86f) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleDone(p.id)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        SqIcon(SqIconKind.Check, size = 13.dp, tint = if (p.done) Color.White else Sakura.muted, stroke = 2.8f)
                    }
                    if (!p.done) {
                        Spacer(Modifier.width(7.dp))
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(Brush.linearGradient(listOf(Sakura.accent, Sakura.accent2)))
                                .bouncyClick(0.93f) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onStart(p.id)
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SqIcon(SqIconKind.Play, size = 11.dp, tint = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text(s.schedStart, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = ThaiFont)
                        }
                    }
                } else {
                    Box(
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Sakura.bg2)
                            .bouncyClick(0.85f) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onDelete(p.id)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        SqIcon(SqIconKind.Cross, size = 11.dp, tint = Sakura.muted, stroke = 2.4f)
                    }
                }
            }
        }
    }
}

/** ฟอร์มเขียนการ์ดพรุ่งนี้ (พับได้ตาม schedNotecard): หมวด / ชื่อ / เวลาที่ใช้ / เริ่มกี่โมง */
@Composable
private fun WriteCardForm(onAdd: (name: String, category: String, durationMin: Int, startHHMM: String) -> PlanError?) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    var open by rememberSaveable { mutableStateOf(false) }
    var cat by rememberSaveable { mutableStateOf(CATEGORIES[0].id) }
    var name by rememberSaveable { mutableStateOf("") }
    var dur by rememberSaveable { mutableIntStateOf(20) }
    var hour by rememberSaveable { mutableIntStateOf(9) }
    var minute by rememberSaveable { mutableIntStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    SoftCard(Modifier.fillMaxWidth().enterStagger(1)) {
        Row(
            Modifier.fillMaxWidth().bouncyClick(0.985f) { open = !open },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SqIcon(SqIconKind.Pencil, size = 17.dp, tint = Sakura.accent)
            Spacer(Modifier.width(9.dp))
            Text(s.schedWriteCard, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont, modifier = Modifier.weight(1f))
            val chev by animateFloatAsState(if (open) 90f else 0f, spring(stiffness = 300f), label = "chev")
            SqIcon(SqIconKind.ChevronRight, size = 15.dp, tint = Sakura.muted, modifier = Modifier.rotate(chev))
        }

        AnimatedVisibility(
            visible = open,
            enter = expandVertically(spring(stiffness = 260f, dampingRatio = 0.85f)) + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column {
                Spacer(Modifier.height(14.dp))

                Text(s.schedCategory, fontSize = 12.sp, color = Sakura.muted, fontWeight = FontWeight.SemiBold, fontFamily = ThaiFont)
                Spacer(Modifier.height(7.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    CATEGORIES.forEach { c ->
                        val active = cat == c.id
                        Column(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(13.dp))
                                .background(if (active) c.soft else Sakura.bg2)
                                .border(1.5.dp, if (active) c.color else Color.Transparent, RoundedCornerShape(13.dp))
                                .bouncyClick(0.93f) { cat = c.id }
                                .padding(vertical = 9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            SqIcon(schedCatIcon(c.id), size = 17.dp, tint = if (active) c.gradEnd else Sakura.muted)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(s.schedQuestName, fontSize = 12.sp, color = Sakura.muted, fontWeight = FontWeight.SemiBold, fontFamily = ThaiFont)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 40) name = it },
                    placeholder = { Text(s.schedNamePh, color = Sakura.muted, fontFamily = ThaiFont) },
                    textStyle = TextStyle(fontSize = 14.sp, fontFamily = ThaiFont, color = Sakura.ink, fontWeight = FontWeight.SemiBold),
                    singleLine = true,
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

                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(s.schedDuration, fontSize = 12.sp, color = Sakura.muted, fontWeight = FontWeight.SemiBold, fontFamily = ThaiFont)
                    Spacer(Modifier.weight(1f))
                    Text("$dur ${s.min}", fontSize = 14.sp, color = Sakura.accent, fontWeight = FontWeight.ExtraBold, fontFamily = NumFont)
                }
                Slider(
                    value = dur.toFloat(),
                    onValueChange = { dur = (it / 5f).toInt() * 5 },
                    valueRange = 5f..60f,
                    colors = SliderDefaults.colors(
                        thumbColor = Sakura.accent,
                        activeTrackColor = Sakura.accent,
                        inactiveTrackColor = Sakura.line,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(s.schedStartTime, fontSize = 12.sp, color = Sakura.muted, fontWeight = FontWeight.SemiBold, fontFamily = ThaiFont)
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    TimeWheel(value = hour, range = 0..23) { hour = it }
                    Text(" : ", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink, fontFamily = NumFont)
                    TimeWheel(value = minute, range = 0..55, step = 5) { minute = it }
                }

                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, fontSize = 12.sp, color = Sakura.bad, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
                }

                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Sakura.bg2)
                            .bouncyClick(0.95f) { name = ""; error = null }
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                    ) {
                        Text(s.schedClear, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Sakura.muted, fontFamily = ThaiFont)
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (name.trim().isNotEmpty()) Brush.linearGradient(listOf(Sakura.accent, Sakura.accent2))
                                else Brush.linearGradient(listOf(Sakura.line, Sakura.line)),
                            )
                            .bouncyClick(0.96f) {
                                if (name.trim().isEmpty()) return@bouncyClick
                                val hhmm = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                                val err = onAdd(name, cat, dur, hhmm)
                                error = when (err) {
                                    PlanError.NAME_EXISTS -> s.schedNameExists.tmpl("name" to name.trim())
                                    PlanError.OVERLAP -> s.schedOverlap.tmpl("name" to name.trim(), "range" to "$hhmm–${DateUtil.endHHMM(hhmm, dur)}")
                                    null -> {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        name = ""
                                        open = false
                                        null
                                    }
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            s.save, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont,
                            color = if (name.trim().isNotEmpty()) Color.White else Sakura.muted,
                        )
                    }
                }
            }
        }
    }
}

/** ตัวปรับเวลาแบบกดขึ้น/ลง */
@Composable
private fun TimeWheel(value: Int, range: IntRange, step: Int = 1, onChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(30.dp).clip(CircleShape).background(Sakura.bg2)
                .bouncyClick(0.85f) {
                    val next = value + step
                    onChange(if (next > range.last) range.first else next)
                },
            contentAlignment = Alignment.Center,
        ) {
            SqIcon(SqIconKind.Plus, size = 13.dp, tint = Sakura.accent, stroke = 2.6f)
        }
        Text(
            value.toString().padStart(2, '0'),
            fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink, fontFamily = NumFont,
            modifier = Modifier.padding(vertical = 3.dp),
        )
        Box(
            Modifier.size(30.dp).clip(CircleShape).background(Sakura.bg2)
                .bouncyClick(0.85f) {
                    val next = value - step
                    onChange(if (next < range.first) range.last - (range.last % step) else next)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("−", fontSize = 17.sp, color = Sakura.accent, fontWeight = FontWeight.Bold)
        }
    }
}
