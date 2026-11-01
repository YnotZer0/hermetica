package com.example.ui.magic

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.AlchemicalAudioEngine
import com.example.data.magic.PlanetaryHourCalculator
import com.example.data.model.CelestialPlanet
import com.example.data.model.PlanetaryHourInfo
import com.example.data.repository.HermeticRepository
import com.example.ui.theme.*
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

/**
 * Horologium Hermeticum:
 * 17th-century Astrolabe & Real-Time Planetary Hours module.
 * Informs the adept of celestial rulerships, planetary incenses, metal correspondences,
 * and auspicious magical operations according to Agrippa and the Picatrix.
 */
@Composable
fun HorologiumHermeticumDialog(
    repository: HermeticRepository,
    onDismiss: () -> Unit
) {
    val audioEngine = remember { AlchemicalAudioEngine.getInstance() }
    val currentHourInfo = remember { PlanetaryHourCalculator.calculateCurrentPlanetaryHour() }
    val daySchedule = remember { PlanetaryHourCalculator.getFullDaySchedule() }
    var selectedTab by remember { mutableStateOf(0) } // 0: Auspice & Rite, 1: 24h Astrolabe Timetable
    var isInscribed by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("horologium_hermeticum_dialog")
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, AntiqueGold, RoundedCornerShape(16.dp)),
            color = ChamberDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ChamberDark,
                                Color(0xFF1B1612),
                                ChamberDark
                            )
                        )
                    )
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ChamberSurface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "☿",
                            fontSize = 22.sp,
                            color = AntiqueGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "HOROLOGIUM HERMETICUM",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 2.sp,
                                color = GoldBright
                            )
                            Text(
                                text = "${currentHourInfo.dayNameLatin} • Ruler: ${currentHourInfo.dayRulerPlanet.glyph} ${currentHourInfo.dayRulerPlanet.latinName}",
                                fontFamily = GaramondFont,
                                fontSize = 12.sp,
                                color = ParchmentDark
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            onDismiss()
                        },
                        modifier = Modifier.testTag("close_horologium_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Horologium",
                            tint = AntiqueGold
                        )
                    }
                }

                // Tab Switcher: Current Auspice vs 24h Timetable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ChamberSurface)
                        .padding(4.dp)
                ) {
                    TabPill(
                        title = "CURRENT AUSPICE",
                        glyph = currentHourInfo.planet.glyph,
                        isSelected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TabPill(
                        title = "24-HOUR ASTROLABE",
                        glyph = "🜎",
                        isSelected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Body Content
                Box(modifier = Modifier.weight(1f)) {
                    if (selectedTab == 0) {
                        CurrentAuspiceView(
                            hourInfo = currentHourInfo,
                            isInscribed = isInscribed,
                            onInscribe = {
                                isInscribed = true
                                repository.addJournalEntry(
                                    title = "Auspice of ${currentHourInfo.planet.latinName} (${currentHourInfo.planet.glyph})",
                                    category = "Ritual",
                                    content = "Consecrated during the ${if (currentHourInfo.isDiurnal) "Diurnal" else "Nocturnal"} Hour of ${currentHourInfo.planet.englishName}. Auspicious operations: ${currentHourInfo.planet.auspiciousWork} Metal: ${currentHourInfo.planet.metal}. Incense: ${currentHourInfo.planet.incense}.",
                                    sealGlyph = currentHourInfo.planet.glyph
                                )
                                repository.addExperience(75)
                                audioEngine.playWaxSeal()
                                audioEngine.playTransmutationHarmonic()
                            }
                        )
                    } else {
                        AstrolabeTimetable(daySchedule = daySchedule, currentHour = currentHourInfo.hourNumber)
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentAuspiceView(
    hourInfo: PlanetaryHourInfo,
    isInscribed: Boolean,
    onInscribe: () -> Unit
) {
    val planet = hourInfo.planet
    val scrollState = rememberScrollState()

    // Rotating astrolabe ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "AstrolabeRot")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(36000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AstrolabeRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Celestial Planetary Bezel
        Box(
            modifier = Modifier
                .size(170.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f

                // Outer brass ring
                drawCircle(
                    color = AntiqueGold.copy(alpha = 0.8f),
                    radius = radius - 4.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Rotating tick marks
                val ticks = 24
                for (i in 0 until ticks) {
                    val angle = (i * (2 * Math.PI / ticks) + (rotation * Math.PI / 180f)).toFloat()
                    val r1 = radius - 12.dp.toPx()
                    val r2 = radius - 5.dp.toPx()
                    drawLine(
                        color = AntiqueGold.copy(alpha = 0.5f),
                        start = Offset(center.x + r1 * cos(angle), center.y + r1 * sin(angle)),
                        end = Offset(center.x + r2 * cos(angle), center.y + r2 * sin(angle)),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Inner planetary glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(planet.colorHex).copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius * 0.7f
                    ),
                    radius = radius * 0.7f,
                    center = center
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = planet.glyph,
                    fontSize = 44.sp,
                    color = GoldBright
                )
                Text(
                    text = planet.latinName.uppercase(),
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = AntiqueGold
                )
            }
        }

        // Time Banner & Countdown
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(ChamberSurface)
                .border(1.dp, AntiqueGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = CitrinitasAmber,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${hourInfo.startTimeFormatted} – ${hourInfo.endTimeFormatted} (${hourInfo.minutesRemaining} min remaining)",
                fontFamily = CinzelFont,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = CitrinitasAmber
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Correspondences Grid Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ChamberSurface),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "CELESTIAL CORRESPONDENCES",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = AntiqueGold
                )
                Spacer(modifier = Modifier.height(8.dp))

                CorrespondenceRow(label = "Metal & Symbol", value = "${planet.metal}  ${planet.metalGlyph}")
                CorrespondenceRow(label = "Elemental Nature", value = planet.element)
                CorrespondenceRow(label = "Sacred Incense", value = planet.incense)
                CorrespondenceRow(label = "Cycle Phase", value = if (hourInfo.isDiurnal) "Diurnal (Solar Day)" else "Nocturnal (Lunar Night)")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Auspicious Alchemical Operations
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ParchmentBase.copy(alpha = 0.12f)),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, CitrinitasAmber.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🜔", fontSize = 16.sp, color = GoldBright)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AUSPICIOUS ALCHEMICAL WORK",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = planet.auspiciousWork,
                    fontFamily = GaramondFont,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = ParchmentLight
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = AntiqueGold.copy(alpha = 0.3f), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "“${planet.hermeticAxiom}”",
                    fontFamily = GaramondFont,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = CitrinitasAmber
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Inscribe Auspice Button
        Button(
            onClick = onInscribe,
            enabled = !isInscribed,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("inscribe_auspice_button")
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, AntiqueGold, RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isInscribed) ChamberDark else WaxSealRed,
                contentColor = AntiqueGold
            ),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isInscribed) "✓  AUSPICE INSCRIBED IN GRIMOIRE" else "🜎  INSCRIBE AUSPICE IN GRIMOIRE (+75 XP)",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = if (isInscribed) ParchmentDark else GoldBright
                )
            }
        }
    }
}

