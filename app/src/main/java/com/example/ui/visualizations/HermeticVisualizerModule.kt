package com.example.ui.visualizations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*
import kotlin.math.*

/**
 * Interactive Hermetic Visualizer Module:
 * Provides advanced, tactile visualizations explaining core Hermetic concepts
 * and the Seven Principles of Hermeticism with 17th-century alchemical styling.
 */
@Composable
fun HermeticVisualizerModule(
    selectedPrincipleId: Int,
    onSelectPrinciple: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val principlesList = remember {
        listOf(
            1 to "Mentalism",
            2 to "Correspondence",
            3 to "Vibration",
            4 to "Polarity",
            5 to "Rhythm",
            6 to "Causality",
            7 to "Gender"
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hermetic_visualizer_module"),
        colors = CardDefaults.cardColors(containerColor = ChamberDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder, BrassChamber))
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with 17th-century brass instrument ornament
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WaxSealBadge(
                        glyph = when (selectedPrincipleId) {
                            1 -> "🜚"
                            2 -> "🜍"
                            3 -> "∿"
                            4 -> "☿"
                            5 -> "⚖"
                            6 -> "⚚"
                            else -> "🜔"
                        },
                        size = 36.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "THEATRUM HERMETICUM",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp,
                            color = AntiqueGold
                        )
                        Text(
                            text = "Interactive Concept Demonstration",
                            fontFamily = GaramondFont,
                            fontSize = 12.sp,
                            color = ParchmentDark
                        )
                    }
                }

                Text(
                    text = "LAW $selectedPrincipleId OF 7",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = CitrinitasAmber
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(principlesList) { (id, name) ->
                    val isCurrent = id == selectedPrincipleId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurrent) AntiqueGold else ChamberSurfaceLight)
                            .border(1.dp, if (isCurrent) GoldBright else ChamberBorder, RoundedCornerShape(6.dp))
                            .clickable { onSelectPrinciple(id) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$id. $name",
                            fontFamily = CinzelFont,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) NigredoBlack else ParchmentLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Interactive Canvas per Principle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F0B08))
                    .border(1.2.dp, ChamberBorder, RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                when (selectedPrincipleId) {
                    1 -> MentalismVisualizer()
                    2 -> CorrespondenceVisualizer()
                    3 -> VibrationCymaticVisualizer()
                    4 -> PolarityCaduceusVisualizer()
                    5 -> RhythmPendulumVisualizer()
                    6 -> CausalityMatrixVisualizer()
                    7 -> GenderConiunctioVisualizer()
                }
            }
        }
    }
}

