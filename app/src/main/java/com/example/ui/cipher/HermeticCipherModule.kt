package com.example.ui.cipher

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AlchemicalAudioEngine
import com.example.data.model.HermeticCipherPuzzle
import com.example.data.model.KnowledgeNode
import com.example.data.repository.HermeticRepository
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class CipherViewMode {
    DECODING_SCROLL,
    KNOWLEDGE_CONSTELLATION
}

/**
 * Hermetic Cipher Module:
 * Presents daily decoding puzzles from The Kybalion using the 17th-century weather-beaten
 * scroll UI with rotary cipher volvelle, cipher key controls, and unlocks knowledge nodes.
 */
@Composable
fun HermeticCipherModule(
    repository: HermeticRepository,
    modifier: Modifier = Modifier
) {
    val audioEngine = remember { AlchemicalAudioEngine.getInstance() }
    val puzzles by repository.ciphers.collectAsState()
    val activeCipher by repository.activeCipher.collectAsState()
    val knowledgeNodes by repository.knowledgeNodes.collectAsState()
    val adeptProfile by repository.adeptProfile.collectAsState()

    var viewMode by remember { mutableStateOf(CipherViewMode.DECODING_SCROLL) }
    var selectedCipherId by remember { mutableStateOf(activeCipher.id) }
    val currentPuzzle = puzzles.find { it.id == selectedCipherId } ?: activeCipher

    val isSolved = adeptProfile.solvedCiphers.contains(currentPuzzle.id)
    var currentShiftKey by remember(currentPuzzle.id) {
        mutableIntStateOf(if (isSolved) currentPuzzle.cipherKey else 0)
    }

    var isScrollUnrolled by remember { mutableStateOf(true) }
    var showHintDialog by remember { mutableStateOf(false) }
    var selectedNodeDetail by remember { mutableStateOf<KnowledgeNode?>(null) }
    var celebrationMessage by remember { mutableStateOf<String?>(null) }

    // Live decoded text preview
    val liveDecodedText = remember(currentPuzzle, currentShiftKey) {
        decodeCaesar(currentPuzzle.scrambledText, currentShiftKey)
    }

    // Check if the current dial solves the puzzle
    val isCurrentlyDecoded = liveDecodedText.equals(currentPuzzle.solutionText, ignoreCase = true)

    // Handle solving victory trigger
    LaunchedEffect(isCurrentlyDecoded) {
        if (isCurrentlyDecoded && !isSolved) {
            audioEngine.playWaxSeal()
            audioEngine.playBellChime()
            audioEngine.playTransmutationHarmonic()
            val newlySolved = repository.solveCipher(currentPuzzle.id)
            if (newlySolved) {
                celebrationMessage = "Axiom Deciphered! Knowledge Node Unlocked (+${currentPuzzle.rewardXp} XP)"
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("hermetic_cipher_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 17th Century Module Title Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🜎",
                        fontSize = 20.sp,
                        color = AntiqueGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CYPHRA HERMETICA",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        letterSpacing = 2.sp,
                        color = AntiqueGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🜎",
                        fontSize = 20.sp,
                        color = AntiqueGold
                    )
                }

                Text(
                    text = "Daily Kybalion Cryptograms & Gnosis Spheres",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = ParchmentDark
                )
            }
        }

        // Sub-Navigation Tabs: [Decoding Scroll] vs [Knowledge Constellation]
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ChamberSurface)
                    .border(1.dp, ChamberBorder, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Decoding Scroll tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (viewMode == CipherViewMode.DECODING_SCROLL) AntiqueGold else Color.Transparent)
                        .clickable {
                            viewMode = CipherViewMode.DECODING_SCROLL
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📜",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cipher Scroll",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (viewMode == CipherViewMode.DECODING_SCROLL) NigredoBlack else ParchmentLight
                        )
                    }
                }

                // Knowledge Constellation tab
                val unlockedCount = knowledgeNodes.count { it.isUnlocked }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (viewMode == CipherViewMode.KNOWLEDGE_CONSTELLATION) AntiqueGold else Color.Transparent)
                        .clickable {
                            viewMode = CipherViewMode.KNOWLEDGE_CONSTELLATION
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🜚",
                            fontSize = 14.sp,
                            color = if (viewMode == CipherViewMode.KNOWLEDGE_CONSTELLATION) NigredoBlack else GoldBright
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gnosis Nodes ($unlockedCount/7)",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (viewMode == CipherViewMode.KNOWLEDGE_CONSTELLATION) NigredoBlack else ParchmentLight
                        )
                    }
                }
            }
        }

        if (viewMode == CipherViewMode.DECODING_SCROLL) {
            // Day / Puzzle Selection Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "THE SEVEN KYBALION CIPHERS",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = AntiqueGold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(puzzles) { puzzle ->
                            val isSelected = puzzle.id == selectedCipherId
                            val isPuzzleSolved = adeptProfile.solvedCiphers.contains(puzzle.id)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) WaxSealRed.copy(alpha = 0.85f)
                                        else ChamberSurface
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) GoldBright else ChamberBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedCipherId = puzzle.id
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isPuzzleSolved) "✦" else puzzle.glyphSymbol,
                                        fontSize = 13.sp,
                                        color = if (isPuzzleSolved) GoldBright else ParchmentDark
                                    )
                                    Text(
                                        text = "Day ${puzzle.dayNumber}",
                                        fontFamily = CinzelFont,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = if (isSelected) ParchmentLight else ParchmentBase
                                    )
                                    if (isPuzzleSolved) {
                                        Text(
                                            text = "✓",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldBright
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // The Weather-Beaten Kybalion Cipher Scroll
            item {
                CipherWeatherBeatenScroll(
                    puzzle = currentPuzzle,
                    liveText = liveDecodedText,
                    isDecoded = isCurrentlyDecoded || isSolved,
                    isUnrolled = isScrollUnrolled,
                    currentShiftKey = currentShiftKey,
                    onToggleUnroll = {
                        isScrollUnrolled = !isScrollUnrolled
                    },
                    onBreakSeal = {
                        audioEngine.playWaxSeal()
                        isScrollUnrolled = true
                    },
                    onViewNode = {
                        val node = knowledgeNodes.find { it.id == currentPuzzle.associatedNodeId }
                        selectedNodeDetail = node
                    }
                )
            }

            // Interactive Cipher Deciphering Apparatus
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cipher_volvelle_card"),
                    colors = CardDefaults.cardColors(containerColor = ChamberSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Title & Clue Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "VOLVELLE OF HERMES",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp,
                                    color = AntiqueGold
                                )
                                Text(
                                    text = "Ruler: ${currentPuzzle.planetaryRuler}",
                                    fontFamily = GaramondFont,
                                    fontSize = 12.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = CitrinitasAmber
                                )
                            }

                            // Hint / Clue Button
                            IconButton(
                                onClick = {
                                    audioEngine.playBellChime()
                                    showHintDialog = true
                                }
                            ) {
                                Text(
                                    text = "🔍",
                                    fontSize = 18.sp
                                )
                            }
                        }

                        // Rotary Volvelle Dial Visualization
                        CipherVolvelleDial(
                            shiftKey = currentShiftKey,
                            isSolved = isCurrentlyDecoded || isSolved
                        )

                        // Key Controller Controls
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ALCHEMICAL SHIFT KEY:",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = ParchmentDark
                                )

                                Text(
                                    text = "DEGREE ${toRomanNumeral(currentShiftKey)} (${currentShiftKey})",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isCurrentlyDecoded) GoldBright else AntiqueGold
                                )
                            }

                            // Interactive Shift Slider (0..25)
                            Slider(
                                value = currentShiftKey.toFloat(),
                                onValueChange = {
                                    val newKey = it.toInt()
                                    if (newKey != currentShiftKey) {
                                        audioEngine.playCipherClick()
                                        currentShiftKey = newKey
                                    }
                                },
                                valueRange = 0f..25f,
                                steps = 24,
                                colors = SliderDefaults.colors(
                                    thumbColor = AntiqueGold,
                                    activeTrackColor = AntiqueGold,
                                    inactiveTrackColor = ChamberBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Quick Stepper & Common Keys
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        audioEngine.playCipherClick()
                                        currentShiftKey = (currentShiftKey - 1 + 26) % 26
                                    },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ChamberBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ParchmentBase)
                                ) {
                                    Text("⟵ -1 Deg", fontFamily = CinzelFont, fontSize = 10.sp)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf(1, 2, 3, 4, 5, 6, 7).forEach { k ->
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(if (currentShiftKey == k) AntiqueGold else ChamberDark)
                                                .border(1.dp, if (currentShiftKey == k) GoldBright else ChamberBorder, CircleShape)
                                                .clickable {
                                                    audioEngine.playCipherClick()
                                                    currentShiftKey = k
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = k.toString(),
                                                fontFamily = CinzelFont,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (currentShiftKey == k) NigredoBlack else ParchmentBase
                                            )
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        audioEngine.playCipherClick()
                                        currentShiftKey = (currentShiftKey + 1) % 26
                                    },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ChamberBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ParchmentBase)
                                ) {
                                    Text("+1 Deg ⟶", fontFamily = CinzelFont, fontSize = 10.sp)
                                }
                            }
                        }

                        // Action / Reveal Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!isCurrentlyDecoded && !isSolved) {
                                Button(
                                    onClick = {
                                        audioEngine.playCipherClick()
                                        currentShiftKey = currentPuzzle.cipherKey
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = WaxSealRed.copy(alpha = 0.8f))
                                ) {
                                    Text(
                                        text = "Apply Astrological Key",
                                        fontFamily = CinzelFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = ParchmentLight
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        val node = knowledgeNodes.find { it.id == currentPuzzle.associatedNodeId }
                                        selectedNodeDetail = node
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                                ) {
                                    Text(
                                        text = "Inspect Unlocked Knowledge Node ✦",
                                        fontFamily = CinzelFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = NigredoBlack
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Knowledge Constellation Tree View
            item {
                KnowledgeNodesConstellationView(
                    nodes = knowledgeNodes,
                    onSelectNode = { node ->
                        selectedNodeDetail = node
                    }
                )
            }
        }

        // Celebratory floating notice
        celebrationMessage?.let { msg ->
            item {
                LaunchedEffect(msg) {
                    delay(3500)
                    celebrationMessage = null
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AntiqueGold)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = msg,
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        color = NigredoBlack,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Esoteric Clue / Loupe of Paracelsus Hint Modal
    if (showHintDialog) {
        AlertDialog(
            onDismissRequest = { showHintDialog = false },
            containerColor = ChamberDark,
            title = {
                Text(
                    text = "LOUPE OF PARACELSUS",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AntiqueGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Source: ${currentPuzzle.kybalionChapter}",
                        fontFamily = GaramondFont,
                        fontStyle = FontStyle.Italic,
                        color = CitrinitasAmber,
                        fontSize = 13.sp
                    )
                    Text(
                        text = currentPuzzle.esotericClue,
                        fontFamily = GaramondFont,
                        color = ParchmentLight,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    HorizontalDivider(color = ChamberBorder, thickness = 1.dp)
                    Text(
                        text = "Historical Marginalia: ${currentPuzzle.historicalContext}",
                        fontFamily = GaramondFont,
                        fontStyle = FontStyle.Italic,
                        color = ParchmentDark,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHintDialog = false }) {
                    Text("Close Loupe", fontFamily = CinzelFont, color = AntiqueGold)
                }
            }
        )
    }

    // Knowledge Node Detail Dialog
    selectedNodeDetail?.let { node ->
        KnowledgeNodeDetailDialog(
            node = node,
            onDismiss = { selectedNodeDetail = null },
            onInscribe = {
                audioEngine.playWaxSeal()
                repository.addJournalEntry(
                    title = "Gnosis: ${node.title}",
                    category = "Hermetism",
                    content = "Axiom: ${node.kybalionAxiom}\n\nSecret Gnosis: ${node.secretGnosis}\n\nArt: ${node.practicalHermeticArt}\n\nPsychology: ${node.psychologicalInterpretation}",
                    sealGlyph = node.glyph
                )
                selectedNodeDetail = null
                celebrationMessage = "Inscribed into thy Grimoire (+60 XP)"
            }
        )
    }
}

/**
 * Weather-Beaten Scroll displaying the ciphered text from The Kybalion.
 */
@Composable
private fun CipherWeatherBeatenScroll(
    puzzle: HermeticCipherPuzzle,
    liveText: String,
    isDecoded: Boolean,
    isUnrolled: Boolean,
    currentShiftKey: Int,
    onToggleUnroll: () -> Unit,
    onBreakSeal: () -> Unit,
    onViewNode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cipher_scroll_component"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Wooden Roller Dowel with Brass Finials
        ScrollRollerDowelBar()

        // The Scroll Parchment Body
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
                    brush = Brush.verticalGradient(
                        listOf(
                            if (isDecoded) AntiqueGold else ChamberBorder,
                            AntiqueGold,
                            if (isDecoded) AntiqueGold else ChamberBorder
                        )
                    ),
                    shape = androidx.compose.ui.graphics.RectangleShape
                )
        ) {
            // Antique Aged Canvas Framing
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height
                val inset = 10.dp.toPx()

                // Aged deckled margin line
                drawRect(
                    color = if (isDecoded) AntiqueGold.copy(alpha = 0.4f) else FadedInk.copy(alpha = 0.22f),
                    topLeft = Offset(inset, inset),
                    size = Size(w - inset * 2, h - inset * 2),
                    style = Stroke(1.2f)
                )

                // 17th Century corner flourishes
                val cLen = 14.dp.toPx()
                val cornerColor = if (isDecoded) GoldBright else AntiqueGold
                drawLine(cornerColor, Offset(inset, inset + cLen), Offset(inset, inset), strokeWidth = 2f)
                drawLine(cornerColor, Offset(inset, inset), Offset(inset + cLen, inset), strokeWidth = 2f)
                drawLine(cornerColor, Offset(w - inset - cLen, inset), Offset(w - inset, inset), strokeWidth = 2f)
                drawLine(cornerColor, Offset(w - inset, inset), Offset(w - inset, inset + cLen), strokeWidth = 2f)
                drawLine(cornerColor, Offset(inset, h - inset - cLen), Offset(inset, h - inset), strokeWidth = 2f)
                drawLine(cornerColor, Offset(inset, h - inset), Offset(inset + cLen, h - inset), strokeWidth = 2f)
                drawLine(cornerColor, Offset(w - inset - cLen, h - inset), Offset(w - inset, h - inset), strokeWidth = 2f)
                drawLine(cornerColor, Offset(w - inset, h - inset), Offset(w - inset, h - inset - cLen), strokeWidth = 2f)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header Ribbon with Wax Seal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CYPHRA ANCIENT SCROLL",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp,
                            color = WaxSealRed
                        )
                        Text(
                            text = puzzle.title,
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = IronGallInk
                        )
                    }

                    WaxSealBadge(
                        glyph = if (isDecoded) "✦" else puzzle.glyphSymbol,
                        size = 38.dp,
                        onClick = {
                            if (!isUnrolled) {
                                onBreakSeal()
                            } else {
                                onToggleUnroll()
                            }
                        }
                    )
                }

                Text(
                    text = puzzle.kybalionChapter,
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = FadedInk
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scroll Content
                AnimatedVisibility(
                    visible = isUnrolled,
                    enter = fadeIn(tween(350)) + expandVertically(),
                    exit = fadeOut(tween(250)) + shrinkVertically()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Status Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isDecoded) AntiqueGold.copy(alpha = 0.25f)
                                    else WaxSealRed.copy(alpha = 0.12f)
                                )
                                .border(
                                    1.dp,
                                    if (isDecoded) AntiqueGold else WaxSealRed.copy(alpha = 0.4f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isDecoded) "✦ CIPHER DECIPHERED ✦" else "ENCRYPTED HERMETIC FOLIO",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    color = if (isDecoded) IronGallInk else WaxSealRed
                                )

                                Text(
                                    text = if (isDecoded) "Gnosis Unlocked" else "Key: Shift ${currentShiftKey}",
                                    fontFamily = GaramondFont,
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = IronGallInk
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // The Scrambled or Decoded Kybalion Passage Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(ParchmentDark.copy(alpha = 0.45f))
                                .border(1.dp, FadedInk.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isDecoded) "“${puzzle.solutionText}”" else "“${liveText}”",
                                    fontFamily = GaramondFont,
                                    fontStyle = if (isDecoded) FontStyle.Normal else FontStyle.Italic,
                                    fontWeight = if (isDecoded) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 17.sp,
                                    lineHeight = 25.sp,
                                    color = if (isDecoded) IronGallInk else FadedInk,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (!isDecoded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Original Scramble: ${puzzle.scrambledText}",
                                        fontFamily = CinzelFont,
                                        fontSize = 9.sp,
                                        letterSpacing = 1.sp,
                                        color = FadedInk.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        if (isDecoded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextButton(onClick = onViewNode) {
                                    Text(
                                        text = "✦ View Unlocked Gnosis Sphere ✦",
                                        fontFamily = CinzelFont,
                                        fontWeight = FontWeight.Bold,
                                        color = WaxSealRed,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (!isUnrolled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleUnroll() }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "✦ Tap to Unroll Scrambled Kybalion Cipher ✦",
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
        ScrollRollerDowelBar()
    }
}

/**
 * Wooden Roller Dowel Bar with antique brass finials for the cipher parchment scroll.
 */
@Composable
private fun ScrollRollerDowelBar() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
    ) {
        val w = size.width
        val h = size.height
        val midY = h / 2f

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
 * Interactive 17th-Century Rotary Cipher Volvelle Dial.
 */
@Composable
private fun CipherVolvelleDial(
    shiftKey: Int,
    isSolved: Boolean,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = shiftKey * (360f / 26f),
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(130.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f - 4f
            val innerRadius = outerRadius * 0.70f

            // Outer Brass Ring
            drawCircle(
                brush = Brush.radialGradient(listOf(BrassChamber, ChamberDark, AntiqueGold)),
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = AntiqueGold,
                radius = outerRadius,
                center = center,
                style = Stroke(2f)
            )

            // Inner Rotating Disc
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        if (isSolved) GoldBright.copy(alpha = 0.5f) else CitrinitasAmber.copy(alpha = 0.3f),
                        ChamberDark
                    )
                ),
                radius = innerRadius,
                center = center
            )
            drawCircle(
                color = if (isSolved) GoldBright else AntiqueGold,
                radius = innerRadius,
                center = center,
                style = Stroke(1.5f)
            )

            // Center Brass Rivet
            drawCircle(
                brush = Brush.radialGradient(listOf(GoldBright, AntiqueGold, Color(0xFF332211))),
                radius = 16f,
                center = center
            )

            // Outer stationary tick marks
            for (i in 0 until 26) {
                val angleRad = (i * (360f / 26f) - 90f) * (PI / 180f)
                val p1 = Offset(
                    center.x + (outerRadius - 6f) * cos(angleRad).toFloat(),
                    center.y + (outerRadius - 6f) * sin(angleRad).toFloat()
                )
                val p2 = Offset(
                    center.x + outerRadius * cos(angleRad).toFloat(),
                    center.y + outerRadius * sin(angleRad).toFloat()
                )
                drawLine(
                    color = AntiqueGold.copy(alpha = 0.7f),
                    start = p1,
                    end = p2,
                    strokeWidth = 1.2f
                )
            }
        }

        // Planetary Glyph / Degree Emblem in Center
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isSolved) "☉" else "☿",
                fontSize = 22.sp,
                color = if (isSolved) GoldBright else AntiqueGold
            )
            Text(
                text = "Key ${toRomanNumeral(shiftKey)}",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (isSolved) GoldBright else ParchmentBase
            )
        }
    }
}

