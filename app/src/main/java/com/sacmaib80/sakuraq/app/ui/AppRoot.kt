package com.sacmaib80.sakuraq.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sacmaib80.sakuraq.app.domain.DateUtil
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.i18n.strsOf
import com.sacmaib80.sakuraq.app.ui.screens.CelebrationOverlay
import com.sacmaib80.sakuraq.app.ui.screens.DiceOverlay
import com.sacmaib80.sakuraq.app.ui.screens.HistoryScreen
import com.sacmaib80.sakuraq.app.ui.screens.HomeScreen
import com.sacmaib80.sakuraq.app.ui.screens.MenuScreen
import com.sacmaib80.sakuraq.app.ui.screens.QuestFlowScreen
import com.sacmaib80.sakuraq.app.ui.screens.ReportScreen
import com.sacmaib80.sakuraq.app.ui.screens.RunScreen
import com.sacmaib80.sakuraq.app.ui.screens.ScheduleScreen
import com.sacmaib80.sakuraq.app.ui.screens.TodayScreen
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont

@Composable
fun AppRoot(vm: AppViewModel = viewModel()) {
    val data by vm.app.collectAsState()
    val loaded by vm.loaded.collectAsState()
    val tab by vm.tab.collectAsState()
    val overlay by vm.overlay.collectAsState()
    val nowMs by vm.nowMs.collectAsState()
    val haptic = LocalHapticFeedback.current

    androidx.compose.runtime.LaunchedEffect(loaded) { if (loaded) vm.prunePlans() }

    CompositionLocalProvider(LocalStrings provides strsOf(data.prefs.language)) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Sakura.bgBrush),
        ) {
            if (!loaded) return@Box

            val topInset = WindowInsets.statusBars.asPaddingValues()
            val bottomInset = WindowInsets.navigationBars.asPaddingValues()

            // ── แท็บหลัก ──
            Box(Modifier.fillMaxSize().padding(topInset)) {
                AnimatedContent(
                    targetState = tab,
                    transitionSpec = {
                        (fadeIn(spring(stiffness = 300f)) + slideInVertically { it / 40 }).togetherWith(fadeOut())
                    },
                    label = "tab",
                ) { t ->
                    when (t) {
                        Tab.HOME -> HomeScreen(
                            data = data,
                            onStartQuest = { vm.openQuestFlow() },
                            onOpenDice = { vm.openDice() },
                            onGoToday = { vm.setTab(Tab.TODAY) },
                            onEditName = vm::setProfileName,
                            todayPlanCount = data.plans.count { it.dateKey == DateUtil.localDateKey() && !it.done },
                            onGoPlan = { vm.setTab(Tab.PLAN) },
                        )
                        Tab.TODAY -> TodayScreen(
                            data = data,
                            onDelete = vm::deleteQuest,
                            onClearToday = vm::clearToday,
                            onStartQuest = { vm.setTab(Tab.HOME); vm.openQuestFlow() },
                        )
                        Tab.PLAN -> ScheduleScreen(
                            plans = data.plans,
                            onAdd = vm::addPlan,
                            onDelete = vm::deletePlan,
                            onToggleDone = vm::togglePlanDone,
                            onStart = vm::startFromPlan,
                        )
                        Tab.MENU -> MenuScreen(
                            language = data.prefs.language,
                            versionName = "1.1.0",
                            onSetLanguage = vm::setLanguage,
                            onClearToday = vm::clearToday,
                            onResetAll = vm::resetAll,
                        )
                        else -> HistoryScreen(data = data)
                    }
                }
            }

            // ── bottom nav (ซ่อนเมื่อมี overlay) ──
            AnimatedVisibility(
                visible = overlay is Overlay.None,
                enter = slideInVertically(spring(stiffness = 300f, dampingRatio = 0.85f)) { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                BottomNav(
                    tab = tab,
                    bottomPadding = bottomInset.calculateBottomPadding(),
                    onSelect = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        vm.setTab(it)
                    },
                )
            }

            // ── overlays ──
            AnimatedVisibility(
                visible = overlay is Overlay.QuestFlow,
                enter = slideInVertically(spring(stiffness = 280f, dampingRatio = 0.86f)) { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
                Box(Modifier.fillMaxSize().background(Sakura.bg).padding(topInset)) {
                    QuestFlowScreen(
                        recentNames = data.questNameHistory,
                        onStart = { cat, name, mins -> vm.startQuest(cat, name, mins) },
                        onClose = { vm.closeOverlay() },
                    )
                }
            }

            AnimatedVisibility(
                visible = overlay is Overlay.Run,
                enter = scaleIn(initialScale = 1.05f, animationSpec = spring(stiffness = 260f, dampingRatio = 0.9f)) + fadeIn(),
                exit = fadeOut(),
            ) {
                val sessionN = (data.days[DateUtil.localDateKey()]?.quests?.size ?: 0) + 1
                RunScreen(
                    session = data.session,
                    nowMs = nowMs,
                    sessionN = sessionN,
                    onPause = vm::pauseQuest,
                    onResume = vm::resumeQuest,
                    onExtend = vm::extendQuest,
                    onStop = vm::stopQuest,
                )
            }

            AnimatedVisibility(
                visible = overlay is Overlay.Report,
                enter = slideInVertically(spring(stiffness = 260f, dampingRatio = 0.88f)) { it } + fadeIn(),
                exit = slideOutVertically { it / 2 } + fadeOut(),
            ) {
                Box(Modifier.fillMaxSize().padding(topInset)) {
                    ReportScreen(
                        data = data,
                        seedMinutes = vm.elapsedMinutes(),
                        onDiscard = vm::discardReport,
                        onSave = vm::submitReport,
                    )
                }
            }

            AnimatedVisibility(
                visible = overlay is Overlay.Dice,
                enter = fadeIn(spring(stiffness = 380f)) + scaleIn(initialScale = 0.92f, animationSpec = spring(stiffness = 300f, dampingRatio = 0.8f)),
                exit = fadeOut() + scaleOut(targetScale = 0.95f),
            ) {
                DiceOverlay(
                    data = data,
                    onRoll = vm::rollDice,
                    onStart = vm::startDiceOffer,
                    onClear = vm::clearDiceOffer,
                    onClose = vm::closeOverlay,
                )
            }

            val celebration = overlay
            AnimatedVisibility(
                visible = celebration is Overlay.Celebration,
                enter = fadeIn(spring(stiffness = 400f)),
                exit = fadeOut() + scaleOut(targetScale = 1.05f),
            ) {
                (celebration as? Overlay.Celebration)?.let { c ->
                    CelebrationOverlay(result = c.result, onDismiss = { vm.closeOverlay() })
                }
            }
        }
    }
}

