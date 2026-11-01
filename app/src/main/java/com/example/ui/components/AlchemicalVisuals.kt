package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 17th-century weathered scroll card with parchment gradient, aged borders, and ornamental corner accents.
 */
@Composable
fun ParchmentScroll(
    modifier: Modifier = Modifier,
    title: String? = null,
    latinMotto: String? = null,
    sealGlyph: String? = null,
    onSealClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(12.dp), spotColor = Color.Black.copy(alpha = 0.7f))
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ParchmentLight,
                        ParchmentBase,
                        ParchmentDark
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ChamberBorder,
                        AntiqueGold,
                        ParchmentDeep,
                        ChamberBorder
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // Decorative antique border canvas
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 1f
            val inset = 8.dp.toPx()
            val w = size.width
            val h = size.height

            // Inner margin framing line
            drawRect(
                color = FadedInk.copy(alpha = 0.25f),
                topLeft = Offset(inset, inset),
                size = Size(w - inset * 2, h - inset * 2),
                style = Stroke(strokeWidth)
            )

            // 17th Century corner flourishes
            val cornerLen = 14.dp.toPx()
            // Top-Left
            drawLine(AntiqueGold, Offset(inset, inset + cornerLen), Offset(inset, inset), strokeWidth = 2.5f)
            drawLine(AntiqueGold, Offset(inset, inset), Offset(inset + cornerLen, inset), strokeWidth = 2.5f)
            // Top-Right
            drawLine(AntiqueGold, Offset(w - inset - cornerLen, inset), Offset(w - inset, inset), strokeWidth = 2.5f)
            drawLine(AntiqueGold, Offset(w - inset, inset), Offset(w - inset, inset + cornerLen), strokeWidth = 2.5f)
            // Bottom-Left
            drawLine(AntiqueGold, Offset(inset, h - inset - cornerLen), Offset(inset, h - inset), strokeWidth = 2.5f)
            drawLine(AntiqueGold, Offset(inset, h - inset), Offset(inset + cornerLen, h - inset), strokeWidth = 2.5f)
            // Bottom-Right
            drawLine(AntiqueGold, Offset(w - inset - cornerLen, h - inset), Offset(w - inset, h - inset), strokeWidth = 2.5f)
            drawLine(AntiqueGold, Offset(w - inset, h - inset), Offset(w - inset, h - inset - cornerLen), strokeWidth = 2.5f)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            if (title != null || sealGlyph != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (latinMotto != null) {
                            Text(
                                text = latinMotto.uppercase(),
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp,
                                color = WaxSealRed
                            )
                        }
                        if (title != null) {
                            Text(
                                text = title,
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = IronGallInk
                            )
                        }
                    }

                    if (sealGlyph != null) {
                        WaxSealBadge(
                            glyph = sealGlyph,
                            onClick = onSealClick
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            content()
        }
    }
}

/**
 * 17th Century crimson wax seal stamped with an alchemical glyph.
 */
@Composable
fun WaxSealBadge(
    glyph: String,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFA82626),
                        WaxSealRed,
                        Color(0xFF5E1212)
                    )
                )
            )
            .border(1.5.dp, GoldBurnished.copy(alpha = 0.6f), CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = glyph,
            fontFamily = CinzelFont,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.44f).sp,
            color = AntiqueGold
        )
    }
}

/**
 * Animated Alchemical Apparatus Canvas:
 * Displays a bubbling Cauldron, Athanor flames, rising mystical vapors, and distillation alembic receiver!
 */
