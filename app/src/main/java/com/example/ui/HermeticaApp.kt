package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AlchemicalAudioEngine
import com.example.ui.components.AudioSettingsDialog
import androidx.compose.material.icons.automirrored.filled.MenuBook
import com.example.data.repository.HermeticRepository
import com.example.ui.cipher.HermeticCipherModule
import com.example.ui.magic.HorologiumHermeticumDialog
import com.example.ui.navigation.InkSpreadNavigation
import com.example.ui.screens.*
import com.example.ui.splash.AlchemicalSplashScreen
import com.example.ui.welcome.WelcomeScreen
import com.example.ui.contemplation.ContemplationConfig
import com.example.ui.contemplation.ContemplationSessionScreen
import com.example.ui.journal.HermeticJournalScreen
import com.example.ui.journal.JournalLaunch
import com.example.ui.theme.*

enum class HermeticaTab(val label: String, val icon: ImageVector, val glyph: String) {
    DAILY_ORACLE("Oracle", Icons.Default.AutoAwesome, "☉"),
    CIPHER("Cipher", Icons.Default.Key, "🜎"),
    PRINCIPLES("Laws", Icons.Default.AllInclusive, "🜚"),
    LABORATORY("Crucible", Icons.Default.Science, "🜔"),
    COMPENDIUM("Archive", Icons.AutoMirrored.Filled.MenuBook, "🜫"),
    GRIMOIRE("Grimoire", Icons.Default.Stars, "☿")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HermeticaApp(
    repository: HermeticRepository,
    modifier: Modifier = Modifier
) {
    val audioEngine = remember { AlchemicalAudioEngine.getInstance() }
    val isAmbientPlaying by audioEngine.isAmbientPlaying.collectAsState()
    val isMuted by audioEngine.isMuted.collectAsState()
    var showAudioDialog by remember { mutableStateOf(false) }
    var showSplashScreen by remember { mutableStateOf(true) }
    var showHorologiumDialog by remember { mutableStateOf(false) }
    // Introductory first page, shown once the opening splash animation is dismissed
    // (unless the user ticked "Don't show this page again")
    var showWelcomeScreen by remember { mutableStateOf(repository.shouldShowWelcomeScreen()) }

    // Full-screen Contemplation Timer session and Hermetic Journal (null = not showing)
    var contemplationSession by remember { mutableStateOf<ContemplationConfig?>(null) }
    var journalLaunch by remember { mutableStateOf<JournalLaunch?>(null) }
    val dailyInsight by repository.dailyInsight.collectAsState()

    var currentTab by remember { mutableStateOf(HermeticaTab.DAILY_ORACLE) }
    val adeptProfile by repository.adeptProfile.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = GrimoireCover,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "HERMETICA",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    letterSpacing = 2.sp,
                                    color = AntiqueGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "•  ANNO 1684",
                                    fontFamily = CinzelFont,
                                    fontSize = 11.sp,
                                    color = CitrinitasAmber
                                )
                            }
                            Text(
                                text = "${adeptProfile.rankTitle} (Deg. ${adeptProfile.degreeLevel})",
                                fontFamily = GaramondFont,
                                fontSize = 12.sp,
                                color = ParchmentDark
                            )
                        }
                    },
                    actions = {
                        // Planetary Horologium Astrolabe Button
                        IconButton(
                            onClick = {
                                audioEngine.playCipherClick()
                                showHorologiumDialog = true
                            },
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .clip(CircleShape)
                        ) {
                            Text(
                                text = "🪐",
                                fontSize = 16.sp
                            )
                        }

                        // Ambient Soundscape Controller Button
                        IconButton(
                            onClick = { showAudioDialog = true },
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .clip(CircleShape)
                                .background(if (isAmbientPlaying) AntiqueGold.copy(alpha = 0.25f) else Color.Transparent)
                        ) {
                            Text(
                                text = if (isAmbientPlaying) "🔔" else if (isMuted) "🔇" else "🔕",
                                fontSize = 16.sp
                            )
                        }

                        // Sanctum Initiation (Splash Replay) Button
                        IconButton(
                            onClick = {
                                audioEngine.playBellChime()
                                showSplashScreen = true
                            },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clip(CircleShape)
                        ) {
                            Text(
                                text = "🜚",
                                fontSize = 16.sp,
                                color = AntiqueGold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ChamberSurface)
                                .border(1.dp, AntiqueGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${adeptProfile.experiencePoints} XP",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AntiqueGold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ChamberDark,
                        titleContentColor = ParchmentLight
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hermetica_bottom_nav"),
                    containerColor = ChamberDark,
                    contentColor = ParchmentLight,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    HermeticaTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            alwaysShowLabel = true,
                            onClick = {
                                currentTab = tab
                            },
                            icon = {
                                Text(
                                    text = tab.glyph,
                                    fontSize = 17.sp,
                                    color = if (isSelected) GoldBright else ParchmentDark
                                )
                            },
                            label = {
                                val labelFontSize = when {
                                    tab.label.length >= 8 -> 8.sp
                                    tab.label.length >= 7 -> 8.5.sp
                                    else -> 9.sp
                                }
                                Text(
                                    text = tab.label,
                                    fontFamily = CinzelFont,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = labelFontSize,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = (-0.3).sp,
                                    color = if (isSelected) AntiqueGold else ParchmentDark
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GoldBright,
                                selectedTextColor = AntiqueGold,
                                indicatorColor = WaxSealRed.copy(alpha = 0.6f),
                                unselectedIconColor = ParchmentDark,
                                unselectedTextColor = ParchmentDark
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                InkSpreadNavigation(currentTab = currentTab) { tab ->
                    when (tab) {
                        HermeticaTab.DAILY_ORACLE -> DailyInsightScreen(
                            repository = repository,
                            onNavigateToCipher = { currentTab = HermeticaTab.CIPHER },
                            onBeginContemplation = { contemplationSession = it },
                            onOpenJournal = { startNew -> journalLaunch = JournalLaunch(startWithNewEntry = startNew) }
                        )
                        HermeticaTab.CIPHER -> HermeticCipherModule(repository = repository)
                        HermeticaTab.PRINCIPLES -> PrinciplesScreen(repository = repository)
                        HermeticaTab.LABORATORY -> AlchemicalLabScreen(repository = repository)
                        HermeticaTab.COMPENDIUM -> CompendiumScreen()
                        HermeticaTab.GRIMOIRE -> GrimoireReliquaryScreen(
                            repository = repository,
                            onOpenJournal = { journalLaunch = it }
                        )
                    }
                }
            }

            if (showAudioDialog) {
                AudioSettingsDialog(onDismiss = { showAudioDialog = false })
            }

            if (showHorologiumDialog) {
                HorologiumHermeticumDialog(
                    repository = repository,
                    onDismiss = { showHorologiumDialog = false }
                )
            }

            if (showSplashScreen) {
                AlchemicalSplashScreen(
                    onDismiss = { showSplashScreen = false }
                )
            }
        }

        // Hermetic Journal, drawn above the whole app
        journalLaunch?.let { launch ->
            HermeticJournalScreen(
                repository = repository,
                launch = launch,
                onClose = { journalLaunch = null }
            )
        }

        // Contemplation session, drawn above everything so the timer runs uninterrupted
        contemplationSession?.let { config ->
            ContemplationSessionScreen(
                config = config,
                focusPrompt = dailyInsight.dailyHermeticContemplation,
                onClose = { contemplationSession = null },
                onRecordReflection = { minutes ->
                    contemplationSession = null
                    journalLaunch = JournalLaunch(startWithNewEntry = true, contemplationMinutes = minutes)
                }
            )
        }

        // Drawn above the Scaffold (including the top and bottom bars) so it is a true first page.
        if (showWelcomeScreen && !showSplashScreen) {
            WelcomeScreen(
                onEnter = { dontShowAgain ->
                    if (dontShowAgain) {
                        repository.setShowWelcomeScreen(false)
                    }
                    showWelcomeScreen = false
                }
            )
        }
    }
}