// --------------------------------------------------------------------------
// 1. MENTALISM: Cosmic Mind Matrix & Sacred Geometric Emanation
// --------------------------------------------------------------------------
@Composable
private fun MentalismVisualizer() {
    var clarityMode by remember { mutableIntStateOf(1) } // 0: Raw Chaos, 1: Focused Intent, 2: Divine Spirit
    var focalCenter by remember { mutableStateOf(Offset(220f, 160f)) }

    val infiniteTransition = rememberInfiniteTransition(label = "mentalism_engine")
    val cosmicBreath by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breath"
    )
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            focalCenter = change.position
                            change.consume()
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset -> focalCenter = offset }
                    }
            ) {
                val w = size.width
                val h = size.height
                val center = if (focalCenter.x in 10f..w && focalCenter.y in 10f..h) focalCenter else Offset(w / 2f, h / 2f)

                // Background Cosmic Mind Matrix grid
                val gridStep = 32f
                for (x in 0..(w / gridStep).toInt()) {
                    drawLine(
                        color = ChamberBorder.copy(alpha = 0.25f),
                        start = Offset(x * gridStep, 0f),
                        end = Offset(x * gridStep, h),
                        strokeWidth = 0.8f
                    )
                }
                for (y in 0..(h / gridStep).toInt()) {
                    drawLine(
                        color = ChamberBorder.copy(alpha = 0.25f),
                        start = Offset(0f, y * gridStep),
                        end = Offset(w, y * gridStep),
                        strokeWidth = 0.8f
                    )
                }

                // Emanation Rings
                val rings = when (clarityMode) {
                    0 -> 4
                    1 -> 7
                    else -> 10
                }

                for (r in 1..rings) {
                    val radius = r * 22f * cosmicBreath
                    val ringAlpha = (1f - (radius / (w * 0.7f))).coerceIn(0.1f, 0.8f)

                    drawCircle(
                        color = when (clarityMode) {
                            0 -> Quicksilver.copy(alpha = ringAlpha * 0.4f)
                            1 -> AntiqueGold.copy(alpha = ringAlpha * 0.6f)
                            else -> GoldBright.copy(alpha = ringAlpha * 0.8f)
                        },
                        radius = radius,
                        center = center,
                        style = Stroke(if (r % 2 == 0) 1.8f else 1.0f)
                    )
                }

                // Sacred Geometry Seed of Life (when in Focused or Divine mode)
                if (clarityMode >= 1) {
                    rotate(rotationAngle * (if (clarityMode == 2) 1.5f else 0.8f), pivot = center) {
                        val petalR = 36f * cosmicBreath
                        for (petal in 0 until 6) {
                            val angle = petal * (PI / 3).toFloat()
                            val px = center.x + petalR * cos(angle)
                            val py = center.y + petalR * sin(angle)

                            drawCircle(
                                color = if (clarityMode == 2) GoldBright.copy(alpha = 0.6f) else AntiqueGold.copy(alpha = 0.4f),
                                radius = petalR,
                                center = Offset(px, py),
                                style = Stroke(1.5f)
                            )
                        }
                    }
                }

                // Central Eye of the Divine Nous / Living Mind
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, GoldBright, WaxSealRed, Color.Transparent),
                        center = center,
                        radius = 28f
                    ),
                    radius = 28f,
                    center = center
                )
            }

            Text(
                text = "Drag or tap anywhere to reposition the focal center of THE ALL",
                fontFamily = GaramondFont,
                fontStyle = FontStyle.Italic,
                fontSize = 11.sp,
                color = ParchmentDark.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // State selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("MIND VIBRATIONAL DEGREE:", fontFamily = CinzelFont, fontSize = 10.sp, color = CitrinitasAmber)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Chaos", "Focused Intent", "The All").forEachIndexed { index, label ->
                    val isSelected = clarityMode == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) AntiqueGold else ChamberSurface)
                            .border(1.dp, if (isSelected) GoldBright else ChamberBorder, RoundedCornerShape(4.dp))
                            .clickable { clarityMode = index }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            fontFamily = CinzelFont,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NigredoBlack else ParchmentLight
                        )
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------
// 2. CORRESPONDENCE: Macrocosm-Microcosm Reciprocal Celestial Orrery
// --------------------------------------------------------------------------
@Composable
private fun CorrespondenceVisualizer() {
    var macroRotation by remember { mutableFloatStateOf(45f) }
    var alignConstellations by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        macroRotation += dragAmount.x * 0.5f
                        change.consume()
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midY = h / 2f

                // Dividing Veil of Hermes (The Horizon of Correspondence)
                drawLine(
                    color = AntiqueGold,
                    start = Offset(w * 0.05f, midY),
                    end = Offset(w * 0.95f, midY),
                    strokeWidth = 2.5f
                )

                // Latin Inscription on dividing veil
                // Macrocosm (Top Half)
                val macroCenter = Offset(w / 2f, midY * 0.5f)
                val macroR = midY * 0.42f

                drawCircle(
                    color = ChamberBorder.copy(alpha = 0.5f),
                    radius = macroR,
                    center = macroCenter,
                    style = Stroke(1.5f)
                )

                // Orbiting Celestial Planets in Macrocosm
                val planets = listOf("☉", "☽", "☿", "♀", "♂")
                val rad = Math.toRadians(macroRotation.toDouble()).toFloat()

                for (i in planets.indices) {
                    val pAngle = rad + (i * (2 * PI / planets.size)).toFloat()
                    val px = macroCenter.x + macroR * cos(pAngle)
                    val py = macroCenter.y + macroR * sin(pAngle)

                    drawCircle(
                        brush = Brush.radialGradient(listOf(GoldBright, AntiqueGold)),
                        radius = 12f,
                        center = Offset(px, py)
                    )

                    // Microcosm (Bottom Half - Exact reciprocal reflection)
                    val microCenter = Offset(w / 2f, midY + (midY * 0.5f))
                    val microR = macroR
                    val mx = microCenter.x + microR * cos(pAngle)
                    val my = microCenter.y - microR * sin(pAngle) // inverted y-axis reflection

                    drawCircle(
                        brush = Brush.radialGradient(listOf(EmeraldHermetic, Quicksilver)),
                        radius = 12f,
                        center = Offset(mx, my)
                    )

                    // Synchronicity Ray linking above with below
                    if (alignConstellations) {
                        drawLine(
                            brush = Brush.verticalGradient(listOf(AntiqueGold, EmeraldHermetic)),
                            start = Offset(px, py),
                            end = Offset(mx, my),
                            strokeWidth = 1.2f
                        )
                    }
                }

                // Terrestrial Microcosmic Circle
                drawCircle(
                    color = EmeraldHermetic.copy(alpha = 0.5f),
                    radius = macroR,
                    center = Offset(w / 2f, midY + (midY * 0.5f)),
                    style = Stroke(1.5f)
                )
            }

            // Labels
            Text(
                text = "MACROCOSMUS (The Celestial Firmament) ☉",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = AntiqueGold,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 12.dp, top = 6.dp)
            )
            Text(
                text = "MICROCOSMUS (The Soul of Man) 🜔",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = EmeraldHermetic,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Drag to rotate celestial orrery", fontFamily = GaramondFont, fontStyle = FontStyle.Italic, fontSize = 11.sp, color = ParchmentDark)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show Synchronicity Rays:", fontFamily = CinzelFont, fontSize = 10.sp, color = ParchmentLight)
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = alignConstellations,
                    onCheckedChange = { alignConstellations = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GoldBright,
                        checkedTrackColor = WaxSealRed,
                        uncheckedThumbColor = ParchmentDark,
                        uncheckedTrackColor = ChamberBorder
                    )
                )
            }
        }
    }
}

