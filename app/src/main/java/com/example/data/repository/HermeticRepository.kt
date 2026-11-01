package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.GeminiOracleService
import com.example.data.journal.HermeticJournalDatabase
import com.example.data.journal.JournalEntry
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HermeticRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hermetica_vault", Context.MODE_PRIVATE)

    private val oracleService = GeminiOracleService()

    private val _principles = MutableStateFlow(HermeticData.principles)
    val principles: StateFlow<List<HermeticPrinciple>> = _principles.asStateFlow()

    private val _artifacts = MutableStateFlow<List<CollectibleArtifact>>(emptyList())
    val artifacts: StateFlow<List<CollectibleArtifact>> = _artifacts.asStateFlow()

    private val _dailyInsight = MutableStateFlow<DailyInsight>(HermeticData.initialDailyInsights.first())
    val dailyInsight: StateFlow<DailyInsight> = _dailyInsight.asStateFlow()

    // Source text currently selected on the Oracle screen (The Kybalion, Emerald Tablet, ...).
    // Kept here rather than in the screen so it survives switching to other tabs and back,
    // keeping the highlighted button in step with the scroll text being shown.
    private val _selectedInsightTopic = MutableStateFlow("The Kybalion")
    val selectedInsightTopic: StateFlow<String> = _selectedInsightTopic.asStateFlow()

    fun setSelectedInsightTopic(topic: String) {
        _selectedInsightTopic.value = topic
    }

    private val _insightHistory = MutableStateFlow<List<DailyInsight>>(HermeticData.initialDailyInsights)
    val insightHistory: StateFlow<List<DailyInsight>> = _insightHistory.asStateFlow()


    private val _personalSigils = MutableStateFlow<List<PersonalSigil>>(emptyList())
    val personalSigils: StateFlow<List<PersonalSigil>> = _personalSigils.asStateFlow()

    private val _adeptProfile = MutableStateFlow(
        AdeptProfile(
            rankTitle = "Neophyte of the Threshold",
            degreeLevel = 1,
            experiencePoints = 120,
            daysStreak = 3,
            transmutationsPerformed = 1,
            principlesMastered = setOf(1),
            artifactsFound = setOf("emerald_tablet"),
            currentLunarPhase = calculateLunarPhase()
        )
    )
    val adeptProfile: StateFlow<AdeptProfile> = _adeptProfile.asStateFlow()

    private val _isGeneratingInsight = MutableStateFlow(false)
    val isGeneratingInsight: StateFlow<Boolean> = _isGeneratingInsight.asStateFlow()

    private val _oracleResponse = MutableStateFlow<String?>(null)
    val oracleResponse: StateFlow<String?> = _oracleResponse.asStateFlow()

    private val _isConsultingOracle = MutableStateFlow(false)
    val isConsultingOracle: StateFlow<Boolean> = _isConsultingOracle.asStateFlow()

    private val _ciphers = MutableStateFlow(HermeticData.cipherPuzzles)
    val ciphers: StateFlow<List<HermeticCipherPuzzle>> = _ciphers.asStateFlow()

    private val _knowledgeNodes = MutableStateFlow(HermeticData.knowledgeNodes)
    val knowledgeNodes: StateFlow<List<KnowledgeNode>> = _knowledgeNodes.asStateFlow()

    private val _activeCipher = MutableStateFlow(HermeticData.cipherPuzzles.first())
    val activeCipher: StateFlow<HermeticCipherPuzzle> = _activeCipher.asStateFlow()

    // ---- Hermetic Journal (Room) ----
    // The app's single journal. Hand-written reflections and entries inscribed from other
    // screens (Oracle decrees, deciphered axioms, sigils, planetary auspices) are all stored here
    // and persist across restarts. (This replaced the Grimoire's old in-memory journal.)
    private val journalDao = HermeticJournalDatabase.get(context).journalDao()
    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** All journal entries, newest first. Emits again whenever an entry changes. */
    val journalReflections: Flow<List<JournalEntry>> = journalDao.observeAll()

    /**
     * Saves a new journal entry and awards the inscription rewards (+60 XP and the Seal of
     * Solomon relic on first use), exactly as the old Grimoire inscription did.
     */
    suspend fun addReflection(
        title: String,
        content: String,
        theme: String,
        sealGlyph: String? = null,
        contemplationMinutes: Int? = null
    ) {
        val now = System.currentTimeMillis()
        journalDao.insert(
            JournalEntry(
                createdAt = now,
                updatedAt = now,
                title = title,
                content = content,
                theme = theme,
                contemplationMinutes = contemplationMinutes,
                sealGlyph = sealGlyph
            )
        )
        addExperience(60)
        unlockArtifact("seal_of_solomon")
    }

    suspend fun updateReflection(entry: JournalEntry) {
        journalDao.update(entry.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteReflection(entry: JournalEntry) {
        journalDao.delete(entry)
    }

    init {
        loadPersistedState()
    }

    private fun loadPersistedState() {
        // Load unlocked artifacts
        val unlockedIds = prefs.getStringSet("unlocked_artifacts", setOf("emerald_tablet")) ?: setOf("emerald_tablet")
        val loadedArtifacts = HermeticData.artifacts.map { artifact ->
            artifact.copy(isUnlocked = unlockedIds.contains(artifact.id))
        }
        _artifacts.value = loadedArtifacts

        // Load streak & transmutations
        val streak = prefs.getInt("days_streak", 3)
        val transmutations = prefs.getInt("transmutations_count", 1)
        val exp = prefs.getInt("experience_pts", 120)
        val mastered = prefs.getStringSet("mastered_principles", setOf("1"))?.mapNotNull { it.toIntOrNull() }?.toSet() ?: setOf(1)

        // Load solved ciphers & unlocked knowledge nodes
        val solvedCipherIds = prefs.getStringSet("solved_ciphers", emptySet()) ?: emptySet()
        val unlockedNodeIds = prefs.getStringSet("unlocked_knowledge_nodes", setOf("node_mentalism")) ?: setOf("node_mentalism")

        _knowledgeNodes.value = HermeticData.knowledgeNodes.map { node ->
            node.copy(isUnlocked = unlockedNodeIds.contains(node.id))
        }

        // Determine active cipher: day-of-week index
        val dayOfWeekIndex = (java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
        val defaultDayCipher = HermeticData.cipherPuzzles.getOrNull(dayOfWeekIndex) ?: HermeticData.cipherPuzzles.first()
        _activeCipher.value = defaultDayCipher

        val degree = 1 + (exp / 200)
        val rank = determineRank(degree)

        _adeptProfile.value = AdeptProfile(
            rankTitle = rank,
            degreeLevel = degree,
            experiencePoints = exp,
            daysStreak = streak,
            transmutationsPerformed = transmutations,
            principlesMastered = mastered,
            artifactsFound = unlockedIds,
            currentLunarPhase = calculateLunarPhase(),
            solvedCiphers = solvedCipherIds,
            unlockedKnowledgeNodes = unlockedNodeIds
        )


        // Load personal sigils
        val savedSigilsJson = prefs.getString("saved_personal_sigils", null)
        if (!savedSigilsJson.isNullOrEmpty()) {
            try {
                val arr = org.json.JSONArray(savedSigilsJson)
                val list = mutableListOf<PersonalSigil>()
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    list.add(PersonalSigil.fromJson(obj))
                }
                _personalSigils.value = if (list.isNotEmpty()) list else createDefaultSampleSigils()
            } catch (e: Exception) {
                _personalSigils.value = createDefaultSampleSigils()
            }
        } else {
            _personalSigils.value = createDefaultSampleSigils()
        }
    }

    fun setActiveCipher(cipher: HermeticCipherPuzzle) {
        _activeCipher.value = cipher
    }

    fun solveCipher(puzzleId: String): Boolean {
        val puzzle = HermeticData.cipherPuzzles.find { it.id == puzzleId } ?: return false
        val currentSolved = _adeptProfile.value.solvedCiphers.toMutableSet()
        val isNewSolve = currentSolved.add(puzzleId)

        if (isNewSolve) {
            prefs.edit().putStringSet("solved_ciphers", currentSolved).apply()

            unlockKnowledgeNode(puzzle.associatedNodeId)
            addExperience(puzzle.rewardXp)

            val node = HermeticData.knowledgeNodes.find { it.id == puzzle.associatedNodeId }
            val gnosisSnippet = node?.secretGnosis?.take(180) ?: ""
            addJournalEntry(
                title = "Deciphered: ${puzzle.title}",
                category = "Hermetic Cipher",
                content = "Axiom Decoded:\n\"${puzzle.solutionText}\"\n\nSource: ${puzzle.kybalionChapter}\n\nGnosis Unveiled: $gnosisSnippet...",
                sealGlyph = puzzle.glyphSymbol
            )

            _adeptProfile.value = _adeptProfile.value.copy(
                solvedCiphers = currentSolved
            )
        }
        return isNewSolve
    }

    fun unlockKnowledgeNode(nodeId: String) {
        val currentUnlocked = _adeptProfile.value.unlockedKnowledgeNodes.toMutableSet()
        if (currentUnlocked.add(nodeId)) {
            prefs.edit().putStringSet("unlocked_knowledge_nodes", currentUnlocked).apply()
            _knowledgeNodes.value = _knowledgeNodes.value.map { node ->
                if (node.id == nodeId) {
                    node.copy(isUnlocked = true, unlockedTimestamp = "Anno 1684 • Solved")
                } else node
            }
            _adeptProfile.value = _adeptProfile.value.copy(
                unlockedKnowledgeNodes = currentUnlocked
            )
        }
    }

    private fun determineRank(degree: Int): String = when {
        degree >= 7 -> "Magus of the Hermetic Rosy Cross"
        degree >= 6 -> "Magister Templi"
        degree >= 5 -> "Adeptus Major"
        degree >= 4 -> "Adeptus Minor"
        degree >= 3 -> "Philosophus of the Crucible"
        degree >= 2 -> "Practicus of the Athanor"
        else -> "Neophyte of the Threshold"
    }


    /** Whether the introductory first page should be shown when the app opens. */
    fun shouldShowWelcomeScreen(): Boolean = prefs.getBoolean("show_welcome_screen", true)

    fun setShowWelcomeScreen(show: Boolean) {
        prefs.edit().putBoolean("show_welcome_screen", show).apply()
    }

    // Incremented per request so only the most recent topic selection updates the scroll.
    private var latestInsightRequest = 0
    private var insightJob: kotlinx.coroutines.Job? = null

    /**
     * Fetches a fresh insight for [topic] on the repository's own scope, cancelling any older
     * request. Running here (not on the Oracle screen's scope) means the request still completes
     * if the user switches tab while it is loading, so the highlighted source and the scroll
     * text can't drift apart.
     */
    fun requestDailyInsight(topic: String?) {
        insightJob?.cancel()
        insightJob = repoScope.launch { fetchFreshDailyInsight(topic) }
    }

    suspend fun fetchFreshDailyInsight(topic: String? = null) {
        val requestId = ++latestInsightRequest
        _isGeneratingInsight.value = true
        try {
            val insight = oracleService.generateDailyInsight(topic)
            if (requestId != latestInsightRequest) return // a newer topic was selected
            _dailyInsight.value = insight
            _insightHistory.value = listOf(insight) + _insightHistory.value.filter { it.id != insight.id }
            addExperience(40)
        } finally {
            if (requestId == latestInsightRequest) {
                _isGeneratingInsight.value = false
            }
        }
    }

    suspend fun askOracle(query: String, domain: String) {
        _isConsultingOracle.value = true
        _oracleResponse.value = null
        try {
            val answer = oracleService.consultOracle(query, domain)
            _oracleResponse.value = answer
            addExperience(25)
        } finally {
            _isConsultingOracle.value = false
        }
    }

    fun markPrincipleStudied(principleId: Int) {
        val currentMastered = _adeptProfile.value.principlesMastered.toMutableSet()
        if (currentMastered.add(principleId)) {
            addExperience(50)
            prefs.edit().putStringSet("mastered_principles", currentMastered.map { it.toString() }.toSet()).apply()
            _adeptProfile.value = _adeptProfile.value.copy(principlesMastered = currentMastered)

            if (currentMastered.size == 7) {
                unlockArtifact("eye_of_hermes")
            }
        }
    }

    fun unlockArtifact(artifactId: String) {
        val currentFound = _adeptProfile.value.artifactsFound.toMutableSet()
        if (currentFound.add(artifactId)) {
            prefs.edit().putStringSet("unlocked_artifacts", currentFound).apply()
            _artifacts.value = _artifacts.value.map {
                if (it.id == artifactId) it.copy(isUnlocked = true) else it
            }
            addExperience(150)
            _adeptProfile.value = _adeptProfile.value.copy(artifactsFound = currentFound)
        }
    }

    fun performTransmutation(selectedElementIds: List<String>): TransmutationResult {
        val currentCount = _adeptProfile.value.transmutationsPerformed + 1
        prefs.edit().putInt("transmutations_count", currentCount).apply()

        // Match against known formulas (order independent)
        val matchedRecipe = HermeticData.transmutationRecipes.find { recipe ->
            recipe.inputs.size == selectedElementIds.size &&
                    recipe.inputs.all { selectedElementIds.contains(it) }
        }

        addExperience(30)
        _adeptProfile.value = _adeptProfile.value.copy(transmutationsPerformed = currentCount)

        return if (matchedRecipe != null) {
            matchedRecipe.unlockedArtifactId?.let { unlockArtifact(it) }
            TransmutationResult.Success(matchedRecipe)
        } else {
            TransmutationResult.Partial(
                substanceName = "Unrefined Magisterium Residue",
                hint = "The celestial alignment wavers. Combine Sulfur, Mercury, and Salt for the supreme Magnum Opus, or Prima Materia with Fire and Water."
            )
        }
    }

    /**
     * Inscribes an entry from another screen (Oracle, ciphers, sigils, planetary hours) into
     * the Hermetic Journal. Callers are plain click handlers, so the save runs on [repoScope].
     */
    fun addJournalEntry(title: String, category: String, content: String, sealGlyph: String) {
        repoScope.launch {
            addReflection(title = title, content = content, theme = category, sealGlyph = sealGlyph)
        }
    }

    fun addExperience(pts: Int) {
        val newExp = _adeptProfile.value.experiencePoints + pts
        val newDegree = 1 + (newExp / 200)
        val newRank = determineRank(newDegree)

        prefs.edit().putInt("experience_pts", newExp).apply()
        _adeptProfile.value = _adeptProfile.value.copy(
            experiencePoints = newExp,
            degreeLevel = newDegree,
            rankTitle = newRank
        )
    }

    private fun calculateLunarPhase(): String {
        // Aesthetic 17th-century lunar calculation
        val dayOfMonth = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
        return when (dayOfMonth % 8) {
            0 -> "New Moon (Luna Nova)"
            1 -> "Waxing Crescent (Luna Crescens)"
            2 -> "First Quarter (Dimidia Prima)"
            3 -> "Waxing Gibbous (Luna Gibba)"
            4 -> "Full Cold Moon (Luna Plena)"
            5 -> "Waning Gibbous (Luna Disseminata)"
            6 -> "Last Quarter (Dimidia Ultima)"
            else -> "Balsamic Moon (Luna Vetus)"
        }
    }

    fun savePersonalSigil(sigil: PersonalSigil) {
        val updated = listOf(sigil) + _personalSigils.value.filter { it.id != sigil.id }
        _personalSigils.value = updated
        persistSigils(updated)

        addJournalEntry(
            title = "Consecration: ${sigil.name}",
            category = "Personal Sigil",
            content = "Seal forged with motto: '${sigil.motto}'. Constructed with ${sigil.geometry.title} and ${sigil.glyphs.size} sacred motifs (${sigil.glyphs.joinToString(" ") { it.glyph }}). Tincture: ${sigil.tincture.title}.",
            sealGlyph = sigil.glyphs.firstOrNull()?.glyph ?: "🜚"
        )
        addExperience(100)
        unlockArtifact("seal_of_solomon")
    }

    fun deletePersonalSigil(id: String) {
        val updated = _personalSigils.value.filter { it.id != id }
        _personalSigils.value = updated
        persistSigils(updated)
    }

    private fun persistSigils(list: List<PersonalSigil>) {
        val arr = org.json.JSONArray()
        list.forEach { arr.put(it.toJson()) }
        prefs.edit().putString("saved_personal_sigils", arr.toString()).apply()
    }

    private fun createDefaultSampleSigils(): List<PersonalSigil> {
        return listOf(
            PersonalSigil(
                id = "preset_hermes",
                name = "Seal of Hermes Trismegistus",
                motto = "Quod est superius est sicut quod est inferius",
                geometry = SealBaseGeometry.SOLOMON_HEXAGRAM,
                tincture = SealTincture.ALCHEMICAL_GOLD,
                glyphs = listOf(
                    PlacedGlyph("g1", "☉", "Sol", 0f, -0.42f, 1.2f, 0f, androidx.compose.ui.graphics.Color(0xFFFFD700)),
                    PlacedGlyph("g2", "☽", "Luna", -0.40f, 0.28f, 1.1f, 0f, androidx.compose.ui.graphics.Color(0xFFE2E8F0)),
                    PlacedGlyph("g3", "☿", "Mercurius", 0.40f, 0.28f, 1.1f, 0f, androidx.compose.ui.graphics.Color(0xFFD4AF37)),
                    PlacedGlyph("g4", "🜛", "Philosopher's Stone", 0f, 0f, 1.4f, 0f, androidx.compose.ui.graphics.Color(0xFF8B1E1E))
                ),
                createdTimestamp = System.currentTimeMillis() - 86400000L
            )
        )
    }
}

sealed class TransmutationResult {
    data class Success(val formula: TransmutationFormula) : TransmutationResult()
    data class Partial(val substanceName: String, val hint: String) : TransmutationResult()
}
