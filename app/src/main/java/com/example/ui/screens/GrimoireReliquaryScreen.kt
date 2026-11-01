package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollectibleArtifact
import com.example.data.journal.displayGlyph
import com.example.ui.journal.JournalLaunch
import com.example.data.repository.HermeticRepository
import com.example.ui.components.ParchmentScroll
import com.example.ui.components.WaxSealBadge
import com.example.ui.sigil.PersonalSigilForgeModule
import com.example.ui.theme.*

@Composable
fun GrimoireReliquaryScreen(
    repository: HermeticRepository,
    onOpenJournal: (JournalLaunch) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val adeptProfile by repository.adeptProfile.collectAsState()
    val artifacts by repository.artifacts.collectAsState()
    // The Grimoire shows the app's single Hermetic Journal (Room-backed, persistent)
    // (null until Room's first emission, so the empty message doesn't flash while loading)
    val loadedJournal by repository.journalReflections.collectAsState(initial = null)
    val journalEntries = loadedJournal.orEmpty()
    val personalSigils by repository.personalSigils.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Cabinet of Relics, 1: Cryptic Grimoire, 2: Sigil Forge
    var selectedArtifactForDetail by remember { mutableStateOf<CollectibleArtifact?>(null) }

    Column(modifier = modifier.fillMaxSize().background(GrimoireCover)) {
        // Top 3-Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { activeTab = 0 },
                modifier = Modifier.weight(1f).testTag("relics_tab_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == 0) AntiqueGold else ChamberSurface,
                    contentColor = if (activeTab == 0) NigredoBlack else ParchmentLight
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Text("Relics", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
                onClick = { activeTab = 1 },
                modifier = Modifier.weight(1f).testTag("grimoire_tab_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == 1) AntiqueGold else ChamberSurface,
                    contentColor = if (activeTab == 1) NigredoBlack else ParchmentLight
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Text("Grimoire", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
                onClick = { activeTab = 2 },
                modifier = Modifier.weight(1f).testTag("sigil_forge_tab_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == 2) AntiqueGold else ChamberSurface,
                    contentColor = if (activeTab == 2) NigredoBlack else ParchmentLight
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Text("✦ Sigil Forge", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        if (activeTab == 2) {
            PersonalSigilForgeModule(
                repository = repository,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
            // Adept Rank & Initiatic Progression Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("adept_progress_card"),
                    colors = CardDefaults.cardColors(containerColor = ChamberDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder))),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = adeptProfile.rankTitle.uppercase(),
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp,
                                    color = AntiqueGold
                                )
                                Text(
                                    text = "Degree ${adeptProfile.degreeLevel} • ${adeptProfile.currentLunarPhase}",
                                    fontFamily = GaramondFont,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 13.sp,
                                    color = ParchmentDark
                                )
                            }
                            WaxSealBadge(glyph = "☉", size = 42.dp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Experience Progress to next degree
                        val currentExpInDegree = adeptProfile.experiencePoints % 200
                        val progressFraction = (currentExpInDegree / 200f).coerceIn(0f, 1f)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("INITIATIC GNOSIS", fontFamily = CinzelFont, fontSize = 10.sp, color = CitrinitasAmber)
                            Text("${adeptProfile.experiencePoints} XP (Degree ${adeptProfile.degreeLevel})", fontFamily = CinzelFont, fontSize = 10.sp, color = AntiqueGold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AntiqueGold,
                            trackColor = ChamberSurfaceLight
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatPill(title = "Study Streak", value = "${adeptProfile.daysStreak}d", icon = "☾")
                            StatPill(title = "Relics Found", value = "${adeptProfile.artifactsFound.size}/8", icon = "🜫")
                            StatPill(title = "Laws Sealed", value = "${adeptProfile.principlesMastered.size}/7", icon = "⚚")
                            StatPill(title = "Sigils Forged", value = "${personalSigils.size}", icon = "🜚")
                        }
                    }
                }
            }

            // Tab 0: Cabinet of Collectible Relics
            if (activeTab == 0) {
                item {
                    Text(
                        text = "THE ADEPT'S RELIQUARY",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CitrinitasAmber,
                        letterSpacing = 1.sp
                    )
                }

                items(artifacts.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { artifact ->
                            Box(modifier = Modifier.weight(1f)) {
                                ArtifactCard(
                                    artifact = artifact,
                                    onClick = { selectedArtifactForDetail = artifact }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                // Tab 1: Cryptic Grimoire Journal
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FOLIOS OF THE CRYPTIC GRIMOIRE",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CitrinitasAmber,
                            letterSpacing = 1.sp,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        )

                        Button(
                            onClick = { onOpenJournal(JournalLaunch(startWithNewEntry = true)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WaxSealRed,
                                contentColor = ParchmentLight
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Create, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Inscribe", fontFamily = CinzelFont, fontSize = 11.sp)
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = { onOpenJournal(JournalLaunch()) },
                        modifier = Modifier.fillMaxWidth().testTag("grimoire_open_journal_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AntiqueGold),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AntiqueGold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Open Hermetic Journal • filters & progress",
                            fontFamily = CinzelFont,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                if (loadedJournal != null && journalEntries.isEmpty()) {
                    item {
                        Text(
                            text = "No folios yet. Inscribe a reflection, or save an Oracle decree, " +
                                "deciphered axiom or sigil to see it here.",
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
                            color = ParchmentDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                        )
                    }
                }

                items(journalEntries, key = { it.id }) { entry ->
                    ParchmentScroll(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_entry_${entry.id}"),
                        title = entry.title,
                        latinMotto = grimoireDateFormat.format(java.util.Date(entry.createdAt)),
                        sealGlyph = entry.displayGlyph,
                        onSealClick = { onOpenJournal(JournalLaunch(entryId = entry.id)) }
                    ) {
                        Text(
                            text = "CATEGORY: ${entry.theme.uppercase()}",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = WaxSealRed
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = entry.content,
                            fontFamily = GaramondFont,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = IronGallInk,
                            maxLines = 8,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Open folio ›",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = WaxSealRed,
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable { onOpenJournal(JournalLaunch(entryId = entry.id)) }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Detailed Artifact Inspection Dialog
        selectedArtifactForDetail?.let { artifact ->
            AlertDialog(
                onDismissRequest = { selectedArtifactForDetail = null },
                containerColor = ChamberDark,
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        WaxSealBadge(glyph = artifact.glyph, size = 52.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = artifact.name,
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AntiqueGold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = artifact.latinMotto,
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = CitrinitasAmber
                        )
                    }
                },
                text = {
                    Column {
                        if (artifact.isUnlocked) {
                            Text(
                                text = "ANCIENT ORIGIN:",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = WaxSealRed
                            )
                            Text(
                                text = artifact.ancientOrigin,
                                fontFamily = GaramondFont,
                                fontSize = 14.sp,
                                color = ParchmentDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "ESOTERIC VIRTUE:",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = AntiqueGold
                            )
                            Text(
                                text = artifact.description,
                                fontFamily = GaramondFont,
                                fontSize = 14.sp,
                                color = ParchmentLight
                            )
                        } else {
                            Text(
                                text = "LOCKED MYSTERY:",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Quicksilver
                            )
                            Text(
                                text = artifact.unlockRequirement,
                                fontFamily = GaramondFont,
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp,
                                color = ParchmentDark
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedArtifactForDetail = null }) {
                        Text("Close", fontFamily = CinzelFont, color = AntiqueGold)
                    }
                }
            )
        }

        }
    }
}
}

@Composable
private fun StatPill(title: String, value: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 16.sp, color = AntiqueGold)
        Text(text = value, fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ParchmentLight)
        Text(text = title, fontFamily = GaramondFont, fontSize = 11.sp, color = ParchmentDark)
    }
}

@Composable
private fun ArtifactCard(
    artifact: CollectibleArtifact,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() }
            .testTag("artifact_card_${artifact.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (artifact.isUnlocked) ChamberSurface else ChamberDark.copy(alpha = 0.6f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (artifact.isUnlocked) listOf(AntiqueGold, ChamberBorder) else listOf(ChamberBorder, Color.Black)
            )
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (artifact.isUnlocked) {
                WaxSealBadge(glyph = artifact.glyph, size = 44.dp)
                Text(
                    text = artifact.name,
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = ParchmentLight,
                    maxLines = 2
                )
                Text(
                    text = "✦ UNLOCKED",
                    fontFamily = CinzelFont,
                    fontSize = 10.sp,
                    color = AntiqueGold
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ChamberSurfaceLight)
                        .border(1.dp, ChamberBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = FadedInk, modifier = Modifier.size(20.dp))
                }
                Text(
                    text = "Dormant Relic",
                    fontFamily = CinzelFont,
                    fontSize = 12.sp,
                    color = FadedInk
                )
                Text(
                    text = "Tap for Clue",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    color = ParchmentDark.copy(alpha = 0.6f)
                )
            }
        }
    }
}

private val grimoireDateFormat =
    java.text.SimpleDateFormat("EEEE d MMMM yyyy", java.util.Locale.getDefault())