// --------------------------------------------------------------------------
// 3. VIBRATION: Cymatic Chladni Resonator & Standing Wave Harmonograph
// --------------------------------------------------------------------------
@Composable
private fun VibrationCymaticVisualizer() {
    var frequencyMode by remember { mutableFloatStateOf(5f) } // 1 to 10
    var isHarmonicDouble by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "cymatic_anim")
    val waveT by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "cymatic_t"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val center = Offset(w / 2f, h / 2f)

                // Brass Chladni Resonance Plate outline
                drawRect(
                    color = BrassChamber.copy(alpha = 0.35f),
                    size = Size(w * 0.9f, h * 0.88f),
                    topLeft = Offset(w * 0.05f, h * 0.06f),
                    style = Stroke(2f)
                )

                // Cymatic Sand Geometric Patterns generated by vibration equation:
                // Z = sin(n * x) * sin(m * y) - sin(m * x) * sin(n * y)
                val n = frequencyMode
                val m = if (isHarmonicDouble) frequencyMode + 2f else frequencyMode + 1f

                val gridRes = 36
                val stepX = (w * 0.86f) / gridRes
                val stepY = (h * 0.82f) / gridRes
                val startX = w * 0.07f
                val startY = h * 0.09f

                for (i in 0..gridRes) {
                    for (j in 0..gridRes) {
                        val normX = (i.toFloat() / gridRes) * PI.toFloat() * 2f
                        val normY = (j.toFloat() / gridRes) * PI.toFloat() * 2f

                        val chladniValue = sin(n * normX + waveT * 0.4f) * sin(m * normY) -
                                sin(m * normX) * sin(n * normY - waveT * 0.4f)

                        // Nodal lines where sand accumulates (near zero vibration)
                        if (abs(chladniValue) < 0.28f) {
                            val px = startX + i * stepX
                            val py = startY + j * stepY

                            drawCircle(
                                color = if (abs(chladniValue) < 0.12f) GoldBright else AntiqueGold.copy(alpha = 0.65f),
                                radius = 2.2f,
                                center = Offset(px, py)
                            )
                        }
                    }
                }

                // Central acoustic transducer node
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color.White, WaxSealRed, Color.Transparent)),
                    radius = 16f,
                    center = center
                )
            }

            Text(
                text = "CYMATIC CHLADNI RESONANCE PLATE",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = CitrinitasAmber,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("FREQUENCY: ${frequencyMode.toInt()} HZ", fontFamily = CinzelFont, fontSize = 11.sp, color = AntiqueGold)
            Spacer(modifier = Modifier.width(10.dp))
            Slider(
                value = frequencyMode,
                onValueChange = { frequencyMode = it },
                valueRange = 2f..10f,
                steps = 7,
                colors = SliderDefaults.colors(thumbColor = GoldBright, activeTrackColor = AntiqueGold)
            )
        }
    }
}

