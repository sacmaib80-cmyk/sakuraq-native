package com.sacmaib80.sakuraq.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.domain.CATEGORIES
import com.sacmaib80.sakuraq.app.domain.Category
import com.sacmaib80.sakuraq.app.domain.categoryById
import com.sacmaib80.sakuraq.app.domain.categoryDesc
import com.sacmaib80.sakuraq.app.ui.components.Pill
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.components.enterStagger
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.theme.NumFont
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont

private fun flowCatIcon(id: String): SqIconKind = when (id) {
    "Learning" -> SqIconKind.Book
    "Work/Project" -> SqIconKind.Briefcase
    "Physical" -> SqIconKind.Dumbbell
    else -> SqIconKind.Heart
}

/**
 * วิซาร์ด 2 สเต็ปตามแอปจริง: เลือกหมวด (การ์ดพาสเทล) → ตั้งชื่อ + เวลา (slider 5-60 snap)
 */
@Composable
fun QuestFlowScreen(
    recentNames: List<String>,
    onStart: (category: String, questName: String, plannedMin: Int) -> Unit,
    onClose: () -> Unit,
) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    var step by rememberSaveable { mutableIntStateOf(0) }
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var questName by rememberSaveable { mutableStateOf("") }
    var minutes by rememberSaveable { mutableIntStateOf(30) }

    BackHandler { if (step > 0) step-- else onClose() }

    Column(
        Modifier
            .fillMaxSize()
            .background(Sakura.bg)
            .padding(horizontal = 22.dp)
            .imePadding(),
    ) {
        Spacer(Modifier.height(16.dp))

        // ── ปุ่มย้อน + kicker ──
        Box(
            Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Sakura.card)
                .bouncyClick(0.9f) { if (step > 0) step-- else onClose() },
            contentAlignment = Alignment.Center,
        ) {
            SqIcon(SqIconKind.ChevronLeft, size = 18.dp, tint = Sakura.ink2, stroke = 2.4f)
        }
        Spacer(Modifier.height(14.dp))

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                val dir = if (targetState > initialState) 1 else -1
                (slideInHorizontally(spring(stiffness = 300f, dampingRatio = 0.8f)) { it / 3 * dir } + fadeIn())
                    .togetherWith(slideOutHorizontally { -it / 4 * dir } + fadeOut())
            },
            label = "flowStep",
            modifier = Modifier.weight(1f),
        ) { st ->
            if (st == 0) {
                CategoryStep(kicker = s.hqStartQuest, title = s.hqChooseCategory) { cat ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    category = cat.id
                    step = 1
                }
            } else {
                SetupStep(
                    kicker = s.hqQuestSetup,
                    cat = categoryById(category),
                    questName = questName,
                    minutes = minutes,
                    recentNames = recentNames,
                    onNameChange = { questName = it },
                    onMinutesChange = { minutes = it },
                    onStart = {
                        if (questName.trim().isNotEmpty()) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStart(category ?: CATEGORIES[0].id, questName, minutes)
                        }
                    },
                )
            }
        }
    }
}

// ── step 1: การ์ดหมวดพาสเทล (พอร์ต .hqCatBtn) ──

@Composable
private fun CategoryStep(kicker: String, title: String, onPick: (Category) -> Unit) {
    val s = LocalStrings.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(kicker, fontSize = 12.5.sp, color = Sakura.accent, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
        Spacer(Modifier.height(4.dp))
        Text(title, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink, fontFamily = ThaiFont, lineHeight = 33.sp)
        Spacer(Modifier.height(18.dp))

        CATEGORIES.forEachIndexed { i, cat ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .enterStagger(i)
                    .shadow(6.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x14464E8C), spotColor = Color(0x1A464E8C))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(cat.tileStart, cat.tileEnd)))
                    .bouncyClick(0.97f) { onPick(cat) }
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x8CFFFFFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    SqIcon(flowCatIcon(cat.id), size = 23.dp, tint = cat.tileInk)
                }
                Spacer(Modifier.width(13.dp))
                Column(Modifier.weight(1f)) {
                    Text(cat.label, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = cat.tileInk, fontFamily = ThaiFont)
                    Text(
                        categoryDesc(cat, s),
                        fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold,
                        color = cat.tileInk.copy(alpha = 0.72f), fontFamily = ThaiFont, lineHeight = 15.sp,
                    )
                }
                Box(
                    Modifier.size(28.dp).clip(CircleShape).background(Color(0x99FFFFFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    SqIcon(SqIconKind.ChevronRight, size = 13.dp, tint = cat.tileInk, stroke = 2.6f)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(80.dp))
    }
}