private data class NavEntry(val tab: Tab, val icon: SqIconKind, val label: String)

private val NAV_ENTRIES = listOf(
    NavEntry(Tab.HOME, SqIconKind.Home, "Home"),
    NavEntry(Tab.TODAY, SqIconKind.Sun, "Today"),
    NavEntry(Tab.HISTORY, SqIconKind.Chart, "History"),
    NavEntry(Tab.PLAN, SqIconKind.Calendar, "Plan"),
    NavEntry(Tab.MENU, SqIconKind.Menu, "Menu"),
)

@Composable
private fun BottomNav(
    tab: Tab,
    bottomPadding: androidx.compose.ui.unit.Dp,
    onSelect: (Tab) -> Unit,
) {
    Row(
        Modifier
            .padding(bottom = bottomPadding + 12.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x1C464E8C), spotColor = Color(0x2E464E8C))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xF7FFFFFF))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        NAV_ENTRIES.forEach { e ->
            NavItem(e.icon, e.label, tab == e.tab) { onSelect(e.tab) }
        }
    }
}

@Composable
private fun NavItem(icon: SqIconKind, label: String, active: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(17.dp))
            .background(if (active) Sakura.accentSoft else Color.Transparent)
            .bouncyClick(0.9f, onClick)
            .padding(horizontal = 13.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SqIcon(icon, size = 19.dp, tint = if (active) Sakura.accent else Sakura.muted, stroke = 2.1f)
        AnimatedVisibility(visible = active) {
            Text(label, fontSize = 12.5.sp, color = Sakura.accent, fontWeight = FontWeight.Bold, fontFamily = ThaiFont)
        }
    }
}
