package com.example.ui.daily

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyInsight
import com.example.data.repository.HermeticRepository
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Daily Revelation Module:
 * Uses the Gemini API to fetch daily insights from The Kybalion, The Emerald Tablet,
 * Corpus Hermeticum, and ancient alchemical texts, presenting them inside an animated,
 * weather-beaten 17th-century scroll UI component with roll/unroll mechanics,
 * wax seal breaking, and contemplation rituals.
 */
@Composable
fun DailyRevelationModule(
    repository: HermeticRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val dailyInsight by repository.dailyInsight.collectAsState()
    val isGenerating by repository.isGeneratingInsight.collectAsState()

    var isScrollUnrolled by remember { mutableStateOf(true) }
    var isSealBroken by remember { mutableStateOf(false) }
    val selectedTextTopic by repository.selectedInsightTopic.collectAsState()
    var showMeditationTimer by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val topics = listOf(
        "The Kybalion",
        "Emerald Tablet",
        "Corpus Hermeticum",
        "Paracelsus Alchemy",
        "Jungian Psychology",
        "Agrippa Magic"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_revelation_module"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Module Header Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ORACULUM REVELATIONIS",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 1.2.sp,
                    color = AntiqueGold
                )
                Text(
                    text = "Daily Hermetic Gnosis via Gemini AI",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    color = ParchmentDark
                )
            }

            // Quick Topic Refresh Button
            Button(
                onClick = {
                    // Runs on the repository's scope so it completes even if the user changes tab
                    repository.requestDailyInsight(selectedTextTopic)
                    isScrollUnrolled = true
                },
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChamberSurfaceLight,
                    contentColor = AntiqueGold
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = AntiqueGold, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Summon", fontFamily = CinzelFont, fontSize = 11.sp)
                }
            }
        }

        // Ancient Text Topic Filter Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(topics) { topic ->
                val isSelected = topic == selectedTextTopic
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) AntiqueGold else ChamberSurface)
                        .border(1.dp, if (isSelected) GoldBright else ChamberBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            repository.setSelectedInsightTopic(topic)
                            repository.requestDailyInsight(topic)
                            isScrollUnrolled = true
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = topic,
                        fontFamily = CinzelFont,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NigredoBlack else ParchmentLight
                    )
                }
            }
        }

        // The Animated Weather-Beaten Scroll Component
        AnimatedWeatherBeatenScroll(
            insight = dailyInsight,
            isUnrolled = isScrollUnrolled,
            isSealBroken = isSealBroken,
            isGenerating = isGenerating,
            onToggleUnroll = {
                isScrollUnrolled = !isScrollUnrolled
            },
            onBreakSeal = {
                com.example.audio.AlchemicalAudioEngine.getInstance().playWaxSeal()
                com.example.audio.AlchemicalAudioEngine.getInstance().playBellChime()
                isSealBroken = true
            },
            onInscribe = {
                com.example.audio.AlchemicalAudioEngine.getInstance().playWaxSeal()
                repository.addJournalEntry(
                    title = dailyInsight.title,
                    category = "Hermetism",
                    content = "Revelation from ${dailyInsight.sourceText}:\n\n\"${dailyInsight.ancientQuote}\"\n\nExegesis: ${dailyInsight.esotericExegesis}\n\nRitual: ${dailyInsight.dailyHermeticContemplation}",
                    sealGlyph = dailyInsight.glyphSymbol
                )
                snackbarMessage = "Inscribed into thy Grimoire (+60 XP)"
            },
            onStartContemplation = {
                com.example.audio.AlchemicalAudioEngine.getInstance().playBellChime()
                showMeditationTimer = true
            }
        )

        // Contemplation Meditation Modal
        if (showMeditationTimer) {
            HermeticContemplationTimerDialog(
                insight = dailyInsight,
                onDismiss = { showMeditationTimer = false }
            )
        }

        // Notification banner
        AnimatedVisibility(visible = snackbarMessage != null) {
            snackbarMessage?.let { msg ->
                LaunchedEffect(msg) {
                    delay(2600)
                    snackbarMessage = null
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AntiqueGold)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = msg,
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        color = NigredoBlack,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

/**
 * Animated Weather-Beaten Scroll Component:
 * Features top and bottom carved wooden roller dowels with brass finials,
 * deckled parchment canvas edges, animated unrolling transitions,
 * and illuminated text rubrics.
 */
@Composable
fun AnimatedWeatherBeatenScroll(
    insight: DailyInsight,
    isUnrolled: Boolean,
    isSealBroken: Boolean,
    isGenerating: Boolean,
    onToggleUnroll: () -> Unit,
    onBreakSeal: () -> Unit,
    onInscribe: () -> Unit,
    onStartContemplation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("animated_scroll_container"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Wooden Roller Dowel with Brass Finials
        ScrollRollerDowel(isTop = true)

        // The Scroll Body (expands / collapses with spring animation)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .shadow(12.dp, spotColor = Color.Black.copy(alpha = 0.85f))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            ParchmentLight,
                            ParchmentBase,
                            ParchmentDark,
                            ParchmentDeep
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(listOf(ChamberBorder, AntiqueGold, ChamberBorder)),
                    shape = androidx.compose.ui.graphics.RectangleShape
                )
        ) {
            // Weather-Beaten Border Canvas (burnt edges, stains, corner flourishes)
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height
                val inset = 10.dp.toPx()

                // Aged deckled margin line
                drawRect(
                    color = FadedInk.copy(alpha = 0.22f),
                    topLeft = Offset(inset, inset),
                    size = Size(w - inset * 2, h - inset * 2),
                    style = Stroke(1.2f)
                )

                // 17th Century corner flourishes
                val cLen = 14.dp.toPx()
                drawLine(AntiqueGold, Offset(inset, inset + cLen), Offset(inset, inset), strokeWidth = 2f)
                drawLine(AntiqueGold, Offset(inset, inset), Offset(inset + cLen, inset), strokeWidth = 2f)
                drawLine(AntiqueGold, Offset(w - inset - cLen, inset), Offset(w - inset, inset), strokeWidth = 2f)
                drawLine(AntiqueGold, Offset(w - inset, inset), Offset(w - inset, inset + cLen), strokeWidth = 2f)
                drawLine(AntiqueGold, Offset(inset, h - inset - cLen), Offset(inset, h - inset), strokeWidth = 2f)
                drawLine(AntiqueGold, Offset(inset, h - inset), Offset(inset + cLen, h - inset), strokeWidth = 2f)
                drawLine(AntiqueGold, Offset(w - inset - cLen, h - inset), Offset(w - inset, h - inset), strokeWidth = 2f)
                drawLine(AntiqueGold, Offset(w - inset, h - inset), Offset(w - inset, h - inset - cLen), strokeWidth = 2f)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Scroll Top Ribbon / Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ORACULUM ANCIENT FOIL",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                        color = WaxSealRed
                    )

                    // Clickable Red Wax Seal badge
                    WaxSealBadge(
                        glyph = insight.glyphSymbol,
                        size = 40.dp,
                        onClick = {
                            onBreakSeal()
                            onToggleUnroll()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = insight.title,
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = IronGallInk
                )

                Text(
                    text = "Source: ${insight.sourceText} • ${insight.date}",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    color = FadedInk
                )

                Spacer(modifier = Modifier.height(14.dp))

                // The Unrolled Parchment Content
                AnimatedVisibility(
                    visible = isUnrolled,
                    enter = fadeIn(tween(400)) + expandVertically(spring(dampingRatio = 0.8f)),
                    exit = fadeOut(tween(300)) + shrinkVertically()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // The Sacred Ancient Maxim Blockquote
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(ParchmentDark.copy(alpha = 0.45f))
                                .border(1.dp, FadedInk.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = "“${insight.ancientQuote}”",
                                    fontFamily = GaramondFont,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 17.sp,
                                    lineHeight = 25.sp,
                                    color = IronGallInk
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "— ${insight.sourceText}",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = WaxSealRed,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Esoteric Exegesis
                        Text(
                            text = "METAPHYSICAL EXEGESIS:",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp,
                            color = IronGallInk
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = insight.esotericExegesis,
                            fontFamily = GaramondFont,
                            fontSize = 15.sp,
                            lineHeight = 23.sp,
                            color = FadedInk
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Psychological Correlation (Jungian Alchemy)
                        Text(
                            text = "PSYCHOLOGICAL ALCHEMY:",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp,
                            color = WaxSealRed
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = insight.psychologicalCorrelation,
                            fontFamily = GaramondFont,
                            fontSize = 15.sp,
                            lineHeight = 23.sp,
                            color = FadedInk
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Daily Contemplation Ritual Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(AntiqueGold.copy(alpha = 0.15f))
                                .border(1.dp, AntiqueGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "☉ CONTEMPLATION RITUAL",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.8.sp,
                                    color = ChamberDark
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = insight.dailyHermeticContemplation,
                                    fontFamily = GaramondFont,
                                    fontSize = 14.sp,
                                    lineHeight = 21.sp,
                                    color = IronGallInk
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Actions on the Unrolled Scroll
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onStartContemplation,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = IronGallInk),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AntiqueGold)
                            ) {
                                Icon(Icons.Default.HourglassBottom, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Meditate", fontFamily = CinzelFont, fontSize = 11.sp)
                            }

                            Button(
                                onClick = onInscribe,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WaxSealRed,
                                    contentColor = ParchmentLight
                                )
                            ) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Inscribe", fontFamily = CinzelFont, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // If collapsed: Show brief ribbon and tap to unroll
                if (!isUnrolled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleUnroll() }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✦ Tap to Break Seal and Unroll Ancient Parchment ✦",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = WaxSealRed
                        )
                    }
                }
            }
        }

        // Bottom Wooden Roller Dowel with Brass Finials
        ScrollRollerDowel(isTop = false)
    }
}

