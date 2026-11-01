package com.example.data.model

data class HermeticPrinciple(
    val id: Int,
    val name: String,
    val latinTitle: String,
    val aphorism: String,
    val axiomSource: String,
    val summary: String,
    val deepPhilosophy: String,
    val psychologicalApplication: String,
    val interactiveExerciseHint: String,
    val glyph: String
)

data class DailyInsight(
    val id: String,
    val date: String,
    val title: String,
    val sourceText: String,
    val ancientQuote: String,
    val esotericExegesis: String,
    val psychologicalCorrelation: String,
    val dailyHermeticContemplation: String,
    val glyphSymbol: String = "☉"
)

enum class ElementNature {
    PRIMA_MATERIA,
    SALT,
    SULFUR,
    MERCURY,
    FIRE,
    WATER,
    AIR,
    EARTH,
    QUINTESSENCE
}

data class AlchemicalElement(
    val id: String,
    val name: String,
    val symbol: String,
    val latinName: String,
    val nature: ElementNature,
    val colorHex: Long,
    val esotericMeaning: String
)

enum class MagnumOpusStage(val stageName: String, val latinName: String, val colorHex: Long, val meaning: String) {
    NIGREDO("Nigredo", "Putrefactio", 0xFF181512, "The Blackening: Confrontation with the Shadow and dissolution of false ego structures."),
    ALBEDO("Albedo", "Ablutio", 0xFFE0E6ED, "The Whitening: Purification by lunar light, bringing clarity, contemplation, and peace."),
    CITRINITAS("Citrinitas", "Auroratio", 0xFFDDAA33, "The Yellowing: The dawn of solar awareness and intellectual-spiritual enlightenment."),
    RUBEDO("Rubedo", "Coniunctio", 0xFFA02422, "The Reddening: The supreme alchemical marriage, birth of the Philosopher's Stone.")
}

data class TransmutationFormula(
    val id: String,
    val name: String,
    val inputs: List<String>, // element ids
    val outputName: String,
    val outputSymbol: String,
    val stage: MagnumOpusStage,
    val crypticRevelation: String,
    val unlockedArtifactId: String? = null
)

data class CollectibleArtifact(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    val ancientOrigin: String,
    val latinMotto: String,
    val isUnlocked: Boolean = false,
    val unlockRequirement: String,
    val glyph: String
)

data class AdeptProfile(
    val rankTitle: String,
    val degreeLevel: Int,
    val experiencePoints: Int,
    val daysStreak: Int,
    val transmutationsPerformed: Int,
    val principlesMastered: Set<Int>,
    val artifactsFound: Set<String>,
    val currentLunarPhase: String,
    val solvedCiphers: Set<String> = emptySet(),
    val unlockedKnowledgeNodes: Set<String> = emptySet()
)

enum class CipherType {
    ROTARY_VOLVELLE,       // Caesar-style rotary dial with Hermetic shift key
    SCRAMBLED_ANAGRAM,     // Scrambled letters/word anagram reconstruction
    ALCHEMICAL_SUBSTITUTION // Planetary glyph / symbol substitution
}

data class HermeticCipherPuzzle(
    val id: String,
    val dayNumber: Int,
    val title: String,
    val kybalionChapter: String,
    val cipherType: CipherType,
    val scrambledText: String,
    val solutionText: String,
    val cipherKey: Int = 3, // For rotary shift (1..25)
    val planetaryRuler: String, // e.g. "Hermes (Mercury) ☿"
    val esotericClue: String,
    val historicalContext: String,
    val associatedNodeId: String,
    val rewardXp: Int = 120,
    val glyphSymbol: String = "🜎"
)

data class KnowledgeNode(
    val id: String,
    val title: String,
    val subtitle: String,
    val principleAffinity: String,
    val kybalionAxiom: String,
    val secretGnosis: String,
    val practicalHermeticArt: String,
    val psychologicalInterpretation: String,
    val isUnlocked: Boolean = false,
    val glyph: String = "🜚",
    val unlockedTimestamp: String? = null
)