// --------------------------------------------------------------------------
// 4. POLARITY: Caduceus Transmutation of Opposites
// --------------------------------------------------------------------------
@Composable
private fun PolarityCaduceusVisualizer() {
    var transmutationSlider by remember { mutableFloatStateOf(0.5f) } // 0.0 (Dissonance) to 1.0 (Conscious Harmony)
    var selectedPairIndex by remember { mutableIntStateOf(0) }

    val pairs = listOf(
        "Fear (Metus)" to "Courage (Virtus)",
        "Hatred (Odium)" to "Love (Amor)",
        "Grief (Tristitia)" to "Joy (Laetitia)",
        "Inertia (Ignavia)" to "Will (Voluntas)"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midX = w / 2f

                // Central Golden Staff of Hermes
                val staffTop = h * 0.15f
                val staffBottom = h * 0.88f
                drawLine(
                    color = AntiqueGold,
                    start = Offset(midX, staffTop),
                    end = Offset(midX, staffBottom),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )

                // Caduceus Wings at top
                val wingPath = Path().apply {
                    moveTo(midX, staffTop)
                    cubicTo(midX - 45f, staffTop - 30f, midX - 60f, staffTop + 10f, midX, staffTop + 20f)
                    moveTo(midX, staffTop)
                    cubicTo(midX + 45f, staffTop - 30f, midX + 60f, staffTop + 10f, midX, staffTop + 20f)
                }
                drawPath(wingPath, color = GoldBright, style = Stroke(2.5f))

                // Twin Intertwining Serpents of Polarity
                val coils = 4
                val coilHeight = (staffBottom - staffTop) / coils
                val serpent1 = Path()
                val serpent2 = Path()

                val serpentWidth = 35f * (0.6f + transmutationSlider * 0.6f)

                for (c in 0 until coils) {
                    val startY = staffTop + c * coilHeight
                    val endY = startY + coilHeight

                    if (c == 0) {
                        serpent1.moveTo(midX, startY)
                        serpent2.moveTo(midX, startY)
                    }

                    serpent1.cubicTo(
                        midX - serpentWidth, startY + coilHeight * 0.35f,
                        midX + serpentWidth, startY + coilHeight * 0.70f,
                        midX, endY
                    )
                    serpent2.cubicTo(
                        midX + serpentWidth, startY + coilHeight * 0.35f,
                        midX - serpentWidth, startY + coilHeight * 0.70f,
                        midX, endY
                    )
                }

                // Left serpent (Negative Pole)
                drawPath(
                    serpent1,
                    color = if (transmutationSlider < 0.5f) WaxSealRed else Quicksilver,
                    style = Stroke(3f, cap = StrokeCap.Round)
                )

                // Right serpent (Positive Pole)
                drawPath(
                    serpent2,
                    color = if (transmutationSlider > 0.5f) GoldBright else AntiqueGold,
                    style = Stroke(3f, cap = StrokeCap.Round)
                )

                // Adept's Transmutation Mercury Core along the staff
                val coreY = staffBottom - (staffBottom - staffTop) * transmutationSlider
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            if (transmutationSlider > 0.5f) GoldBright else WaxSealRed,
                            Color.Transparent
                        ),
                        center = Offset(midX, coreY),
                        radius = 24f
                    ),
                    radius = 24f,
                    center = Offset(midX, coreY)
                )
            }

            // Left Pole Label
            Column(modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp)) {
                Text(pairs[selectedPairIndex].first, fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = WaxSealRed)
                Text("Base Degree", fontFamily = GaramondFont, fontStyle = FontStyle.Italic, fontSize = 10.sp, color = ParchmentDark)
            }

            // Right Pole Label
            Column(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp), horizontalAlignment = Alignment.End) {
                Text(pairs[selectedPairIndex].second, fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GoldBright)
                Text("Noble Degree", fontFamily = GaramondFont, fontStyle = FontStyle.Italic, fontSize = 10.sp, color = AntiqueGold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pole switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("AXIOM: Opposites are identical in nature, differing in degree", fontFamily = GaramondFont, fontStyle = FontStyle.Italic, fontSize = 11.sp, color = ParchmentDark)
            TextButton(onClick = { selectedPairIndex = (selectedPairIndex + 1) % pairs.size }) {
                Text("Next Polarity Pair", fontFamily = CinzelFont, fontSize = 10.sp, color = CitrinitasAmber)
            }
        }

        Slider(
            value = transmutationSlider,
            onValueChange = { transmutationSlider = it },
            colors = SliderDefaults.colors(thumbColor = GoldBright, activeTrackColor = AntiqueGold)
        )
    }
}