@Composable
private fun AstrolabeTimetable(
    daySchedule: List<PlanetaryHourInfo>,
    currentHour: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(daySchedule) { hour ->
            val isCurrent = hour.hourNumber == currentHour
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) ChamberSurface.copy(alpha = 0.95f) else ChamberDark
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    width = if (isCurrent) 1.5.dp else 0.8.dp,
                    color = if (isCurrent) GoldBright else AntiqueGold.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isCurrent) AntiqueGold.copy(alpha = 0.25f) else ChamberSurface)
                                .border(1.dp, AntiqueGold.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = hour.planet.glyph,
                                fontSize = 18.sp,
                                color = if (isCurrent) GoldBright else AntiqueGold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Hour ${hour.hourNumber}: ${hour.planet.latinName}",
                                    fontFamily = CinzelFont,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (isCurrent) GoldBright else ParchmentLight
                                )
                                if (isCurrent) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ACTIVE",
                                        fontFamily = CinzelFont,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CitrinitasAmber
                                    )
                                }
                            }
                            Text(
                                text = "${hour.startTimeFormatted} – ${hour.endTimeFormatted}  (${if (hour.isDiurnal) "Diurnal ☼" else "Nocturnal ☽"})",
                                fontFamily = GaramondFont,
                                fontSize = 11.sp,
                                color = ParchmentDark
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = hour.planet.metalGlyph,
                            fontSize = 14.sp,
                            color = AntiqueGold
                        )
                        Text(
                            text = hour.planet.element.take(12),
                            fontFamily = GaramondFont,
                            fontSize = 10.sp,
                            color = ParchmentDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CorrespondenceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = GaramondFont,
            fontSize = 12.sp,
            color = ParchmentDark
        )
        Text(
            text = value,
            fontFamily = GaramondFont,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = ParchmentLight
        )
    }
}

@Composable
private fun TabPill(
    title: String,
    glyph: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) AntiqueGold.copy(alpha = 0.25f) else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) AntiqueGold else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = glyph, fontSize = 12.sp, color = if (isSelected) GoldBright else ParchmentDark)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontFamily = CinzelFont,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = if (isSelected) AntiqueGold else ParchmentDark
            )
        }
    }
}
