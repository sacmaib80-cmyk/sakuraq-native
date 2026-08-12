package com.sacmaib80.sakuraq.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacmaib80.sakuraq.app.ui.theme.Sakura
import com.sacmaib80.sakuraq.app.ui.theme.ThaiFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * หัวใจของฟีล native: แตะแล้วหดนุ่มๆ ด้วย spring แล้วเด้งคืน
 * ปุ่มใหญ่ 0.96, pill 0.93, orb 0.90
 */
fun Modifier.bouncyClick(pressedScale: Float = 0.96f, onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 900f),
        label = "bouncy",
    )
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interactionSource = interaction, indication = null, onClick = onClick)
}

/** เข้าฉากแบบ stagger: จางเข้า + ลอยขึ้น 22dp หน่วง index*70ms */
fun Modifier.enterStagger(index: Int): Modifier = composed {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(22f) }
    LaunchedEffect(Unit) {
        delay(index * 70L)
        launch { alpha.animateTo(1f, spring(stiffness = 300f, dampingRatio = 0.85f)) }
        offsetY.animateTo(0f, spring(stiffness = 260f, dampingRatio = 0.75f))
    }
    this.graphicsLayer {
        this.alpha = alpha.value
        translationY = offsetY.value * density
    }
}

/** การ์ดขาวเงานุ่ม (มาตรฐานทั้งแอป) */
@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = Sakura.cardShape,
    color: Color = Sakura.card,
    padding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(elevation = 12.dp, shape = shape, ambientColor = Color(0x14464E8C), spotColor = Color(0x22464E8C))
            .clip(shape)
            .background(color)
            .padding(padding),
        content = content,
    )
}

/** ป้ายเม็ดยาเล็กๆ */
@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    bg: Color = Sakura.accentSoft,
    fg: Color = Sakura.accent,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = ThaiFont)
    }
}

/** ปุ่ม CTA gradient ม่วงใหญ่ */
@Composable
fun CtaButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    brush: Brush = Sakura.ctaBrush,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (enabled) 1f else 0.45f }
            .shadow(14.dp, shape, ambientColor = Color(0x525B6AF0), spotColor = Color(0x525B6AF0))
            .clip(shape)
            .background(brush)
            .bouncyClick(0.96f) { if (enabled) onClick() }
            .padding(vertical = 17.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, fontFamily = ThaiFont, textAlign = TextAlign.Center)
    }
}

/** ชิปสถิติ (เลเวล/แต้มสะสม/สตรีค) */
@Composable
fun StatChip(
    label: String,
    value: String,
    sub: String? = null,
    modifier: Modifier = Modifier,
    valueColor: Color = Sakura.ink,
) {
    SoftCard(modifier = modifier, shape = Sakura.innerShape, padding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
        Text(label, color = Sakura.muted, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, fontFamily = ThaiFont)
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, color = valueColor, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, fontFamily = ThaiFont)
            if (sub != null) {
                Text(sub, color = Sakura.muted, fontSize = 11.sp, fontFamily = ThaiFont, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}