/**
 * Knowledge Nodes Constellation View:
 * Displays the 7 Spheres of Kybalion gnosis with locked/unlocked states.
 */
@Composable
private fun KnowledgeNodesConstellationView(
    nodes: List<KnowledgeNode>,
    onSelectNode: (KnowledgeNode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("knowledge_nodes_constellation"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "THE SEVEN GNOSIS SPHERES",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.2.sp,
                color = AntiqueGold
            )
            Text(
                text = "Unlocked through the solving of the daily Kybalion ciphers",
                fontFamily = GaramondFont,
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                color = ParchmentDark
            )
        }

        nodes.forEach { node ->
            val isUnlocked = node.isUnlocked
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectNode(node) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) ChamberSurface else ChamberDark
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        if (isUnlocked) listOf(GoldBright, AntiqueGold, ChamberBorder)
                        else listOf(ChamberBorder, ChamberDark)
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Node Emblem Seal
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (isUnlocked) Brush.radialGradient(listOf(AntiqueGold, WaxSealRed, ChamberDark))
                                else Brush.radialGradient(listOf(ChamberBorder, ChamberDark))
                            )
                            .border(
                                1.5.dp,
                                if (isUnlocked) GoldBright else ChamberBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isUnlocked) node.glyph else "🔒",
                            fontSize = if (isUnlocked) 20.sp else 16.sp,
                            color = if (isUnlocked) GoldBright else ParchmentDark
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = node.title,
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isUnlocked) AntiqueGold else ParchmentDark
                            )
                        }

                        Text(
                            text = node.subtitle,
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = if (isUnlocked) CitrinitasAmber else FadedInk
                        )

                        Text(
                            text = if (isUnlocked) "“${node.kybalionAxiom}”" else "Encrypted in the Daily Cipher...",
                            fontFamily = GaramondFont,
                            fontSize = 12.sp,
                            color = if (isUnlocked) ParchmentLight else FadedInk.copy(alpha = 0.6f),
                            maxLines = 1
                        )
                    }

                    Text(
                        text = if (isUnlocked) "Inspect ✦" else "Locked",
                        fontFamily = CinzelFont,
                        fontSize = 10.sp,
                        color = if (isUnlocked) GoldBright else FadedInk
                    )
                }
            }
        }
    }
}