// ── step 2: ชื่อ + เวลา (ตาม hqStepSetup) ──

@Composable
private fun SetupStep(
    kicker: String,
    cat: Category,
    questName: String,
    minutes: Int,
    recentNames: List<String>,
    onNameChange: (String) -> Unit,
    onMinutesChange: (Int) -> Unit,
    onStart: () -> Unit,
) {
    val s = LocalStrings.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(kicker, fontSize = 12.5.sp, color = Sakura.accent, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
        Spacer(Modifier.height(4.dp))
        Text(s.hqNameQuest, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink, fontFamily = ThaiFont)
        Spacer(Modifier.height(8.dp))
        Pill(text = cat.label, bg = cat.soft, fg = cat.gradEnd)

        Spacer(Modifier.height(16.dp))
        Text(s.hqQuestNameLabel, fontSize = 12.5.sp, color = Sakura.ink2, fontWeight = FontWeight.SemiBold, fontFamily = ThaiFont)
        Spacer(Modifier.height(7.dp))
        OutlinedTextField(
            value = questName,
            onValueChange = { if (it.length <= 60) onNameChange(it) },
            placeholder = { Text(s.hqNamePh, color = Sakura.muted, fontFamily = ThaiFont) },
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = ThaiFont, color = Sakura.ink, fontWeight = FontWeight.SemiBold),
            singleLine = true,
            shape = Sakura.innerShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Sakura.accent,
                unfocusedBorderColor = Sakura.line,
                focusedContainerColor = Sakura.card,
                unfocusedContainerColor = Sakura.card,
                cursorColor = Sakura.accent,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        if (recentNames.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                recentNames.take(3).forEach { name ->
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(Sakura.card)
                            .bouncyClick(0.94f) { onNameChange(name) }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                    ) {
                        Text(name, fontSize = 12.sp, color = Sakura.ink2, fontFamily = ThaiFont, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(s.hqTimeMin, fontSize = 12.5.sp, color = Sakura.ink2, fontWeight = FontWeight.SemiBold, fontFamily = ThaiFont)
        Spacer(Modifier.height(4.dp))

        // ตัวเลขนาทีใหญ่ + slider 5-60 snap ตามเว็บ
        Row(verticalAlignment = Alignment.Bottom) {
            AnimatedContent(
                targetState = minutes,
                transitionSpec = {
                    (fadeIn(spring(stiffness = 500f)) + slideInHorizontally { it / 10 }).togetherWith(fadeOut())
                },
                label = "min",
            ) { m ->
                Text("$m", fontSize = 46.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.accent, fontFamily = NumFont)
            }
            Text(
                " " + s.min, fontSize = 15.sp, color = Sakura.muted, fontFamily = ThaiFont,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 9.dp),
            )
        }
        Slider(
            value = minutes.toFloat(),
            onValueChange = { onMinutesChange((it / 5f).toInt() * 5) },
            valueRange = 5f..60f,
            colors = SliderDefaults.colors(
                thumbColor = Sakura.accent,
                activeTrackColor = Sakura.accent,
                inactiveTrackColor = Sakura.line,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf(5, 15, 25, 30, 45, 60).forEach { p ->
                Text(
                    "${p}m",
                    fontSize = 11.sp, fontFamily = NumFont,
                    fontWeight = if (p == minutes) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = if (p == minutes) Sakura.accent else Sakura.muted,
                    modifier = Modifier.bouncyClick(0.9f) { onMinutesChange(p) },
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        val ready = questName.trim().isNotEmpty()
        Box(
            Modifier
                .fillMaxWidth()
                .shadow(if (ready) 14.dp else 0.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x525B6AF0), spotColor = Color(0x525B6AF0))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (ready) Brush.linearGradient(listOf(Sakura.accent, Sakura.accent2))
                    else Brush.linearGradient(listOf(Sakura.line, Sakura.line)),
                )
                .bouncyClick(0.96f, onStart)
                .padding(vertical = 17.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SqIcon(SqIconKind.Play, size = 16.dp, tint = if (ready) Color.White else Sakura.muted)
                Spacer(Modifier.width(8.dp))
                Text(
                    s.hqStartBtn,
                    color = if (ready) Color.White else Sakura.muted,
                    fontSize = 16.5.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont,
                )
            }
        }
        Spacer(Modifier.height(90.dp))
    }
}