@Composable
fun AlchemicalApparatusCanvas(
    modifier: Modifier = Modifier,
    liquidColor: Color = EmeraldHermetic,
    isHeating: Boolean = true,
    heatLevel: Float = 0.7f // 0.0 to 1.0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "apparatus_anim")

    // Flame flicker
    val flameFlicker by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame"
    )

    // Vapor oscillation
    val vaporPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vapor"
    )

    // Bubble offset
    val bubbleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbles"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Wooden / Iron Laboratory Hearth Floor
        val hearthY = h * 0.88f
        drawLine(
            color = ChamberBorder,
            start = Offset(w * 0.05f, hearthY),
            end = Offset(w * 0.95f, hearthY),
            strokeWidth = 4f
        )

        // 2. Athanor Furnace Base (Brick / Iron tripod)
        val athanorWidth = w * 0.42f
        val athanorLeft = w * 0.15f
        val athanorTop = hearthY - h * 0.18f

        // Tripod legs
        drawLine(Color(0xFF3B2E24), Offset(athanorLeft + athanorWidth * 0.2f, athanorTop), Offset(athanorLeft + 5f, hearthY), strokeWidth = 5f)
        drawLine(Color(0xFF3B2E24), Offset(athanorLeft + athanorWidth * 0.8f, athanorTop), Offset(athanorLeft + athanorWidth - 5f, hearthY), strokeWidth = 5f)
        drawLine(Color(0xFF261D15), Offset(athanorLeft + athanorWidth * 0.5f, athanorTop), Offset(athanorLeft + athanorWidth * 0.5f, hearthY), strokeWidth = 4f)

        // Athanor Flame (under the cauldron)
        if (isHeating) {
            val flameCenter = Offset(athanorLeft + athanorWidth * 0.5f, hearthY - 8.dp.toPx())
            val flameH = (h * 0.12f * heatLevel * flameFlicker).coerceAtLeast(15f)

            // Outer red-orange flame
            val flamePath = Path().apply {
                moveTo(flameCenter.x - athanorWidth * 0.25f, flameCenter.y)
                quadraticTo(flameCenter.x - athanorWidth * 0.15f, flameCenter.y - flameH * 0.6f, flameCenter.x, flameCenter.y - flameH)
                quadraticTo(flameCenter.x + athanorWidth * 0.15f, flameCenter.y - flameH * 0.6f, flameCenter.x + athanorWidth * 0.25f, flameCenter.y)
                close()
            }
            drawPath(
                flamePath,
                brush = Brush.verticalGradient(
                    colors = listOf(GoldBright, AthanorEmber, Color(0xFF6B1808)),
                    startY = flameCenter.y - flameH,
                    endY = flameCenter.y
                )
            )

            // Inner core yellow flame
            val corePath = Path().apply {
                moveTo(flameCenter.x - athanorWidth * 0.12f, flameCenter.y)
                quadraticTo(flameCenter.x, flameCenter.y - flameH * 0.8f, flameCenter.x + athanorWidth * 0.12f, flameCenter.y)
                close()
            }
            drawPath(
                corePath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, GoldBright),
                    startY = flameCenter.y - flameH * 0.7f,
                    endY = flameCenter.y
                )
            )
        }

        // 3. The Great Cauldron / Cucurbit Vessel
        val cauldronCenter = Offset(athanorLeft + athanorWidth * 0.5f, athanorTop - h * 0.15f)
        val cauldronRadius = w * 0.18f

        // Cauldron Outer Body (Antique cast iron & copper)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF523C2C), Color(0xFF2E1F16), Color(0xFF140D09)),
                center = cauldronCenter.copy(y = cauldronCenter.y + 10f),
                radius = cauldronRadius
            ),
            radius = cauldronRadius,
            center = cauldronCenter
        )

        // Cauldron Rim & Handles
        drawOval(
            brush = Brush.linearGradient(listOf(AntiqueGold, BrassChamber, Color(0xFF4A3420))),
            topLeft = Offset(cauldronCenter.x - cauldronRadius * 0.95f, cauldronCenter.y - cauldronRadius * 0.95f),
            size = Size(cauldronRadius * 1.9f, cauldronRadius * 0.45f)
        )

        // Handles
        drawArc(
            color = BrassChamber,
            startAngle = 140f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(cauldronCenter.x - cauldronRadius * 1.25f, cauldronCenter.y - cauldronRadius * 0.5f),
            size = Size(cauldronRadius * 0.4f, cauldronRadius * 0.6f),
            style = Stroke(3.5f)
        )
        drawArc(
            color = BrassChamber,
            startAngle = 280f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(cauldronCenter.x + cauldronRadius * 0.85f, cauldronCenter.y - cauldronRadius * 0.5f),
            size = Size(cauldronRadius * 0.4f, cauldronRadius * 0.6f),
            style = Stroke(3.5f)
        )

        // Liquid in the Cauldron
        val liquidTop = cauldronCenter.y - cauldronRadius * 0.85f
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(liquidColor.copy(alpha = 0.95f), liquidColor.copy(alpha = 0.6f), Color(0xFF160E08)),
                center = Offset(cauldronCenter.x, liquidTop + 10f),
                radius = cauldronRadius * 0.8f
            ),
            topLeft = Offset(cauldronCenter.x - cauldronRadius * 0.85f, liquidTop),
            size = Size(cauldronRadius * 1.7f, cauldronRadius * 0.35f)
        )

        // Liquid Bubbles rising
        if (isHeating) {
            val bY1 = liquidTop + 15f - (bubbleOffset * 25f)
            val bY2 = liquidTop + 15f - (((bubbleOffset + 0.5f) % 1f) * 25f)
            drawCircle(Color.White.copy(alpha = 0.8f), radius = 3.5f, center = Offset(cauldronCenter.x - 18f, bY1))
            drawCircle(liquidColor.copy(alpha = 0.9f), radius = 4f, center = Offset(cauldronCenter.x + 12f, bY2))
            drawCircle(Color.White.copy(alpha = 0.7f), radius = 2.5f, center = Offset(cauldronCenter.x + 28f, bY1))
        }

        // 4. Distillation Alembic Neck & Receiver (Right side)
        val alembicHelmY = cauldronCenter.y - cauldronRadius * 0.9f
        val receiverX = w * 0.80f
        val receiverY = hearthY - h * 0.12f

        // Glass Alembic Arm tube curving from cauldron helm over to receiver
        val alembicTube = Path().apply {
            moveTo(cauldronCenter.x, alembicHelmY)
            cubicTo(
                cauldronCenter.x + 30f, alembicHelmY - 45f,
                receiverX - 30f, alembicHelmY - 30f,
                receiverX, receiverY - 35f
            )
        }
        drawPath(
            alembicTube,
            color = Quicksilver.copy(alpha = 0.75f),
            style = Stroke(width = 4.5f, cap = StrokeCap.Round)
        )

        // Glass Receiver Flask (Matrass) on the right
        val flaskRadius = w * 0.11f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Quicksilver.copy(alpha = 0.35f), Color(0x33A0B4C8), Color(0x221B140E)),
                center = Offset(receiverX, receiverY),
                radius = flaskRadius
            ),
            radius = flaskRadius,
            center = Offset(receiverX, receiverY)
        )
        drawCircle(
            color = AntiqueGold.copy(alpha = 0.7f),
            radius = flaskRadius,
            center = Offset(receiverX, receiverY),
            style = Stroke(width = 2f)
        )

        // Condensed essence gathering in the receiver
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(liquidColor.copy(alpha = 0.85f), liquidColor.copy(alpha = 0.4f)),
                radius = flaskRadius * 0.65f
            ),
            radius = flaskRadius * 0.65f,
            center = Offset(receiverX, receiverY + flaskRadius * 0.25f)
        )

        // 5. Rising Mystical Vapors & Vapour Trails
        for (i in 0 until 4) {
            val offsetVapor = (vaporPhase + i * (PI.toFloat() / 2f)) % (2 * PI.toFloat())
            val vx = cauldronCenter.x + sin(offsetVapor) * (20f + i * 8f)
            val vy = alembicHelmY - (offsetVapor / (2 * PI.toFloat())) * (h * 0.4f)
            val alpha = (1f - (offsetVapor / (2 * PI.toFloat()))).coerceIn(0f, 0.7f)

            drawCircle(
                color = liquidColor.copy(alpha = alpha * 0.4f),
                radius = 12f + i * 5f,
                center = Offset(vx, vy)
            )
        }
    }
}

