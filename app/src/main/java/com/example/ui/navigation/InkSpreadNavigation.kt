package com.example.ui.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.audio.AlchemicalAudioEngine
import com.example.ui.HermeticaTab
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 17th-Century Ink-Spreading Navigation Transition:
 * When navigating between sanctuary chambers, an organic wash of 17th-century Iron Gall Ink
 * blooms across the vellum page with dendritic capillary ripples, absorbing into the parchment
 * fibers as the incoming chamber is revealed.
 */
@Composable
fun InkSpreadNavigation(
    currentTab: HermeticaTab,
    modifier: Modifier = Modifier,
    content: @Composable (HermeticaTab) -> Unit
) {
    val audioEngine = remember { AlchemicalAudioEngine.getInstance() }
    val coroutineScope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(currentTab) }
    var displayedTab by remember { mutableStateOf(currentTab) }
    var isTransitioning by remember { mutableStateOf(false) }

    val inkProgress = remember { Animatable(0f) }

    LaunchedEffect(currentTab) {
        if (currentTab != activeTab) {
            val previousTab = activeTab
            activeTab = currentTab
            isTransitioning = true

            // Trigger parchment & ink absorption acoustic resonance
            audioEngine.playInkSpread()

            // Run the ink spread & drying animation
            inkProgress.snapTo(0f)
            inkProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 520,
                    easing = FastOutSlowInEasing
                )
            ) {
                // At midpoint of ink wash, swap the underlying tab content
                if (value >= 0.45f && displayedTab != currentTab) {
                    displayedTab = currentTab
                }
            }

            displayedTab = currentTab
            isTransitioning = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Chamber Content: Gentle subtle scale settling during ink absorption
        val contentScale = if (isTransitioning) {
            val p = inkProgress.value
            if (p < 0.45f) {
                1f - (p * 0.05f) // Slight recess as ink hits
            } else {
                0.97f + ((p - 0.45f) / 0.55f) * 0.03f // Blossom out as ink dries
            }
        } else {
            1f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(contentScale)
        ) {
            content(displayedTab)
        }

        // Overlay: 17th-Century Capillary Iron Gall Ink Spread Canvas
        if (isTransitioning) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val progress = inkProgress.value
                val w = size.width
                val h = size.height
                val center = Offset(w / 2f, h * 0.45f)
                val maxRadius = sqrt(w * w + h * h) * 0.85f

                // Phase 1 (0..0.48): Ink blooms and floods outward
                // Phase 2 (0.48..1.0): Ink absorbs into vellum fibers and dries
                if (progress <= 0.48f) {
                    val expandNorm = (progress / 0.48f).coerceIn(0f, 1f)
                    val currentRadius = maxRadius * expandNorm
                    val alpha = (expandNorm * 1.4f).coerceAtMost(1f)

                    drawOrganicInkBlot(
                        center = center,
                        baseRadius = currentRadius,
                        alpha = alpha,
                        isDrying = false
                    )
                } else {
                    val dryNorm = ((progress - 0.48f) / 0.52f).coerceIn(0f, 1f)
                    val alpha = (1f - dryNorm).coerceIn(0f, 1f)

                    drawOrganicInkBlot(
                        center = center,
                        baseRadius = maxRadius,
                        alpha = alpha,
                        isDrying = true
                    )
                }
            }
        }
    }
}

/**
 * Renders an organic, non-circular capillary ink wash with dendritic ripples,
 * iron gall ink layering, and an oxidized antique gold drying rim.
 */
private fun DrawScope.drawOrganicInkBlot(
    center: Offset,
    baseRadius: Float,
    alpha: Float,
    isDrying: Boolean
) {
    if (baseRadius <= 4f || alpha <= 0.01f) return

    val numPoints = 64
    val path = Path()

    // Harmonic capillary equation: R(θ) = R0 * (1 + 0.12*sin(5θ) + 0.08*cos(8θ + 0.7) + 0.04*sin(13θ))
    for (i in 0..numPoints) {
        val angle = (i.toFloat() / numPoints) * 2f * PI.toFloat()
        val perturbation = 1f +
                0.13f * sin(5f * angle) +
                0.08f * cos(8f * angle + 0.7f) +
                0.05f * sin(13f * angle + 1.4f) +
                0.03f * cos(21f * angle)

        val r = baseRadius * perturbation
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()

    // 1. Deep Core: Iron Gall Ink with deep sepia tones
    val coreColor = if (isDrying) {
        IronGallInk.copy(alpha = alpha * 0.92f)
    } else {
        NigredoBlack.copy(alpha = alpha * 0.98f)
    }

    val edgeColor = if (isDrying) {
        ParchmentDeep.copy(alpha = alpha * 0.4f)
    } else {
        SepiaInk.copy(alpha = alpha * 0.85f)
    }

    drawPath(
        path = path,
        brush = Brush.radialGradient(
            colors = listOf(coreColor, edgeColor, Color.Transparent),
            center = center,
            radius = baseRadius * 1.15f
        ),
        style = Fill
    )

    // 2. Oxidized Golden Rim / Capillary Fringe (characteristic of aged 17th-c ink)
    val rimAlpha = (alpha * 0.7f).coerceIn(0f, 1f)
    drawPath(
        path = path,
        color = AntiqueGold.copy(alpha = rimAlpha),
        style = Stroke(width = if (isDrying) 3.5f else 2.5f)
    )

    // 3. Satellite Splatter Droplets that bloom alongside the main quill impact
    val satellites = listOf(
        Pair(Offset(center.x - baseRadius * 0.72f, center.y - baseRadius * 0.45f), baseRadius * 0.14f),
        Pair(Offset(center.x + baseRadius * 0.65f, center.y - baseRadius * 0.60f), baseRadius * 0.12f),
        Pair(Offset(center.x - baseRadius * 0.55f, center.y + baseRadius * 0.70f), baseRadius * 0.16f),
        Pair(Offset(center.x + baseRadius * 0.75f, center.y + baseRadius * 0.50f), baseRadius * 0.13f),
        Pair(Offset(center.x + baseRadius * 0.10f, center.y - baseRadius * 0.85f), baseRadius * 0.09f),
        Pair(Offset(center.x - baseRadius * 0.85f, center.y + baseRadius * 0.15f), baseRadius * 0.11f)
    )

    for ((pos, satRadius) in satellites) {
        if (satRadius > 2f) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        IronGallInk.copy(alpha = alpha * 0.9f),
                        SepiaInk.copy(alpha = alpha * 0.6f),
                        Color.Transparent
                    ),
                    center = pos,
                    radius = satRadius * 1.2f
                ),
                radius = satRadius,
                center = pos
            )
            drawCircle(
                color = AntiqueGold.copy(alpha = rimAlpha * 0.6f),
                radius = satRadius,
                center = pos,
                style = Stroke(width = 1.2f)
            )
        }
    }
}
