package com.example.ui.contemplation

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.audio.AlchemicalAudioEngine
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/** Sound accompaniment for a contemplation session. */
enum class ContemplationSound(val label: String, val glyph: String, val description: String) {
    DRONE("Resonant Drone", "☉", "A soft sustained harmonic drone with bells to open, mark the halfway point and close."),
    LABORATORY("Laboratory", "🜔", "The alchemist's study: athanor murmur, ember crackle and distant bells."),
    BELLS("Bells Only", "🔔", "Silence, marked only by the opening, halfway and closing bells."),
    SILENCE("Silence", "☾", "No sound at all. The timer runs silently.")
}

data class ContemplationConfig(val minutes: Int, val sound: ContemplationSound)

private val durationOptions = listOf(5, 10, 15, 20, 30)

/**
 * Dashboard card: choose a duration and sound, then begin a contemplation session.
 */
@Composable
fun ContemplationTimerCard(
    onBegin: (ContemplationConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var minutes by rememberSaveable { mutableIntStateOf(10) }
    var soundName by rememberSaveable { mutableStateOf(ContemplationSound.DRONE.name) }
    val sound = ContemplationSound.valueOf(soundName)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("contemplation_timer_card"),
        colors = CardDefaults.cardColors(containerColor = ChamberSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder))
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WaxSealBadge(glyph = "⧗", size = 36.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CONTEMPLATION TIMER",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp,
                        color = AntiqueGold
                    )
                    Text(
                        text = "Set aside time for focused study or meditation",
                        fontFamily = GaramondFont,
                        fontSize = 13.sp,
                        color = ParchmentDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            SectionLabel("DURATION")
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                durationOptions.forEach { option ->
                    val isSelected = option == minutes
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AntiqueGold else ChamberSurfaceLight)
                            .border(1.dp, if (isSelected) GoldBright else ChamberBorder, RoundedCornerShape(8.dp))
                            .clickable { minutes = option }
                            .padding(vertical = 8.dp)
                            .testTag("contemplation_duration_$option"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$option min",
                            fontFamily = CinzelFont,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp,
                            color = if (isSelected) NigredoBlack else ParchmentLight,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            SectionLabel("SOUND")
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ContemplationSound.entries.toList()) { option ->
                    val isSelected = option == sound
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) AntiqueGold else ChamberSurfaceLight)
                            .border(1.dp, if (isSelected) GoldBright else ChamberBorder, RoundedCornerShape(16.dp))
                            .clickable { soundName = option.name }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${option.glyph}  ${option.label}",
                            fontFamily = CinzelFont,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NigredoBlack else ParchmentLight
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = sound.description,
                fontFamily = GaramondFont,
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = ParchmentDark
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Stay with the Work: leaving the app pauses the session.",
                fontFamily = GaramondFont,
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                color = CitrinitasAmber
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onBegin(ContemplationConfig(minutes, sound)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contemplation_begin_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = NigredoBlack),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "BEGIN $minutes-MINUTE CONTEMPLATION",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = CinzelFont,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = CitrinitasAmber,
        letterSpacing = 0.5.sp
    )
}

/**
 * Full-screen contemplation session: countdown ring, focus prompt, pause / end controls,
 * and ritual sounds. Shown above the whole app so the timer keeps running uninterrupted.
 *
 * @param focusPrompt optional text to contemplate (today's Oracle exercise)
 * @param onRecordReflection called after completion when the user wants to journal
 */