// --------------------------------------------------------------------------
// 5. RHYTHM: Kinetic Hermetic Pendulum with Law of Neutralization
// --------------------------------------------------------------------------
@Composable
private fun RhythmPendulumVisualizer() {
    var isNeutralized by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "rhythm_swing")
    val swingAngle by infiniteTransition.animateFloat(
        initialValue = -52f,
        targetValue = 52f,
        animationSpec = infiniteRepeatable(tween(1750, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pendulum_angle"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val anchor = Offset(w / 2f, h * 0.12f)
                val pendulumLen = h * 0.68f

                // Dial arc for swing measurement
                drawArc(
                    color = ChamberBorder.copy(alpha = 0.5f),
                    startAngle = 40f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(anchor.x - pendulumLen, anchor.y - pendulumLen),
                    size = Size(pendulumLen * 2, pendulumLen * 2),
                    style = Stroke(1.2f)
                )

                // Law of Neutralization Plane (The Higher Mental Sphere)
                val planeY = h * 0.38f
                drawLine(
                    color = if (isNeutralized) AntiqueGold else ChamberBorder.copy(alpha = 0.4f),
                    start = Offset(w * 0.12f, planeY),
                    end = Offset(w * 0.88f, planeY),
                    strokeWidth = if (isNeutralized) 2.5f else 1f
                )

                // Pendulum Rod
                val angleRad = Math.toRadians(swingAngle.toDouble()).toFloat()
                val bobX = anchor.x + pendulumLen * sin(angleRad)
                val bobY = anchor.y + pendulumLen * cos(angleRad)

                drawLine(
                    color = if (isNeutralized) Quicksilver.copy(alpha = 0.35f) else AntiqueGold,
                    start = anchor,
                    end = Offset(bobX, bobY),
                    strokeWidth = 2.5f
                )

                // Swinging Bob (The Mortal Emotional Rollercoaster)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if (isNeutralized) Quicksilver.copy(alpha = 0.5f) else GoldBright,
                            BrassChamber,
                            Color(0xFF2C1E14)
                        )
                    ),
                    radius = 18f,
                    center = Offset(bobX, bobY)
                )

                // Adept Consciousness Anchor (When Neutralization is Active)
                if (isNeutralized) {
                    drawCircle(
                        brush = Brush.radialGradient(listOf(Color.White, GoldBright, WaxSealRed)),
                        radius = 16f,
                        center = Offset(anchor.x, planeY)
                    )
                }
            }

            if (isNeutralized) {
                Text(
                    text = "✦ LAW OF NEUTRALIZATION ACTIVE: UNTOUCHED BY BACKWARD SWING",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = GoldBright,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Law of Neutralization:", fontFamily = CinzelFont, fontSize = 11.sp, color = ParchmentLight)
                Text("Anchor awareness above emotional pendulum", fontFamily = GaramondFont, fontSize = 10.sp, color = ParchmentDark)
            }
            Switch(
                checked = isNeutralized,
                onCheckedChange = { isNeutralized = it },
                colors = SwitchDefaults.colors(checkedThumbColor = GoldBright, checkedTrackColor = WaxSealRed)
            )
        }
    }
}

