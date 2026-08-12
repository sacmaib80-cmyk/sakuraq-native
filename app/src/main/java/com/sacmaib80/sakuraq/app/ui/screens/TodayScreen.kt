package com.sacmaib80.sakuraq.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.R
import com.sacmaib80.sakuraq.app.data.AppData
import com.sacmaib80.sakuraq.app.data.QuestEntry
import com.sacmaib80.sakuraq.app.domain.DateUtil
import com.sacmaib80.sakuraq.app.domain.ScoreEngine
import com.sacmaib80.sakuraq.app.domain.categoryById
import com.sacmaib80.sakuraq.app.domain.fmtScore
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
import kotlinx.coroutines.launch

private fun tpCatIcon(id: String?): SqIconKind = when (id) {
    "Learning" -> SqIconKind.Book
    "Work/Project" -> SqIconKind.Briefcase
    "Physical" -> SqIconKind.Dumbbell
    else -> SqIconKind.Heart
}

/** หน้า Today ตามเว็บ: hero การ์ดเกรด S-F + มาสคอต + เส้นทางวันนี้ + ล้างวันนี้ (แตะ 2 ครั้ง) */
@Composable
fun TodayScreen(
    data: AppData,
    onDelete: (dateKey: String, ts: Long) -> Unit,
    onClearToday: () -> Unit = {},
    onStartQuest: () -> Unit = {},
) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    val todayKey = DateUtil.localDateKey()
    val day = data.days[todayKey]
    val quests = day?.quests?.sortedByDescending { it.ts } ?: emptyList()
    val total = ScoreEngine.dayEffective(day)
    val (grade, gradeColor) = ScoreEngine.tpGrade(total)
    val pct = total.coerceIn(0.0, 100.0).toInt()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(18.dp))

        // ── หัว: Today + วันที่เต็ม ──
        Row(Modifier.enterStagger(0), verticalAlignment = Alignment.Bottom) {
            Text(s.tpTitle, fontSize = 25.sp, color = Sakura.ink, fontWeight = FontWeight.ExtraBold, fontFamily = ThaiFont)
            Spacer(Modifier.width(8.dp))
            SqIcon(SqIconKind.Sparkle, size = 16.dp, tint = Sakura.accent3, modifier = Modifier.padding(bottom = 6.dp))
            Spacer(Modifier.weight(1f))
            val wd = s.tpDays[DateUtil.weekdayIndex(todayKey)]
            val mo = s.tpMonths[DateUtil.monthValue(todayKey) - 1]
            Text(
                "$wd ${DateUtil.dayOfMonth(todayKey)} $mo",
                fontSize = 12.sp, color = Sakura.muted, fontFamily = ThaiFont, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 5.dp),
            )
        }

        Spacer(Modifier.height(14.dp))

        // ── HERO: คะแนน + เกรด + มาสคอต ──
        Box(Modifier.fillMaxWidth().enterStagger(1)) {
            SoftCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SqIcon(SqIconKind.Sparkle, size = 13.dp, tint = Sakura.accent)
                    Spacer(Modifier.width(6.dp))
                    Text(s.tpTodayScore, fontSize = 12.sp, color = Sakura.muted, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(fmtScore(total), fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NumFont, color = Sakura.ink)
                    Spacer(Modifier.width(6.dp))
                    Text(s.tpPts, fontSize = 14.sp, color = Sakura.muted, fontFamily = ThaiFont, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 14.dp))
                    Spacer(Modifier.weight(1f))
                    // เกรดใหญ่สีตามเกณฑ์เว็บ
                    AnimatedContent(
                        targetState = grade,
                        transitionSpec = { (scaleIn(initialScale = 0.5f, animationSpec = spring(stiffness = 400f, dampingRatio = 0.55f)) + fadeIn()).togetherWith(fadeOut()) },
                        label = "grade",
                    ) { g ->
                        Box(
                            Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(gradeColor)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(g, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, fontFamily = NumFont)
                        }
                    }
                    Spacer(Modifier.width(64.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SqIcon(SqIconKind.Calendar, size = 13.dp, tint = Sakura.ink2)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        s.tpQuestCount.tmpl("n" to quests.size),
                        fontSize = 12.sp, color = Sakura.ink2, fontFamily = ThaiFont, fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(10.dp))
                val frac by animateFloatAsState((total / 100.0).toFloat().coerceIn(0f, 1f), spring(stiffness = 80f, dampingRatio = 0.85f), label = "tpTrack")
                Box(
                    Modifier.fillMaxWidth().height(11.dp).clip(RoundedCornerShape(99.dp)).background(Sakura.bg2),
                ) {
                    if (frac > 0.005f) {
                        Box(
                            Modifier
                                .fillMaxWidth(frac)
                                .height(11.dp)
                                .clip(RoundedCornerShape(99.dp))
                                .background(Brush.horizontalGradient(listOf(Sakura.accent, Sakura.accent3))),
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(s.tpDailyGoal.tmpl("pct" to pct), fontSize = 11.sp, color = Sakura.muted, fontFamily = ThaiFont)
            }
            // มาสคอตโผล่มุมขวาบนของ hero (ตาม tpHeroMascot)
            Image(
                painterResource(R.drawable.mascot_today),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 26.dp, end = 2.dp)
                    .size(112.dp),
            )
        }

        Spacer(Modifier.height(18.dp))

        // ── Journey ──
        Row(Modifier.enterStagger(2), verticalAlignment = Alignment.CenterVertically) {
            SqIcon(SqIconKind.Sparkle, size = 13.dp, tint = Sakura.accent)
            Spacer(Modifier.width(7.dp))
            Text(s.tpJourney, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.ink, fontFamily = ThaiFont)
        }
        Spacer(Modifier.height(10.dp))

        if (quests.isEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .enterStagger(3)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Sakura.card2)
                    .bouncyClick(0.98f, onStartQuest)
                    .padding(vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SqIcon(SqIconKind.Plus, size = 26.dp, tint = Sakura.muted)
                Spacer(Modifier.height(8.dp))
                Text(s.tpEmptyTitle, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Sakura.ink2, fontFamily = ThaiFont)
                Text(s.tpEmptyDesc, fontSize = 12.sp, color = Sakura.muted, fontFamily = ThaiFont)
            }
        } else {
            quests.forEachIndexed { i, q ->
                TodayQuestCard(q = q, stagger = (i + 3).coerceAtMost(8)) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDelete(todayKey, q.ts)
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(14.dp))

        // ── Reset Today (แตะ 2 ครั้ง) ──
        if (quests.isNotEmpty()) {
            var armed by remember { mutableStateOf(false) }
            LaunchedEffect(armed) {
                if (armed) { kotlinx.coroutines.delay(2600); armed = false }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (armed) Sakura.bad.copy(alpha = 0.13f) else Sakura.card2)
                    .bouncyClick(0.97f) {
                        if (armed) { armed = false; haptic.performHapticFeedback(HapticFeedbackType.LongPress); onClearToday() }
                        else armed = true
                    }
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                SqIcon(SqIconKind.Trash, size = 15.dp, tint = if (armed) Sakura.bad else Sakura.muted)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (armed) s.tpResetConfirm else s.tpResetToday,
                    fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont,
                    color = if (armed) Sakura.bad else Sakura.muted,
                )
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun TodayQuestCard(q: QuestEntry, stagger: Int, onDelete: () -> Unit) {
    val s = LocalStrings.current
    val cat = categoryById(q.category)
    var visible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = visible,
        exit = slideOutHorizontally(spring(stiffness = 300f)) { it / 3 } + fadeOut() + shrinkVertically(spring(stiffness = 260f, dampingRatio = 0.9f)),
    ) {
        SoftCard(
            Modifier.fillMaxWidth().enterStagger(stagger),
            shape = Sakura.innerShape,
            padding = androidx.compose.foundation.layout.PaddingValues(13.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Brush.linearGradient(listOf(cat.gradStart, cat.gradEnd))),
                    contentAlignment = Alignment.Center,
                ) {
                    SqIcon(tpCatIcon(q.category), size = 19.dp, tint = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(q.questName, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont, maxLines = 1)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "${q.minutesSpent} ${s.min} • ${DateUtil.timeAgo(q.ts, s)}",
                            fontSize = 11.5.sp, color = Sakura.muted, fontFamily = ThaiFont,
                        )
                        if (q.bonus > 0) {
                            Text("+${q.bonus}", fontSize = 11.sp, color = Color(0xFF059669), fontWeight = FontWeight.ExtraBold, fontFamily = NumFont)
                        }
                    }
                }
                Text("+" + fmtScore(q.questScore), fontSize = 16.5.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NumFont, color = Sakura.accent)
                Spacer(Modifier.width(10.dp))
                Box(
                    Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Sakura.bg2)
                        .bouncyClick(0.85f) {
                            visible = false
                            scope.launch {
                                kotlinx.coroutines.delay(240)
                                onDelete()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    SqIcon(SqIconKind.Cross, size = 11.dp, tint = Sakura.muted, stroke = 2.4f)
                }
            }
        }
    }
}
