package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyInsight
import com.example.data.repository.HermeticRepository
import com.example.ui.magic.HorologiumHermeticumDialog
import com.example.ui.daily.DailyRevelationModule
import com.example.ui.components.ParchmentScroll
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DailyInsightScreen(
    repository: HermeticRepository,
    onNavigateToCipher: () -> Unit = {},
    onBeginContemplation: (com.example.ui.contemplation.ContemplationConfig) -> Unit = {},
    onOpenJournal: (startWithNewEntry: Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val dailyInsight by repository.dailyInsight.collectAsState()
    val isGenerating by repository.isGeneratingInsight.collectAsState()
    val isConsulting by repository.isConsultingOracle.collectAsState()
    val oracleAnswer by repository.oracleResponse.collectAsState()
    val journalReflections by repository.journalReflections.collectAsState(initial = emptyList())

    var userQuestion by remember { mutableStateOf("") }
    var selectedDomain by remember { mutableStateOf("Hermetic Philosophy") }
    var isSealBroken by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf<String?>(null) }
    var showHorologium by remember { mutableStateOf(false) }

    val domains = listOf(
        "Hermetic Philosophy",
        "The Kybalion & 7 Principles",
        "Alchemical Psychology (Jung)",
        "Spagyrics & Natural Magic",
        "The Emerald Tablet"
    )

    val quickQuestions = listOf(
        "How to apply the Law of Polarity to transmute fear?",
        "What is the secret meaning of 'As above, so below'?",
        "How do I use the Law of Neutralization against mood swings?",
        "Explain Carl Jung's Nigredo stage in modern life.",
        "What is the true nature of the Philosopher's Stone?"
    )

    Box(modifier = modifier.fillMaxSize().background(GrimoireCover)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header: 17th Century Date and Title
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ORACULUM HERMETICUM",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 2.sp,
                        color = AntiqueGold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = dailyInsight.date,
                        fontFamily = GaramondFont,
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp,
                        color = ParchmentDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✦ ONE AMAZING THING TO LEARN TODAY ✦",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = CitrinitasAmber
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(ChamberSurface)
                            .border(1.dp, AntiqueGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable {
                                com.example.audio.AlchemicalAudioEngine.getInstance().playCipherClick()
                                showHorologium = true
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪐", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Horologium Astrolabe (Planetary Hours)",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = AntiqueGold
                            )
                        }
                    }
                }
            }

            // Animated Weather-Beaten Scroll Module with Gemini API
            item {
                DailyRevelationModule(
                    repository = repository,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Contemplation Timer: choose a duration and sound, then begin a session
            item {
                com.example.ui.contemplation.ContemplationTimerCard(
                    onBegin = onBeginContemplation,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Hermetic Journal: record and browse daily reflections
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("hermetic_journal_card"),
                    colors = CardDefaults.cardColors(containerColor = ChamberSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WaxSealBadge(glyph = "🜄", size = 36.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "HERMETIC JOURNAL",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    letterSpacing = 1.sp,
                                    color = AntiqueGold
                                )
                                Text(
                                    text = when (journalReflections.size) {
                                        0 -> "Log daily reflections and track thy spiritual growth"
                                        1 -> "1 entry in thy journal"
                                        else -> "${journalReflections.size} entries in thy journal"
                                    },
                                    fontFamily = GaramondFont,
                                    fontSize = 13.sp,
                                    color = ParchmentDark
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onOpenJournal(true) },
                                modifier = Modifier.weight(1f).testTag("journal_card_new_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = NigredoBlack),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("New Reflection", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                            }
                            OutlinedButton(
                                onClick = { onOpenJournal(false) },
                                modifier = Modifier.weight(1f).testTag("journal_card_browse_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AntiqueGold),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AntiqueGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Browse Entries", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Daily Hermetic Cipher Challenge Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cipher_challenge_banner_card")
                        .clickable { onNavigateToCipher() },
                    colors = CardDefaults.cardColors(containerColor = ChamberDark),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(WaxSealRed, AntiqueGold, ChamberBorder))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        WaxSealBadge(glyph = "🜎", size = 44.dp, onClick = onNavigateToCipher)

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DAILY HERMETIC CIPHER",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 1.2.sp,
                                color = AntiqueGold
                            )
                            Text(
                                text = "Decipher the encrypted Kybalion scroll to unveil a Gnosis Knowledge Node.",
                                fontFamily = GaramondFont,
                                fontSize = 12.sp,
                                color = ParchmentBase
                            )
                        }

                        Button(
                            onClick = onNavigateToCipher,
                            colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                        ) {
                            Text(
                                text = "Solve 🗝",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = NigredoBlack
                            )
                        }
                    }
                }
            }

            // Section: Consult the Hermetic Oracle (AI Q&A)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("oracle_consult_card"),
                    colors = CardDefaults.cardColors(containerColor = ChamberSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder))),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WaxSealBadge(glyph = "☿", size = 36.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "CONSULT THE GRAND ORACLE",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    letterSpacing = 1.sp,
                                    color = AntiqueGold
                                )
                                Text(
                                    text = "Ask of the 17th-century masters of Alchemy & Mind",
                                    fontFamily = GaramondFont,
                                    fontSize = 13.sp,
                                    color = ParchmentDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Domain Selection Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(domains) { domain ->
                                val isSelected = domain == selectedDomain
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) AntiqueGold else ChamberSurfaceLight)
                                        .border(1.dp, if (isSelected) GoldBright else ChamberBorder, RoundedCornerShape(16.dp))
                                        .clickable { selectedDomain = domain }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = domain,
                                        fontFamily = CinzelFont,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) NigredoBlack else ParchmentLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Question Suggestions
                        Text(
                            text = "COMMON INQUIRIES OF ADEPTS:",
                            fontFamily = CinzelFont,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CitrinitasAmber,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickQuestions) { q ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ChamberSurfaceLight.copy(alpha = 0.7f))
                                        .border(0.8.dp, FadedInk, RoundedCornerShape(8.dp))
                                        .clickable { userQuestion = q }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = q,
                                        fontFamily = GaramondFont,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 13.sp,
                                        color = ParchmentLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Question Input Field
                        OutlinedTextField(
                            value = userQuestion,
                            onValueChange = { userQuestion = it },
                            modifier = Modifier.fillMaxWidth().testTag("oracle_input_field"),
                            placeholder = {
                                Text(
                                    "Speak thy question to Hermes Trismegistus...",
                                    fontFamily = GaramondFont,
                                    fontStyle = FontStyle.Italic,
                                    color = ParchmentDark.copy(alpha = 0.6f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = ParchmentLight,
                                unfocusedTextColor = ParchmentLight,
                                focusedBorderColor = AntiqueGold,
                                unfocusedBorderColor = ChamberBorder,
                                focusedContainerColor = ChamberDark,
                                unfocusedContainerColor = ChamberDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        if (userQuestion.isNotBlank()) {
                                            coroutineScope.launch {
                                                repository.askOracle(userQuestion, selectedDomain)
                                            }
                                        }
                                    },
                                    enabled = !isConsulting && userQuestion.isNotBlank()
                                ) {
                                    if (isConsulting) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AntiqueGold, strokeWidth = 2.dp)
                                    } else {
                                        Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.Send, contentDescription = "Send to Oracle", tint = AntiqueGold)
                                    }
                                }
                            }
                        )

                        // Oracle Response Display
                        AnimatedVisibility(
                            visible = oracleAnswer != null,
                            enter = fadeIn() + expandVertically()
                        ) {
                            oracleAnswer?.let { answer ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ParchmentLight)
                                        .border(1.5.dp, AntiqueGold, RoundedCornerShape(8.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "DECREE OF THE ORACLE",
                                            fontFamily = CinzelFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp,
                                            color = WaxSealRed
                                        )
                                        Text(
                                            text = "SOLVE ET COAGULA",
                                            fontFamily = CinzelFont,
                                            fontSize = 10.sp,
                                            color = ChamberBorder
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = answer,
                                        fontFamily = GaramondFont,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        color = IronGallInk
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            repository.addJournalEntry(
                                                title = "Oracle Decree: ${userQuestion.take(30)}...",
                                                category = selectedDomain,
                                                content = answer,
                                                sealGlyph = "☿"
                                            )
                                            showSnackbar = "Decree inscribed in thy Grimoire!"
                                        },
                                        modifier = Modifier.align(Alignment.End),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ChamberDark,
                                            contentColor = AntiqueGold
                                        )
                                    ) {
                                        Text("Save Decree", fontFamily = CinzelFont, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Notification banner
        AnimatedVisibility(
            visible = showSnackbar != null,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
        ) {
            showSnackbar?.let { msg ->
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(2800)
                    showSnackbar = null
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AntiqueGold)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = msg,
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        color = NigredoBlack,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (showHorologium) {
            HorologiumHermeticumDialog(
                repository = repository,
                onDismiss = { showHorologium = false }
            )
        }
    }
}