/**
 * Wooden Roller Dowel with antique brass finials for the parchment scroll.
 */
@Composable
private fun ScrollRollerDowel(isTop: Boolean) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
    ) {
        val w = size.width
        val h = size.height
        val midY = h / 2f

        // Center wooden dowel
        val dowelBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF533B2B),
                Color(0xFF2C1D14),
                Color(0xFF140D08)
            )
        )
        drawRoundRect(
            brush = dowelBrush,
            topLeft = Offset(w * 0.03f, 2f),
            size = Size(w * 0.94f, h - 4f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )

        // Left Brass Finial
        drawCircle(
            brush = Brush.radialGradient(listOf(GoldBright, AntiqueGold, BrassChamber)),
            radius = h * 0.44f,
            center = Offset(w * 0.03f, midY)
        )

        // Right Brass Finial
        drawCircle(
            brush = Brush.radialGradient(listOf(GoldBright, AntiqueGold, BrassChamber)),
            radius = h * 0.44f,
            center = Offset(w * 0.97f, midY)
        )
    }
}

/**
 * Hermetic Contemplation Timer Dialog:
 * An alchemical meditation hourglass timing the adept's contemplation of the daily maxim.
 */
@Composable
private fun HermeticContemplationTimerDialog(
    insight: DailyInsight,
    onDismiss: () -> Unit
) {
    var secondsRemaining by remember { mutableIntStateOf(180) } // 3 minutes
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
        if (secondsRemaining == 0) {
            isTimerRunning = false
        }
    }

    val minutes = secondsRemaining / 60
    val secs = secondsRemaining % 60

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ChamberDark,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "HERMETIC CONTEMPLATION",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AntiqueGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Still the mind in the Vessel of Hermes",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = ParchmentDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Meditation timer circular dial
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(ChamberSurface)
                        .border(2.dp, AntiqueGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%02d:%02d", minutes, secs),
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = GoldBright
                        )
                        Text(
                            text = if (secondsRemaining > 0) "Contemplating" else "Complete!",
                            fontFamily = GaramondFont,
                            fontSize = 11.sp,
                            color = ParchmentLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "“${insight.dailyHermeticContemplation}”",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    color = ParchmentLight
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WaxSealRed,
                    contentColor = ParchmentLight
                )
            ) {
                Text(if (secondsRemaining == 0) "Seal Meditation" else "Close", fontFamily = CinzelFont, fontSize = 11.sp)
            }
        }
    )
}