@Composable
fun ContemplationSessionScreen(
    config: ContemplationConfig,
    focusPrompt: String?,
    onClose: () -> Unit,
    onRecordReflection: (minutes: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val audio = remember { AlchemicalAudioEngine.getInstance() }
    val totalMs = config.minutes * 60_000L

    var remainingMs by remember { mutableLongStateOf(totalMs) }
    var isRunning by remember { mutableStateOf(true) }
    var isComplete by remember { mutableStateOf(false) }
    var halfwayRung by remember { mutableStateOf(false) }
    // Set when the session was paused automatically because the user left the app
    var pausedByLeaving by remember { mutableStateOf(false) }

    // Remember whether the top-bar laboratory ambience was already on, to restore it afterwards
    val ambientWasPlaying = remember { audio.isAmbientPlaying.value }
    val usesBells = config.sound != ContemplationSound.SILENCE

    fun sessionSoundOn() {
        when (config.sound) {
            ContemplationSound.DRONE -> {
                audio.stopAmbientSoundscape()
                audio.startMeditationDrone()
            }
            ContemplationSound.LABORATORY -> audio.startAmbientSoundscape()
            ContemplationSound.BELLS, ContemplationSound.SILENCE -> audio.stopAmbientSoundscape()
        }
    }

    fun sessionSoundOff() {
        audio.stopMeditationDrone()
        if (config.sound == ContemplationSound.LABORATORY) audio.stopAmbientSoundscape()
    }

    // Opening ritual, and clean-up whenever the session screen goes away
    DisposableEffect(Unit) {
        sessionSoundOn()
        if (usesBells) audio.playRitualBells(1)
        onDispose {
            sessionSoundOff()
            if (ambientWasPlaying) audio.startAmbientSoundscape()
        }
    }

    // Keep the screen awake while the session is in progress
    val view = LocalView.current
    DisposableEffect(isRunning, isComplete) {
        view.keepScreenOn = isRunning && !isComplete
        onDispose { view.keepScreenOn = false }
    }

    // Countdown based on the real elapsed clock, so it stays accurate even if frames are skipped
    LaunchedEffect(isRunning) {
        if (!isRunning || isComplete) return@LaunchedEffect
        val endAt = SystemClock.elapsedRealtime() + remainingMs
        while (remainingMs > 0) {
            remainingMs = (endAt - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
            if (!halfwayRung && config.minutes >= 10 && remainingMs <= totalMs / 2) {
                halfwayRung = true
                if (usesBells) audio.playRitualBells(1)
            }
            delay(200)
        }
        isComplete = true
        isRunning = false
        sessionSoundOff()
        if (usesBells) audio.playRitualBells(3)
    }

    // Leaving the app (home button, switching apps, screen off) pauses the session.
    // Contemplation asks for undivided attention, so the timer does not run in the background.
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        if (isRunning && !isComplete) {
            isRunning = false
            pausedByLeaving = true
            sessionSoundOff()
        }
    }

    BackHandler(onBack = onClose)

    val progress = 1f - (remainingMs.toFloat() / totalMs.toFloat())
    val totalSeconds = ((remainingMs + 999) / 1000).toInt()
    val timeText = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireCover)
            .testTag("contemplation_session_screen")
            // Absorb taps on empty areas so they don't reach the app screens underneath
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isComplete) "THE WORK IS COMPLETE" else "CONTEMPLATIO",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                letterSpacing = 2.sp,
                color = AntiqueGold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${config.minutes} minutes • ${config.sound.label}",
                fontFamily = GaramondFont,
                fontStyle = FontStyle.Italic,
                fontSize = 15.sp,
                color = ParchmentDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Countdown ring
            Box(modifier = Modifier.size(250.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 10.dp.toPx()
                    val inset = stroke / 2f
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    drawArc(
                        color = ChamberBorder,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = stroke)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(listOf(GoldBurnished, GoldBright, AntiqueGold)),
                        startAngle = -90f,
                        sweepAngle = 360f * progress.coerceIn(0f, 1f),
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isComplete) "🜛" else timeText,
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isComplete) 56.sp else 46.sp,
                        color = GoldBright
                    )
                    Text(
                        text = when {
                            isComplete -> "Session complete"
                            isRunning -> "Contemplating"
                            else -> "Paused"
                        },
                        fontFamily = GaramondFont,
                        fontSize = 14.sp,
                        color = ParchmentLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (!focusPrompt.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ChamberSurface)
                        .border(1.dp, AntiqueGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "☉ TODAY'S CONTEMPLATION",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp,
                        color = CitrinitasAmber
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = focusPrompt,
                        fontFamily = GaramondFont,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                        color = ParchmentLight
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (pausedByLeaving && !isRunning && !isComplete) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(WaxSealRed.copy(alpha = 0.25f))
                        .border(1.dp, WaxSealRed, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                        .testTag("contemplation_paused_by_leaving")
                ) {
                    Text(
                        text = "SESSION PAUSED",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = GoldBright
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Thou didst leave Hermetica, so the timer was paused. The Work asks for " +
                            "an undivided mind: set distractions aside, then resume when ready.",
                        fontFamily = GaramondFont,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        color = ParchmentLight
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isComplete) {
                Button(
                    onClick = { onRecordReflection(config.minutes) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = NigredoBlack),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("RECORD A REFLECTION", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AntiqueGold),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AntiqueGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CLOSE", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (isRunning) {
                                isRunning = false
                                sessionSoundOff()
                            } else {
                                isRunning = true
                                pausedByLeaving = false
                                sessionSoundOn()
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = NigredoBlack),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRunning) "PAUSE" else "RESUME",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ParchmentLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ChamberBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("END", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Leaving the app pauses the session. The screen stays on while it runs.",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = ParchmentDark.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