// --------------------------------------------------------------------------
// 6. CAUSALITY: Multi-Tiered Cause & Effect Matrix (Master vs Pawn)
// --------------------------------------------------------------------------
@Composable
private fun CausalityMatrixVisualizer() {
    var isMasterMode by remember { mutableStateOf(true) }
    var primeCauseIndex by remember { mutableIntStateOf(2) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Three Strata: Spiritual (Top), Mental (Mid), Physical (Bottom)
                val levelsY = listOf(h * 0.22f, h * 0.52f, h * 0.82f)
                val strataNames = listOf("Spiritual Plane (Original Causes)", "Mental Plane (Complexes & Desires)", "Physical Plane (Observed Effects)")

                levelsY.forEachIndexed { idx, py ->
                    drawLine(
                        color = ChamberBorder.copy(alpha = 0.5f),
                        start = Offset(w * 0.08f, py),
                        end = Offset(w * 0.92f, py),
                        strokeWidth = 1f
                    )

                    // Causal Nodes
                    val nodes = 5
                    for (n in 0 until nodes) {
                        val nx = w * 0.16f + n * (w * 0.17f)
                        val isTriggered = if (isMasterMode) {
                            abs(n - primeCauseIndex) <= idx
                        } else {
                            // Pawn mode: moved about blindly by bottom circumstances
                            n % 2 == 0
                        }

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = if (isTriggered) {
                                    listOf(GoldBright, AntiqueGold, WaxSealRed)
                                } else {
                                    listOf(ParchmentDark, ChamberSurfaceLight, ChamberDark)
                                }
                            ),
                            radius = if (isTriggered) 9f else 5.5f,
                            center = Offset(nx, py)
                        )

                        // Downward causality rays
                        if (idx < 2 && isTriggered) {
                            drawLine(
                                color = if (isMasterMode) AntiqueGold.copy(alpha = 0.7f) else Quicksilver.copy(alpha = 0.3f),
                                start = Offset(nx, py),
                                end = Offset(nx, levelsY[idx + 1]),
                                strokeWidth = 1.6f
                            )
                        }
                    }
                }
            }

            Text(
                text = if (isMasterMode) "MASTER MODE: Originating Causes from Above" else "PAWN MODE: Blown about by External Circumstances",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (isMasterMode) GoldBright else Quicksilver,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { isMasterMode = !isMasterMode },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMasterMode) WaxSealRed else ChamberSurfaceLight,
                    contentColor = ParchmentLight
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(if (isMasterMode) "Master of Causes" else "Pawn of Chance", fontFamily = CinzelFont, fontSize = 10.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                (0 until 5).forEach { i ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (primeCauseIndex == i) AntiqueGold else ChamberSurface)
                            .border(1.dp, if (primeCauseIndex == i) GoldBright else ChamberBorder, CircleShape)
                            .clickable { primeCauseIndex = i },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${i + 1}", fontFamily = CinzelFont, fontSize = 10.sp, color = if (primeCauseIndex == i) NigredoBlack else ParchmentLight)
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------
// 7. GENDER: Hieros Gamos Coniunctio Eclipse Dial
// --------------------------------------------------------------------------
@Composable
private fun GenderConiunctioVisualizer() {
    var conjunctionAlignment by remember { mutableFloatStateOf(0.5f) } // 0.0 to 1.0

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midY = h / 2f

                // Solar Sol (Masculine / Logos)
                val solRadius = 38f
                val solX = w * 0.32f + (conjunctionAlignment - 0.5f) * (w * 0.36f)
                drawCircle(
                    brush = Brush.radialGradient(listOf(GoldBright, AntiqueGold, WaxSealRed)),
                    radius = solRadius,
                    center = Offset(solX, midY)
                )

                // Solar Rays
                for (r in 0 until 12) {
                    val angle = (r * (PI / 6)).toFloat()
                    val x1 = solX + solRadius * cos(angle)
                    val y1 = midY + solRadius * sin(angle)
                    val x2 = solX + (solRadius + 14f) * cos(angle)
                    val y2 = midY + (solRadius + 14f) * sin(angle)
                    drawLine(GoldBright.copy(alpha = 0.7f), Offset(x1, y1), Offset(x2, y2), strokeWidth = 2f)
                }

                // Lunar Luna (Feminine / Eros)
                val lunaRadius = 38f
                val lunaX = w * 0.68f - (conjunctionAlignment - 0.5f) * (w * 0.36f)
                drawCircle(
                    brush = Brush.radialGradient(listOf(AlbedoWhite, Quicksilver, Color(0xFF1B2E3D))),
                    radius = lunaRadius,
                    center = Offset(lunaX, midY)
                )

                // Hieros Gamos Conjunction Spark when overlapping in center
                val distance = abs(solX - lunaX)
                if (distance < 24f) {
                    val conjunctX = (solX + lunaX) / 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White, GoldBright, EmeraldHermetic, Color.Transparent),
                            center = Offset(conjunctX, midY),
                            radius = 70f
                        ),
                        radius = 70f,
                        center = Offset(conjunctX, midY)
                    )
                }
            }

            Text(
                text = "SOL (Animus) ☉",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = GoldBright,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 14.dp, top = 8.dp)
            )
            Text(
                text = "☽ LUNA (Anima)",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Quicksilver,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 14.dp, top = 8.dp)
            )

            if (abs(conjunctionAlignment - 0.5f) < 0.06f) {
                Text(
                    text = "✦ CONIUNCTIO SUPREMA (Hieros Gamos Achieved)",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = GoldBright,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ALCHEMICAL MARRIAGE:", fontFamily = CinzelFont, fontSize = 10.sp, color = CitrinitasAmber)
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = conjunctionAlignment,
                onValueChange = { conjunctionAlignment = it },
                colors = SliderDefaults.colors(
                    thumbColor = if (abs(conjunctionAlignment - 0.5f) < 0.06f) GoldBright else Quicksilver,
                    activeTrackColor = AntiqueGold
                )
            )
        }
    }
}
