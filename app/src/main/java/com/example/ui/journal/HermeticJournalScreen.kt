package com.example.ui.journal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.journal.JournalEntry
import com.example.data.journal.JournalThemes
import com.example.data.journal.displayGlyph
import com.example.data.repository.HermeticRepository
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * How the journal should open: on the list (default), straight into a new entry, or
 * straight into an existing entry ([entryId], e.g. when a folio is tapped in the Grimoire).
 */
data class JournalLaunch(
    val startWithNewEntry: Boolean = false,
    val contemplationMinutes: Int? = null,
    val entryId: Long? = null
)

/** Wax-seal glyphs offered when writing an entry (carried over from the old Grimoire dialog). */
private val sealGlyphOptions = listOf("✦", "☉", "☽", "☿", "🜍", "🜔", "🜛", "🜎", "⚚")

/** What the editor is working on: an existing entry, or a new one. */
private data class EditorTarget(
    val existing: JournalEntry?,
    val contemplationMinutes: Int?
)

private val reflectionPrompts = listOf(
    "What did today's teaching stir in you?",
    "Where did you notice the Principle of Polarity at work today?",
    "What rhythm, high or low, are you moving through right now?",
    "What thought, if transmuted, would change your day?",
    "As above, so below: how did your inner state shape your outer day?",
    "What did you observe in stillness that you miss in motion?",
    "Which cause set today's effects in motion?"
)

/**
 * The Hermetic Journal: a Room-backed log of daily reflections with a browsable list,
 * theme filters and simple growth statistics.
 */