/**
 * Knowledge Node Folio Dialog showing the full gnosis, art, and psychology of the decoded node.
 */
@Composable
private fun KnowledgeNodeDetailDialog(
    node: KnowledgeNode,
    onDismiss: () -> Unit,
    onInscribe: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ChamberDark,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = node.glyph,
                    fontSize = 28.sp,
                    color = GoldBright
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = node.title,
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AntiqueGold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = node.principleAffinity,
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = CitrinitasAmber
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sacred Axiom Box
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ParchmentDark.copy(alpha = 0.2f))
                            .border(1.dp, AntiqueGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "“${node.kybalionAxiom}”",
                            fontFamily = GaramondFont,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp,
                            color = ParchmentLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Secret Gnosis
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "THE SECRET GNOSIS",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = AntiqueGold
                        )
                        Text(
                            text = node.secretGnosis,
                            fontFamily = GaramondFont,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = ParchmentBase
                        )
                    }
                }

                // Practical Hermetic Art
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "PRACTICAL TRANSMUTATION ART",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = CitrinitasAmber
                        )
                        Text(
                            text = node.practicalHermeticArt,
                            fontFamily = GaramondFont,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = ParchmentLight
                        )
                    }
                }

                // Psychological Exegesis
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "JUNGIAN PSYCHOLOGY EXEGESIS",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = AntiqueGold
                        )
                        Text(
                            text = node.psychologicalInterpretation,
                            fontFamily = GaramondFont,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = ParchmentBase
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onInscribe,
                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
            ) {
                Text(
                    text = "Inscribe into Grimoire (+60 XP)",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NigredoBlack
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close Folio", fontFamily = CinzelFont, color = ParchmentDark)
            }
        }
    )
}

