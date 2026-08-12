package com.sacmaib80.sakuraq.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.sacmaib80.sakuraq.app.domain.DateUtil
import com.sacmaib80.sakuraq.app.domain.ScoreEngine
import com.sacmaib80.sakuraq.app.domain.categoryById
import com.sacmaib80.sakuraq.app.domain.fmtScore
import com.sacmaib80.sakuraq.app.domain.round1
import com.sacmaib80.sakuraq.app.ui.components.BezierChart
import com.sacmaib80.sakuraq.app.ui.components.SoftCard
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.StatChip
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.components.enterStagger
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import com.sacmaib80.sakuraq.app.ui.theme.NumFont
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont

/** หน้า History ตามเว็บ: แบนเนอร์มาสคอต + hero คะแนนรวม/สตรีค + range tabs + กราฟโค้ง + เลือกวันดู log */
@Composable
fun HistoryScreen(data: AppData) {
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current
    val todayKey = DateUtil.localDateKey()

    var rangeDays by rememberSaveable { mutableStateOf(7) } // 7 / 30 / 0 = All
    val effectiveDays = when {
        rangeDays > 0 -> rangeDays
        data.days.isEmpty() -> 7
        else -> {
            val first = data.days.keys.min()
            var d = 0
            var cursor = first
            while (cursor <= todayKey && d < 365) { d++; cursor = DateUtil.addDays(cursor, 1) }
            maxOf(7, d)
        }
    }

    val keys = remember(data.days, todayKey, effectiveDays) {
        (effectiveDays - 1 downTo 0).map { DateUtil.addDays(todayKey, -it.toLong()) }
    }
    val values = keys.map { ScoreEngine.dayEffective(data.days[it]) }

    val rangeTotal = round1(values.sum())
    val todayScore = ScoreEngine.dayEffective(data.days[todayKey])
    val streak = ScoreEngine.computeStreak(data.days, todayKey)

    var selectedKey by rememberSaveable { mutableStateOf(todayKey) }

    // สถิติเสริมในช่วง
    val rangeQuests = keys.sumOf { data.days[it]?.quests?.size ?: 0 }
    val bestIdx = values.indices.maxByOrNull { values[it] }
    val avgMin = run {
        val qs = keys.flatMap { data.days[it]?.quests ?: emptyList() }
        if (qs.isEmpty()) 0 else qs.sumOf { it.minutesSpent } / qs.size
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // ── แบนเนอร์มาสคอตเต็มความกว้าง (hpTopBanner) ──
        Image(
            painterResource(R.drawable.mascot_top),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .enterStagger(0),
        )

        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(14.dp))

            // ── HERO: คะแนนรวม + วันนี้ + สตรีค ──
            SoftCard(Modifier.fillMaxWidth().enterStagger(1)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(s.histTotalScore, fontSize = 12.5.sp, color = Sakura.muted, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
                    Spacer(Modifier.weight(1f))
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(Sakura.warn.copy(alpha = 0.13f))
                            .padding(horizontal = 11.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SqIcon(SqIconKind.Flame, size = 14.dp, tint = Sakura.warn)
                        Spacer(Modifier.width(4.dp))
                        Text("$streak", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Sakura.warn, fontFamily = NumFont)
                    }
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(fmtScore(rangeTotal), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NumFont, color = Sakura.ink)
                    Spacer(Modifier.width(9.dp))
                    if (todayScore > 0) {
                        Text(
                            s.histTodayBadge.tmpl("score" to fmtScore(todayScore)),
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669), fontFamily = ThaiFont,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(99.dp))
                                .background(Sakura.good.copy(alpha = 0.15f))
                                .padding(horizontal = 9.dp, vertical = 3.dp),
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // range tabs 7D / 30D / All
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    RangeTab("7D", rangeDays == 7) { rangeDays = 7 }
                    RangeTab("30D", rangeDays == 30) { rangeDays = 30 }
                    RangeTab("All", rangeDays == 0) { rangeDays = 0 }
                }

                Spacer(Modifier.height(12.dp))

                if (values.all { it == 0.0 }) {
                    Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text(s.histNoChartData, fontSize = 12.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
                    }
                } else {
                    BezierChart(
                        values = values,
                        maxValue = 100.0,
                        goalValue = 100.0,
                        animKey = "$rangeDays-${values.sum()}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    )
                    Spacer(Modifier.height(5.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(DateUtil.shortLabel(keys.first(), s.lang), fontSize = 10.sp, color = Sakura.muted, fontFamily = ThaiFont)
                        Text(DateUtil.shortLabel(keys.last(), s.lang), fontSize = 10.sp, color = Sakura.muted, fontFamily = ThaiFont)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── สถิติเสริม ──
            Row(Modifier.fillMaxWidth().enterStagger(2), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip(s.histTotalQuests, "$rangeQuests", null, Modifier.weight(1f))
                StatChip(s.histAvgSession, "$avgMin ${s.min}", null, Modifier.weight(1f))
                StatChip(
                    s.histBestDay,
                    if (bestIdx != null && values[bestIdx] > 0) fmtScore(values[bestIdx]) else "—",
                    if (bestIdx != null && values[bestIdx] > 0) DateUtil.shortLabel(keys[bestIdx], s.lang) else null,
                    Modifier.weight(1f),
                    valueColor = Sakura.accent,
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── เลือกวัน (แถบเลื่อนแนวนอน 14 วันล่าสุด) ──
            Text(s.histPickDay, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Sakura.ink2, fontFamily = ThaiFont, modifier = Modifier.enterStagger(3))
            Spacer(Modifier.height(9.dp))
        }

        val pickKeys = remember(todayKey) { (0..13).map { DateUtil.addDays(todayKey, -it.toLong()) } }
        val listState = rememberLazyListState()
        LazyRow(
            state = listState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(pickKeys) { _, key ->
                val active = key == selectedKey
                val has = (data.days[key]?.quests?.isNotEmpty() == true)
                Column(
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (active) Sakura.accent else Sakura.card)
                        .bouncyClick(0.92f) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedKey = key
                        }
                        .padding(horizontal = 13.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        DateUtil.weekdayLabel(key),
                        fontSize = 10.sp, fontFamily = ThaiFont, fontWeight = FontWeight.SemiBold,
                        color = if (active) Color.White.copy(alpha = 0.8f) else Sakura.muted,
                    )
                    Text(
                        "${DateUtil.dayOfMonth(key)}",
                        fontSize = 15.sp, fontFamily = NumFont, fontWeight = FontWeight.ExtraBold,
                        color = if (active) Color.White else Sakura.ink,
                    )
                    Box(
                        Modifier.size(5.dp).clip(CircleShape)
                            .background(
                                when {
                                    active && has -> Color.White
                                    has -> Sakura.accent
                                    else -> Color.Transparent
                                },
                            ),
                    )
                }
            }
        }

        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(12.dp))

            // ── log ของวันที่เลือก ──
            AnimatedContent(
                targetState = selectedKey,
                transitionSpec = { (fadeIn() + slideInVertically(spring(stiffness = 300f, dampingRatio = 0.8f)) { it / 8 }).togetherWith(fadeOut()) },
                label = "dayDetail",
            ) { key ->
                val day = data.days[key]
                val quests = day?.quests?.sortedByDescending { it.ts } ?: emptyList()
                SoftCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            if (key == todayKey) s.histToday else DateUtil.shortLabel(key, s.lang),
                            fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Sakura.ink, fontFamily = ThaiFont,
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            "+" + fmtScore(ScoreEngine.dayEffective(day)),
                            fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NumFont, color = Sakura.accent,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    if (quests.isEmpty()) {
                        Text(s.histNoQuestsDay, fontSize = 12.5.sp, color = Sakura.muted, fontFamily = ThaiFont)
                    } else {
                        quests.forEach { q ->
                            val cat = categoryById(q.category)
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(cat.color))
                                Spacer(Modifier.width(9.dp))
                                Text(q.questName, fontSize = 13.sp, color = Sakura.ink2, fontFamily = ThaiFont, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), maxLines = 1)
                                Text("${q.minutesSpent}${s.min.first()}", fontSize = 11.5.sp, color = Sakura.muted, fontFamily = NumFont)
                                Spacer(Modifier.width(8.dp))
                                Text("+" + fmtScore(q.questScore), fontSize = 12.5.sp, color = Sakura.accent, fontWeight = FontWeight.Bold, fontFamily = NumFont)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(110.dp))
        }
    }
}

@Composable
private fun RangeTab(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(if (active) Sakura.accent else Sakura.bg2)
            .bouncyClick(0.92f, onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            label, fontSize = 12.sp, fontFamily = NumFont, fontWeight = FontWeight.ExtraBold,
            color = if (active) Color.White else Sakura.ink2,
        )
    }
}
