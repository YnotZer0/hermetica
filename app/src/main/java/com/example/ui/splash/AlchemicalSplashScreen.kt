package com.example.ui.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AlchemicalAudioEngine
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Creatively Animated 17th-Century Alchemical Loading Splash Screen:
 * Displays concentric rotating celestial astrolabe rings, sacred geometry of the Seal of Solomon,
 * floating golden furnace embers, illuminated alchemical acronym V.I.T.R.I.O.L., and authentic audio.
 */
@Composable
fun AlchemicalSplashScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val audioEngine = remember { AlchemicalAudioEngine.getInstance() }

    // Animation drivers
    val infiniteTransition = rememberInfiniteTransition(label = "splash_transition")

    val outerRingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_ring_rot"
    )

    val innerRingRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_ring_rot"
    )

    val cruciblePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crucible_pulse"
    )

    val emberOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ember_flow"
    )

    // Unfurling text stages
    var stage by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        audioEngine.playBellChime()
        audioEngine.playBubblingCauldron()
        delay(600)
        stage = 1 // Show title
        delay(700)
        stage = 2 // Show V.I.T.R.I.O.L.
        delay(800)
        stage = 3 // Show initiation button
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireCover)
            .testTag("alchemical_splash_screen")
    ) {
        // Background radial glow mimicking candlelit tower chamber
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AthanorEmber.copy(alpha = 0.18f),
                            ChamberDark.copy(alpha = 0.85f),
                            NigredoBlack
                        ),
                        radius = 1200f
                    )
                )
        )

        // Floating Athanor Embers & Dust Motes Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 24 ambient floating furnace spark particles
            val emberSeeds = listOf(
                Triple(0.15f, 0.85f, 2.5f),
                Triple(0.28f, 0.70f, 3.2f),
                Triple(0.45f, 0.90f, 2.0f),
                Triple(0.60f, 0.75f, 3.8f),
                Triple(0.72f, 0.82f, 2.8f),
                Triple(0.85f, 0.65f, 2.2f),
                Triple(0.20f, 0.50f, 1.8f),
                Triple(0.35f, 0.40f, 2.4f),
                Triple(0.55f, 0.55f, 3.0f),
                Triple(0.68f, 0.45f, 2.6f),
                Triple(0.80f, 0.35f, 1.9f),
                Triple(0.50f, 0.25f, 3.4f)
            )

            emberSeeds.forEach { (xFactor, yBase, radius) ->
                val currentY = ((yBase - emberOffset * 0.45f) % 1.0f + 1.0f) % 1.0f
                val driftX = sin(emberOffset * 2 * PI + xFactor * 10).toFloat() * 14.dp.toPx()
                val px = w * xFactor + driftX
                val py = h * currentY

                val alpha = (sin(currentY * PI)).coerceIn(0.0, 1.0).toFloat() * 0.75f

                drawCircle(
                    color = GoldBright.copy(alpha = alpha),
                    radius = radius.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }

        // Center Alchemical Sigil & Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Rotating Celestial Astrolabe & Seal of Solomon Canvas
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Outer Astrological / Planetary Ring
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(outerRingRotation)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val r = size.width / 2f - 8f

                    // Outer brass border
                    drawCircle(
                        color = AntiqueGold.copy(alpha = 0.55f),
                        radius = r,
                        center = center,
                        style = Stroke(2.dp.toPx())
                    )
                    drawCircle(
                        color = ChamberBorder,
                        radius = r - 12.dp.toPx(),
                        center = center,
                        style = Stroke(1.dp.toPx())
                    )

                    // 12 Astrological House Radials
                    for (i in 0 until 12) {
                        val angle = (i * 30f) * (PI / 180f)
                        val p1 = Offset(
                            center.x + (r - 12.dp.toPx()) * cos(angle).toFloat(),
                            center.y + (r - 12.dp.toPx()) * sin(angle).toFloat()
                        )
                        val p2 = Offset(
                            center.x + r * cos(angle).toFloat(),
                            center.y + r * sin(angle).toFloat()
                        )
                        drawLine(
                            color = AntiqueGold.copy(alpha = 0.5f),
                            start = p1,
                            end = p2,
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                }

                // Inner Counter-Rotating Hexagram & Planetary Disc
                Canvas(
                    modifier = Modifier
                        .size(180.dp)
                        .rotate(innerRingRotation)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val r = size.width / 2f - 4f

                    // Inner circle
                    drawCircle(
                        color = CitrinitasAmber.copy(alpha = 0.65f),
                        radius = r,
                        center = center,
                        style = Stroke(1.5.dp.toPx())
                    )

                    // Alchemical Seal of Solomon (Hexagram: Fire △ & Water ▽)
                    val ptsUp = List(3) { idx ->
                        val a = (idx * 120f - 90f) * (PI / 180f)
                        Offset(center.x + r * cos(a).toFloat(), center.y + r * sin(a).toFloat())
                    }
                    val ptsDown = List(3) { idx ->
                        val a = (idx * 120f + 90f) * (PI / 180f)
                        Offset(center.x + r * cos(a).toFloat(), center.y + r * sin(a).toFloat())
                    }

                    // Upward Triangle (Fire 🜂)
                    drawLine(AntiqueGold, ptsUp[0], ptsUp[1], strokeWidth = 2f)
                    drawLine(AntiqueGold, ptsUp[1], ptsUp[2], strokeWidth = 2f)
                    drawLine(AntiqueGold, ptsUp[2], ptsUp[0], strokeWidth = 2f)

                    // Downward Triangle (Water 🜄)
                    drawLine(CitrinitasAmber, ptsDown[0], ptsDown[1], strokeWidth = 2f)
                    drawLine(CitrinitasAmber, ptsDown[1], ptsDown[2], strokeWidth = 2f)
                    drawLine(CitrinitasAmber, ptsDown[2], ptsDown[0], strokeWidth = 2f)
                }

                // Center Pulsing Crucible Eye of Hermes
                Box(
                    modifier = Modifier
                        .size((76 * cruciblePulse).dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(
                                    GoldBright,
                                    WaxSealRed,
                                    ChamberDark
                                )
                            )
                        )
                        .border(1.5.dp, GoldBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🜚",
                        fontSize = 32.sp,
                        color = GoldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title & Anno Revelation
            AnimatedVisibility(
                visible = stage >= 1,
                enter = fadeIn(tween(600)) + expandVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "HERMETICA",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        letterSpacing = 4.sp,
                        color = AntiqueGold
                    )

                    Text(
                        text = "ANNO DOMINI MDCLXXXIV",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        letterSpacing = 2.5.sp,
                        color = CitrinitasAmber
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The Arcane Kybalion & Alchemical Laboratory",
                        fontFamily = GaramondFont,
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp,
                        color = ParchmentLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // The Alchemical Maxim: V.I.T.R.I.O.L.
            AnimatedVisibility(
                visible = stage >= 2,
                enter = fadeIn(tween(600)) + expandVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = ChamberDark.copy(alpha = 0.85f)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(ChamberBorder, AntiqueGold, ChamberBorder))
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "V • I • T • R • I • O • L",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 3.sp,
                            color = WaxSealRed
                        )
                        Text(
                            text = "“Visita Interiora Terrae Rectificando Invenies Occultum Lapidem”",
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = ParchmentBase,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Visit the interior of the earth, and by rectifying, thou shalt find the hidden stone.",
                            fontFamily = GaramondFont,
                            fontSize = 11.sp,
                            color = FadedInk,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Interactive "Enter the Sanctum" Wax Seal Initiation Button
            AnimatedVisibility(
                visible = stage >= 3,
                enter = fadeIn(tween(500)) + expandVertically()
            ) {
                Button(
                    onClick = {
                        audioEngine.playWaxSeal()
                        audioEngine.playBellChime()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .shadow(12.dp, CircleShape, spotColor = WaxSealRed),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WaxSealRed,
                        contentColor = ParchmentLight
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldBright),
                    shape = CircleShape
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "✦",
                            fontSize = 16.sp,
                            color = GoldBright
                        )
                        Text(
                            text = "INITIATE THE SANCTUM",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "✦",
                            fontSize = 16.sp,
                            color = GoldBright
                        )
                    }
                }
            }
        }
    }
}