@Composable
fun HermeticJournalScreen(
    repository: HermeticRepository,
    launch: JournalLaunch,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    // null until Room's first emission, so the empty-state message doesn't flash while loading
    val loadedEntries by repository.journalReflections.collectAsState(initial = null)
    val entries = loadedEntries.orEmpty()
    var editorTarget by remember(launch) {
        mutableStateOf(
            if (launch.startWithNewEntry) EditorTarget(null, launch.contemplationMinutes) else null
        )
    }

    // Opening a specific entry: wait for Room's first emission, then open it once.
    var pendingEntryId by remember(launch) { mutableStateOf(launch.entryId) }
    LaunchedEffect(entries, pendingEntryId) {
        val id = pendingEntryId ?: return@LaunchedEffect
        entries.firstOrNull { it.id == id }?.let {
            editorTarget = EditorTarget(it, it.contemplationMinutes)
            pendingEntryId = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireCover)
            .testTag("hermetic_journal_screen")
            // Absorb taps on empty areas so they don't reach the app screens underneath
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        val target = editorTarget
        if (target == null) {
            JournalListView(
                entries = entries,
                isLoading = loadedEntries == null,
                onBack = onClose,
                onNewEntry = { editorTarget = EditorTarget(null, null) },
                onOpenEntry = { editorTarget = EditorTarget(it, it.contemplationMinutes) }
            )
        } else {
            JournalEditorView(
                repository = repository,
                target = target,
                onDone = { editorTarget = null }
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// List view
// ---------------------------------------------------------------------------------------------

@Composable
private fun JournalListView(
    entries: List<JournalEntry>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onNewEntry: () -> Unit,
    onOpenEntry: (JournalEntry) -> Unit
) {
    var themeFilter by remember { mutableStateOf<String?>(null) }
    // Fixed themes plus any categories inscribed from other screens (e.g. "Hermetic Cipher")
    val filterThemes = remember(entries) {
        (JournalThemes.all + entries.map { it.theme }).distinct()
    }
    val visible = if (themeFilter == null) entries else entries.filter { it.theme == themeFilter }

    BackHandler(onBack = onBack)

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        JournalHeader(
            title = "HERMETIC JOURNAL",
            subtitle = "Daily reflections on the Great Work",
            onBack = onBack,
            action = {
                Button(
                    onClick = onNewEntry,
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = NigredoBlack),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("journal_new_entry_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { GrowthStats(entries) }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    item {
                        FilterChipBox(label = "All", isSelected = themeFilter == null) { themeFilter = null }
                    }
                    items(filterThemes) { theme ->
                        FilterChipBox(label = theme, isSelected = themeFilter == theme) { themeFilter = theme }
                    }
                }
            }

            if (isLoading) {
                // Nothing to show yet; avoids flashing the empty-state message
            } else if (visible.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🜄", fontSize = 36.sp, color = AntiqueGold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (entries.isEmpty()) "Thy journal awaits its first reflection."
                            else "No reflections filed under $themeFilter yet.",
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 16.sp,
                            color = ParchmentDark,
                            textAlign = TextAlign.Center
                        )
                        if (entries.isEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap New to record what today's study or meditation revealed.",
                                fontFamily = GaramondFont,
                                fontSize = 14.sp,
                                color = ParchmentDark.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(visible, key = { it.id }) { entry ->
                    JournalEntryCard(entry = entry, onClick = { onOpenEntry(entry) })
                }
            }
        }
    }
}

@Composable
private fun GrowthStats(entries: List<JournalEntry>) {
    val streak = remember(entries) { currentStreakDays(entries) }
    val minutes = remember(entries) { entries.sumOf { it.contemplationMinutes ?: 0 } }
    val daysWritten = remember(entries) { entries.map { dayKey(it.createdAt) }.toSet().size }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ChamberSurface)
            .border(1.dp, AntiqueGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCell(value = entries.size.toString(), label = "Reflections", modifier = Modifier.weight(1f))
        StatCell(value = daysWritten.toString(), label = "Days written", modifier = Modifier.weight(1f))
        StatCell(value = streak.toString(), label = "Day streak", modifier = Modifier.weight(1f))
        StatCell(value = minutes.toString(), label = "Mins in stillness", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = GoldBright)
        Text(
            text = label,
            fontFamily = GaramondFont,
            fontSize = 12.sp,
            color = ParchmentDark,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun JournalEntryCard(entry: JournalEntry, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ParchmentBase)
            .border(1.dp, ChamberBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("journal_entry_${entry.id}")
    ) {
        Text(
            text = formatEntryDate(entry.createdAt),
            fontFamily = GaramondFont,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            color = FadedInk
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = entry.title,
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = IronGallInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            WaxSealBadge(glyph = entry.displayGlyph, size = 30.dp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TagPill(entry.theme)
            entry.contemplationMinutes?.let { TagPill("⧗ $it min") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = entry.content,
            fontFamily = GaramondFont,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            color = IronGallInk,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TagPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(WaxSealRed.copy(alpha = 0.12f))
            .border(0.8.dp, WaxSealRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = text, fontFamily = CinzelFont, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WaxSealRed)
    }
}

@Composable
private fun FilterChipBox(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) AntiqueGold else ChamberSurfaceLight)
            .border(1.dp, if (isSelected) GoldBright else ChamberBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontFamily = CinzelFont,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) NigredoBlack else ParchmentLight
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Editor view
// ---------------------------------------------------------------------------------------------

@Composable
private fun JournalEditorView(
    repository: HermeticRepository,
    target: EditorTarget,
    onDone: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val existing = target.existing
    val minutes = target.contemplationMinutes

    val initialTheme = existing?.theme ?: if (minutes != null) JournalThemes.MEDITATION else JournalThemes.GENERAL
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var content by remember { mutableStateOf(existing?.content ?: "") }
    var theme by remember { mutableStateOf(initialTheme) }
    val initialGlyph = existing?.displayGlyph ?: sealGlyphOptions.first()
    var sealGlyph by remember { mutableStateOf(initialGlyph) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    // Guards against a double tap saving the entry (and awarding XP) twice
    var isSaving by remember { mutableStateOf(false) }

    val prompt = remember {
        reflectionPrompts[Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % reflectionPrompts.size]
    }
    val isDirty = title != (existing?.title ?: "") ||
        content != (existing?.content ?: "") ||
        theme != initialTheme ||
        sealGlyph != initialGlyph

    val requestBack: () -> Unit = {
        if (isDirty) {
            showDiscardDialog = true
        } else {
            onDone()
        }
    }
    BackHandler(onBack = requestBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        JournalHeader(
            title = if (existing == null) "NEW REFLECTION" else "REFLECTION",
            subtitle = existing?.let { formatEntryDate(it.createdAt) } ?: formatEntryDate(System.currentTimeMillis()),
            onBack = requestBack,
            action = {
                if (existing != null) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete reflection", tint = ParchmentDark)
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (minutes != null) {
                Text(
                    text = "⧗ Written after a $minutes-minute contemplation",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = CitrinitasAmber
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth().testTag("journal_title_field"),
                singleLine = true,
                label = { Text("Title (optional)", fontFamily = GaramondFont) },
                colors = journalFieldColors(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "THEME",
                fontFamily = CinzelFont,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CitrinitasAmber,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                // Include the entry's own category if it came from another screen
                items((JournalThemes.all + initialTheme).distinct()) { option ->
                    FilterChipBox(label = option, isSelected = option == theme) { theme = option }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "WAX SEAL",
                fontFamily = CinzelFont,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CitrinitasAmber,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                items((sealGlyphOptions + initialGlyph).distinct()) { g ->
                    val isChosen = g == sealGlyph
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isChosen) WaxSealRed else ChamberSurfaceLight)
                            .border(1.dp, if (isChosen) GoldBright else ChamberBorder, CircleShape)
                            .clickable { sealGlyph = g },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = g, fontSize = 15.sp, color = if (isChosen) GoldBright else ParchmentDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp)
                    .testTag("journal_content_field"),
                placeholder = {
                    Text(
                        text = prompt,
                        fontFamily = GaramondFont,
                        fontStyle = FontStyle.Italic,
                        color = ParchmentDark.copy(alpha = 0.6f)
                    )
                },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaramondFont, fontSize = 17.sp, lineHeight = 25.sp),
                colors = journalFieldColors(),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (isSaving) return@Button
                isSaving = true
                val finalTitle = title.trim().ifBlank {
                    "Reflection • " + SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date())
                }
                scope.launch {
                    if (existing == null) {
                        repository.addReflection(
                            title = finalTitle,
                            content = content.trim(),
                            theme = theme,
                            sealGlyph = sealGlyph,
                            contemplationMinutes = minutes
                        )
                    } else {
                        repository.updateReflection(
                            existing.copy(
                                title = finalTitle,
                                content = content.trim(),
                                theme = theme,
                                sealGlyph = sealGlyph
                            )
                        )
                    }
                    onDone()
                }
            },
            enabled = content.isNotBlank() && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(50.dp)
                .testTag("journal_save_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = AntiqueGold,
                contentColor = NigredoBlack,
                disabledContainerColor = ChamberSurfaceLight,
                disabledContentColor = ParchmentDark
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (existing == null) "INSCRIBE REFLECTION (+60 XP)" else "SAVE CHANGES",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            containerColor = ChamberDark,
            title = { Text("Discard changes?", fontFamily = CinzelFont, color = AntiqueGold) },
            text = { Text("Your unsaved writing will be lost.", fontFamily = GaramondFont, color = ParchmentLight) },
            confirmButton = {
                TextButton(onClick = { showDiscardDialog = false; onDone() }) {
                    Text("Discard", fontFamily = CinzelFont, color = WaxSealRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep writing", fontFamily = CinzelFont, color = AntiqueGold)
                }
            }
        )
    }

    if (showDeleteDialog && existing != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = ChamberDark,
            title = { Text("Delete this reflection?", fontFamily = CinzelFont, color = AntiqueGold) },
            text = { Text("This cannot be undone.", fontFamily = GaramondFont, color = ParchmentLight) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    if (isSaving) return@TextButton
                    isSaving = true
                    scope.launch {
                        repository.deleteReflection(existing)
                        onDone()
                    }
                }) {
                    Text("Delete", fontFamily = CinzelFont, color = WaxSealRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", fontFamily = CinzelFont, color = AntiqueGold)
                }
            }
        )
    }
}

@Composable
private fun journalFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ParchmentLight,
    unfocusedTextColor = ParchmentLight,
    focusedBorderColor = AntiqueGold,
    unfocusedBorderColor = ChamberBorder,
    focusedContainerColor = ChamberDark,
    unfocusedContainerColor = ChamberDark,
    focusedLabelColor = AntiqueGold,
    unfocusedLabelColor = ParchmentDark,
    cursorColor = AntiqueGold
)

// ---------------------------------------------------------------------------------------------
// Shared header
// ---------------------------------------------------------------------------------------------

@Composable
private fun JournalHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChamberDark)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AntiqueGold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                letterSpacing = 1.5.sp,
                color = AntiqueGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontFamily = GaramondFont,
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                color = ParchmentDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(modifier = Modifier.padding(end = 8.dp)) { action() }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

// ---------------------------------------------------------------------------------------------
// Date helpers (java.util based: minSdk 24 has no java.time without desugaring)
// ---------------------------------------------------------------------------------------------

private fun formatEntryDate(millis: Long): String =
    SimpleDateFormat("EEEE d MMMM yyyy • HH:mm", Locale.getDefault()).format(Date(millis))

private fun dayKey(millis: Long): Int {
    val c = Calendar.getInstance().apply { timeInMillis = millis }
    return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH)
}

/**
 * Consecutive days, ending today, with at least one reflection. If nothing has been written
 * today yet, the streak still counts from yesterday so it isn't shown as broken mid-day.
 */
private fun currentStreakDays(entries: List<JournalEntry>): Int {
    if (entries.isEmpty()) return 0
    val days = entries.map { dayKey(it.createdAt) }.toSet()
    val cursor = Calendar.getInstance()
    if (!days.contains(dayKey(cursor.timeInMillis))) {
        cursor.add(Calendar.DAY_OF_MONTH, -1)
    }
    var streak = 0
    while (days.contains(dayKey(cursor.timeInMillis))) {
        streak++
        cursor.add(Calendar.DAY_OF_MONTH, -1)
    }
    return streak
}
