package com.sacmaib80.sakuraq.app.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.R
import com.sacmaib80.sakuraq.app.domain.fmtScore
import com.sacmaib80.sakuraq.app.ui.components.SqIcon
import com.sacmaib80.sakuraq.app.ui.components.SqIconKind
import com.sacmaib80.sakuraq.app.ui.components.bouncyClick
import com.sacmaib80.sakuraq.app.ui.i18n.LocalStrings
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import com.sacmaib80.sakuraq.app.ui.theme.NumFont
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont

/** การ์ดคำพูดมาสคอต: โลโก้ + คำทักกวนๆ + ชื่อ (แตะแก้ได้) + หางกล่องคำพูด */
@Composable
internal fun SpeechCard(
    prefix: String,
    name: String,
    sub: String,
    onEditName: (String) -> Unit,
) {
    var editing by remember { mutableStateOf(false) }
    val s = LocalStrings.current

    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .shadow(14.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x216366F1), spotColor = Color(0x266366F1))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xF7FFFFFF))
                .border(1.5.dp, Color(0x388B5CF6), RoundedCornerShape(24.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0x338B5CF6), CircleShape),
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        prefix,
                        fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF25365F), fontFamily = ThaiFont, lineHeight = 20.sp,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        name,
                        fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                        color = Sakura.accent, fontFamily = ThaiFont,
                        modifier = Modifier.bouncyClick(0.9f) { editing = true },
                    )
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    sub,
                    fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64708F), fontFamily = ThaiFont, lineHeight = 18.sp,
                )
            }
        }
        // หางกล่องคำพูด ชี้ลงหามาสคอต
        Canvas(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = 30.dp, y = 13.dp)
                .size(22.dp, 14.dp),
        ) {
            val tail = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width * 0.32f, size.height)
                close()
            }
            drawPath(tail, Color(0xF7FFFFFF))
        }
    }

    if (editing) {
        var draft by remember { mutableStateOf(name) }
        AlertDialog(
            onDismissRequest = { editing = false },
            containerColor = Sakura.card,
            title = { Text(s.homeEditNameTitle, fontFamily = ThaiFont, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Sakura.ink) },
            text = {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { if (it.length <= 20) draft = it },
                    placeholder = { Text(s.homeEditNamePh, fontFamily = ThaiFont, color = Sakura.muted) },
                    textStyle = TextStyle(fontFamily = ThaiFont, fontSize = 15.sp, color = Sakura.ink),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Sakura.accent,
                        unfocusedBorderColor = Sakura.line,
                        cursorColor = Sakura.accent,
                    ),
                )
            },
            confirmButton = {
                TextButton(onClick = { onEditName(draft); editing = false }) {
                    Text(s.save, fontFamily = ThaiFont, color = Sakura.accent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editing = false }) {
                    Text(s.cancel, fontFamily = ThaiFont, color = Sakura.muted)
                }
            },
        )
    }
}

/** การ์ดแต้มวันนี้: เลขใหญ่ / 100 + "+X วันนี้ ›" + แถบ progress ไล่เฉดม่วง */
@Composable
internal fun HomeProgressCard(score: Double, onGoToday: () -> Unit) {
    val s = LocalStrings.current
    val fraction by animateFloatAsState(
        targetValue = (score / 100.0).toFloat().coerceIn(0f, 1f),
        animationSpec = spring(stiffness = 80f, dampingRatio = 0.85f),
        label = "homeTrack",
    )
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x1A6366F1), spotColor = Color(0x226366F1))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xF2FFFFFF))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                fmtScore(score),
                fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NumFont,
                color = Color(0xFF203869),
            )
            Text(
                " / 100",
                fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = NumFont,
                color = Color(0xFF5F6983),
                modifier = Modifier.padding(bottom = 6.dp),
            )
            Spacer(Modifier.weight(1f))
            Text(
                s.homeGainToday.tmpl("n" to fmtScore(score)),
                fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, fontFamily = ThaiFont,
                color = Color(0xFF31C77B),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .bouncyClick(0.93f, onGoToday),
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Color(0x2422C55E)),
        ) {
            if (fraction > 0.004f) {
                Box(
                    Modifier
                        .fillMaxWidth(fraction)
                        .height(14.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFA78BFA), Color(0xFF5B5BD6)))),
                )
            }
        }
    }
}

/** แบนเนอร์ "มี n quest รออยู่วันนี้" → พาไปแท็บ Plan */
@Composable
internal fun PlanBanner(count: Int, onClick: () -> Unit) {
    val s = LocalStrings.current
    Row(
        Modifier
            .shadow(8.dp, RoundedCornerShape(99.dp), ambientColor = Color(0x1A6366F1), spotColor = Color(0x226366F1))
            .clip(RoundedCornerShape(99.dp))
            .background(Color(0xF2FFFFFF))
            .bouncyClick(0.96f, onClick)
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SqIcon(SqIconKind.Target, size = 16.dp, tint = Sakura.accent)
        Spacer(Modifier.width(8.dp))
        Text(
            s.homePlanWaiting.tmpl("n" to count),
            fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF25365F), fontFamily = ThaiFont,
        )
        Spacer(Modifier.width(6.dp))
        SqIcon(SqIconKind.ChevronRight, size = 13.dp, tint = Sakura.muted, stroke = 2.6f)
    }
}

/** ปุ่ม "ไปต่อกันมั้ย?" — ม่วง gradient + pulse หายใจ + อุ้งเท้า (ตามปุ่มจริง) */
@Composable
internal fun StartQuestButton(text: String, onClick: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "cta")
    val t by pulse.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1050), RepeatMode.Reverse),
        label = "ctaPulse",
    )
    Box(
        Modifier
            .graphicsLayer {
                val sc = 1f + 0.016f * t
                scaleX = sc; scaleY = sc
                translationY = -2f * t * density
            }
            .shadow(16.dp, RoundedCornerShape(99.dp), ambientColor = Color(0x525B5BD6), spotColor = Color(0x665B5BD6))
            .clip(RoundedCornerShape(99.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF7C7DE8), Color(0xFF5B5BD6), Color(0xFF4A4AC4))),
            )
            .bouncyClick(0.955f, onClick)
            .padding(horizontal = 26.dp, vertical = 15.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SqIcon(SqIconKind.Paw, size = 19.dp, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text(
                text,
                color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, fontFamily = ThaiFont,
            )
            Spacer(Modifier.width(6.dp))
            SqIcon(SqIconKind.ChevronRight, size = 15.dp, tint = Color.White, stroke = 2.6f)
        }
    }
}
