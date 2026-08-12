package com.sacmaib80.sakuraq.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.R
import com.sacmaib80.sakuraq.app.data.AppData
import com.sacmaib80.sakuraq.app.domain.DateUtil
import com.sacmaib80.sakuraq.app.domain.Greetings
import com.sacmaib80.sakuraq.app.domain.ScoreEngine
import com.sacmaib80.sakuraq.app.domain.fmtScore
import com.sacmaib80.sakuraq.app.ui.components.AmbientPetals
import com.sacmaib80.sakuraq.app.ui.components.Mascot
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.components.enterStagger
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import com.sacmaib80.sakuraq.app.ui.theme.NumFont
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont
import kotlin.math.roundToInt

/**
 * หน้า Home ตามเลย์เอาต์แอปจริง: ฉากห้อง (room-bg) + speech card คำทัก +
 * การ์ดแต้มวันนี้ + ปุ่ม "ไปต่อกันมั้ย?" + มาสคอต + ลูกเต๋าลากได้
 */
@Composable
fun HomeScreen(
    data: AppData,
    onStartQuest: () -> Unit,
    onOpenDice: () -> Unit = {},
    onGoToday: () -> Unit = {},
    onEditName: (String) -> Unit = {},
    todayPlanCount: Int = 0,
    onGoPlan: () -> Unit = {},
) {
    val s = LocalStrings.current
    val todayKey = DateUtil.localDateKey()
    val score = ScoreEngine.dayEffective(data.days[todayKey])
    val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val greet = remember(Greetings.bucketOf(score), Greetings.periodOf(hour), s.lang) {
        Greetings.pick(s, score, hour)
    }

    Box(Modifier.fillMaxSize()) {
        Image(
            painterResource(R.drawable.room_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter,
        )
        // scrim ขาวจางบนหัว ให้การ์ดอ่านง่ายบนฉากห้อง
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Brush.verticalGradient(listOf(Color(0xB8FFFFFF), Color(0x30FFFFFF), Color.Transparent))),
        )
        AmbientPetals(Modifier.fillMaxSize())

        // หมาโบกมือ นั่งกลางห้อง ค่อนขวา (ตาม foxMascot left:68%)
        Mascot(
            size = 330.dp,
            wave = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = 58.dp, y = (-86).dp),
        )

        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(18.dp))
            Box(Modifier.enterStagger(0)) {
                SpeechCard(
                    prefix = greet.first,
                    name = data.profileName.ifBlank { s.homeNameDefault },
                    sub = greet.second,
                    onEditName = onEditName,
                )
            }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.enterStagger(1)) {
                HomeProgressCard(score = score, onGoToday = onGoToday)
            }
            if (todayPlanCount > 0) {
                Spacer(Modifier.height(12.dp))
                Box(Modifier.enterStagger(2)) {
                    PlanBanner(count = todayPlanCount, onClick = onGoPlan)
                }
            }
            Spacer(Modifier.height(14.dp))
            Box(Modifier.enterStagger(3)) {
                StartQuestButton(text = s.homeStartQuest, onClick = onStartQuest)
            }
        }

        DiceFab(
            cooldownUntil = data.dice.cooldownUntil,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 112.dp),
            onClick = onOpenDice,
        )
    }
}

/** ลูกเต๋าลอย ลากย้ายได้ + ป้าย cooldown นับถอยหลัง */
@Composable
private fun DiceFab(cooldownUntil: Long, modifier: Modifier = Modifier, onClick: () -> Unit) {
    var off by remember { mutableStateOf(Offset.Zero) }
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    androidx.compose.runtime.LaunchedEffect(cooldownUntil) {
        while (System.currentTimeMillis() < cooldownUntil) {
            now = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000)
        }
        now = System.currentTimeMillis()
    }
    val remaining = cooldownUntil - now

    Column(
        modifier
            .offset { IntOffset(off.x.roundToInt(), off.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, amt ->
                    change.consume()
                    off += amt
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(60.dp)
                .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x33464E8C), spotColor = Color(0x40464E8C))
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(Color.White, Color(0xFFEEF3FF))))
                .bouncyClick(0.88f, onClick),
            contentAlignment = Alignment.Center,
        ) {
            SqIcon(SqIconKind.Dice, size = 30.dp, tint = Sakura.accent, stroke = 2.2f)
        }
        if (remaining > 0) {
            Spacer(Modifier.height(5.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color(0xC21F2340))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    DateUtil.fmtClock(remaining / 1000),
                    fontSize = 10.sp, color = Color.White, fontFamily = NumFont, fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
