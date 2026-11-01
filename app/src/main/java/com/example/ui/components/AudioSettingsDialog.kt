package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AlchemicalAudioEngine
import com.example.ui.theme.*

@Composable
fun AudioSettingsDialog(
    onDismiss: () -> Unit
) {
    val audioEngine = remember { AlchemicalAudioEngine.getInstance() }
    val isMuted by audioEngine.isMuted.collectAsState()
    val isAmbientPlaying by audioEngine.isAmbientPlaying.collectAsState()
    val masterVolume by audioEngine.masterVolume.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ChamberDark,
        modifier = Modifier.testTag("audio_settings_dialog"),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HARMONIA ALCHEMICA",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = AntiqueGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "17th-Century Atmospheric Audio Engine",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    color = ParchmentDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Continuous Ambient Soundscape Toggle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isAmbientPlaying) AntiqueGold.copy(alpha = 0.15f) else ChamberSurface)
                        .border(1.dp, if (isAmbientPlaying) GoldBright else ChamberBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ambient Laboratory Drone",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isAmbientPlaying) GoldBright else ParchmentLight
                            )
                            Text(
                                text = "Bubbling alembics, athanor embers & distant bells",
                                fontFamily = GaramondFont,
                                fontSize = 11.sp,
                                color = ParchmentDark
                            )
                        }

                        Switch(
                            checked = isAmbientPlaying,
                            onCheckedChange = { audioEngine.toggleAmbient() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldBright,
                                checkedTrackColor = WaxSealRed,
                                uncheckedThumbColor = ParchmentDark,
                                uncheckedTrackColor = ChamberBorder
                            )
                        )
                    }
                }

                // Master Volume Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isMuted) androidx.compose.material.icons.Icons.AutoMirrored.Filled.VolumeMute else androidx.compose.material.icons.Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = AntiqueGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Acoustic Volume:", fontFamily = CinzelFont, fontSize = 11.sp, color = ParchmentLight)
                        }
                        Text("${(masterVolume * 100).toInt()}%", fontFamily = CinzelFont, fontSize = 11.sp, color = CitrinitasAmber)
                    }

                    Slider(
                        value = masterVolume,
                        onValueChange = { audioEngine.setMasterVolume(it) },
                        colors = SliderDefaults.colors(
                            thumbColor = GoldBright,
                            activeTrackColor = AntiqueGold,
                            inactiveTrackColor = ChamberBorder
                        )
                    )
                }

                // Test / Preview Individual Atmospheric Sounds
                Text(
                    text = "INDIVIDUAL ACOUSTIC RESONANCES:",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = CitrinitasAmber,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SoundSampleButton(
                        label = "Monastery Bell",
                        glyph = "🔔",
                        modifier = Modifier.weight(1f),
                        onClick = { audioEngine.playBellChime() }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SoundSampleButton(
                        label = "Alembic Bubbles",
                        glyph = "🜔",
                        modifier = Modifier.weight(1f),
                        onClick = { audioEngine.playBubblingCauldron() }
                    )
                    SoundSampleButton(
                        label = "Wax Seal Stamp",
                        glyph = "🔴",
                        modifier = Modifier.weight(1f),
                        onClick = { audioEngine.playWaxSeal() }
                    )
                }

                SoundSampleButton(
                    label = "Pythagorean Transmutation Chord",
                    glyph = "🜛",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { audioEngine.playTransmutationHarmonic() }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChamberDark,
                    contentColor = AntiqueGold
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder))
                )
            ) {
                Text("Seal Sanctuary", fontFamily = CinzelFont, fontSize = 11.sp)
            }
        }
    )
}

@Composable
private fun SoundSampleButton(
    label: String,
    glyph: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ChamberSurfaceLight)
            .border(1.dp, ChamberBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(glyph, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontFamily = CinzelFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = ParchmentLight,
                textAlign = TextAlign.Center
            )
        }
    }
}
