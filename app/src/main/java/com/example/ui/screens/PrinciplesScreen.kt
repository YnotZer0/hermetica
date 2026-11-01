package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.visualizations.HermeticVisualizerModule
import com.example.data.model.HermeticPrinciple
import com.example.data.repository.HermeticData
import com.example.data.repository.HermeticRepository
import com.example.ui.components.HermeticWheelCanvas
import com.example.ui.components.ParchmentScroll
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun PrinciplesScreen(
    repository: HermeticRepository,
    modifier: Modifier = Modifier
) {
    val principles by repository.principles.collectAsState()
    val adeptProfile by repository.adeptProfile.collectAsState()
    var selectedPrincipleId by remember { mutableIntStateOf(1) }

    val currentPrinciple = principles.find { it.id == selectedPrincipleId } ?: principles.first()
    val isMastered = adeptProfile.principlesMastered.contains(currentPrinciple.id)

    Box(modifier = modifier.fillMaxSize().background(GrimoireCover)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SEPTEM PRINCIPIA HERMETICA",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 1.5.sp,
                        color = AntiqueGold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "The Seven Cosmic Laws of the Kybalion",
                        fontFamily = GaramondFont,
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp,
                        color = ParchmentDark,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Interactive Hermetic Orbital Wheel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ChamberDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ChamberBorder, AntiqueGold))),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CELESTIAL WHEEL OF THE LAWS",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = CitrinitasAmber
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HermeticWheelCanvas(
                            modifier = Modifier
                                .size(240.dp)
                                .testTag("hermetic_wheel_canvas"),
                            activePrincipleId = selectedPrincipleId,
                            onPrincipleSelected = { selectedPrincipleId = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the wheel or tabs below to align with each cosmic law",
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = ParchmentDark.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Principle Selector Horizontal Bar
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("principles_tab_row")
                ) {
                    items(principles) { principle ->
                        val isSelected = principle.id == selectedPrincipleId
                        val masteredThis = adeptProfile.principlesMastered.contains(principle.id)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) AntiqueGold else ChamberSurface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) GoldBright else ChamberBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedPrincipleId = principle.id }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = principle.glyph,
                                    fontSize = 14.sp,
                                    color = if (isSelected) NigredoBlack else AntiqueGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = principle.name.replace("The Principle of ", ""),
                                    fontFamily = CinzelFont,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = if (isSelected) NigredoBlack else ParchmentLight
                                )
                                if (masteredThis) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Mastered",
                                        tint = if (isSelected) WaxSealRed else EmeraldHermetic,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Principle Detail Scroll
            item {
                ParchmentScroll(
                    modifier = Modifier.testTag("principle_detail_scroll"),
                    title = currentPrinciple.name,
                    latinMotto = currentPrinciple.latinTitle,
                    sealGlyph = currentPrinciple.glyph
                ) {
                    // Axiom Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(WaxSealRed.copy(alpha = 0.12f))
                            .border(1.2.dp, WaxSealRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "“${currentPrinciple.aphorism}”",
                                fontFamily = GaramondFont,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = WaxSealRed,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— ${currentPrinciple.axiomSource}",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = FadedInk,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Deep Hermetic Philosophy
                    Text(
                        text = "THE HERMETIC MYSTERY",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.8.sp,
                        color = IronGallInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentPrinciple.deepPhilosophy,
                        fontFamily = GaramondFont,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = FadedInk
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Psychological Transmutation (Jungian lens)
                    Text(
                        text = "PSYCHIC TRANSMUTATION",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.8.sp,
                        color = WaxSealRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentPrinciple.psychologicalApplication,
                        fontFamily = GaramondFont,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = FadedInk
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Master Button
                    Button(
                        onClick = {
                            repository.markPrincipleStudied(currentPrinciple.id)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("master_principle_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMastered) EmeraldHermetic else ChamberDark,
                            contentColor = if (isMastered) ParchmentLight else AntiqueGold
                        )
                    ) {
                        Icon(
                            if (isMastered) Icons.Default.CheckCircle else Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isMastered) "Principle Mastered (+50 XP)" else "Attune & Seal Law (+50 XP)",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Dedicated Interactive Hermetic Visualization Module
            item {
                HermeticVisualizerModule(
                    selectedPrincipleId = selectedPrincipleId,
                    onSelectPrinciple = { selectedPrincipleId = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