/**
 * Interactive Hermetic Wheel Canvas:
 * Displays the 7 Hermetic Principles arranged along celestial orbits with an Ouroboros ring and Sol/Luna axis.
 */
@Composable
fun HermeticWheelCanvas(
    modifier: Modifier = Modifier,
    activePrincipleId: Int = 1,
    onPrincipleSelected: (Int) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wheel_anim")
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .clickable {
                // cycle through principles on tap
                val next = if (activePrincipleId >= 7) 1 else activePrincipleId + 1
                onPrincipleSelected(next)
            }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = size.width * 0.44f
        val innerRadius = size.width * 0.28f

        // 1. Concentric Astrological & Alchemical Rings
        drawCircle(
            color = AntiqueGold.copy(alpha = 0.2f),
            radius = outerRadius,
            center = center,
            style = Stroke(1.5f)
        )
        drawCircle(
            color = ChamberBorder.copy(alpha = 0.5f),
            radius = outerRadius * 0.88f,
            center = center,
            style = Stroke(1f)
        )
        drawCircle(
            color = AntiqueGold.copy(alpha = 0.35f),
            radius = innerRadius,
            center = center,
            style = Stroke(2f)
        )

        // 2. Ouroboros / Sacred Hexagram Axis in the center
        rotate(degrees = orbitRotation * 0.3f, pivot = center) {
            val triRadius = innerRadius * 0.75f
            val tri1 = Path().apply {
                val p1 = Offset(center.x, center.y - triRadius)
                val p2 = Offset(center.x + triRadius * cos(PI.toFloat() / 6), center.y + triRadius * sin(PI.toFloat() / 6))
                val p3 = Offset(center.x - triRadius * cos(PI.toFloat() / 6), center.y + triRadius * sin(PI.toFloat() / 6))
                moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close()
            }
            val tri2 = Path().apply {
                val p1 = Offset(center.x, center.y + triRadius)
                val p2 = Offset(center.x + triRadius * cos(PI.toFloat() / 6), center.y - triRadius * sin(PI.toFloat() / 6))
                val p3 = Offset(center.x - triRadius * cos(PI.toFloat() / 6), center.y - triRadius * sin(PI.toFloat() / 6))
                moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close()
            }
            drawPath(tri1, color = AntiqueGold.copy(alpha = 0.3f), style = Stroke(1.5f))
            drawPath(tri2, color = Quicksilver.copy(alpha = 0.3f), style = Stroke(1.5f))
        }

        // Center Sun of Hermes
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(GoldBright, AntiqueGold, Color(0xFF6B1808)),
                center = center,
                radius = innerRadius * 0.35f
            ),
            radius = innerRadius * 0.35f,
            center = center
        )

        // 3. The 7 Principles Nodes orbiting on the ring
        val angleStep = (2 * PI) / 7.0
        val glyphs = listOf("🜚", "🜍", "∿", "☿", "⚖", "⚚", "🜔")

        for (i in 0 until 7) {
            val angle = i * angleStep - PI / 2.0
            val nodeCenter = Offset(
                x = (center.x + outerRadius * cos(angle)).toFloat(),
                y = (center.y + outerRadius * sin(angle)).toFloat()
            )

            val isSelected = (i + 1) == activePrincipleId
            val nodeRadius = if (isSelected) 22.dp.toPx() else 15.dp.toPx()

            // Ray connecting to center
            drawLine(
                color = if (isSelected) AntiqueGold else FadedInk.copy(alpha = 0.25f),
                start = center,
                end = nodeCenter,
                strokeWidth = if (isSelected) 2f else 1f
            )

            // Node Circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isSelected) {
                        listOf(GoldBright, AntiqueGold, WaxSealRed)
                    } else {
                        listOf(ParchmentLight, ParchmentDark, ChamberBorder)
                    },
                    center = nodeCenter,
                    radius = nodeRadius
                ),
                radius = nodeRadius,
                center = nodeCenter
            )

            drawCircle(
                color = if (isSelected) GoldBright else ChamberBorder,
                radius = nodeRadius,
                center = nodeCenter,
                style = Stroke(if (isSelected) 2.5f else 1.5f)
            )
        }
    }
}