/**
 * Helper to decode Caesar cipher text by shifting backwards by [shiftKey].
 */
private fun decodeCaesar(input: String, shiftKey: Int): String {
    val k = (shiftKey % 26 + 26) % 26
    return input.map { ch ->
        when (ch) {
            in 'A'..'Z' -> {
                val offset = (ch - 'A' - k + 26) % 26
                ('A' + offset)
            }
            in 'a'..'z' -> {
                val offset = (ch - 'a' - k + 26) % 26
                ('a' + offset)
            }
            else -> ch
        }
    }.joinToString("")
}

/**
 * Converts integer 0..25 to Roman numeral representation.
 */
private fun toRomanNumeral(num: Int): String = when (num) {
    0 -> "N"
    1 -> "I"
    2 -> "II"
    3 -> "III"
    4 -> "IV"
    5 -> "V"
    6 -> "VI"
    7 -> "VII"
    8 -> "VIII"
    9 -> "IX"
    10 -> "X"
    11 -> "XI"
    12 -> "XII"
    13 -> "XIII"
    14 -> "XIV"
    15 -> "XV"
    16 -> "XVI"
    17 -> "XVII"
    18 -> "XVIII"
    19 -> "XIX"
    20 -> "XX"
    21 -> "XXI"
    22 -> "XXII"
    23 -> "XXIII"
    24 -> "XXIV"
    25 -> "XXV"
    else -> num.toString()
}
