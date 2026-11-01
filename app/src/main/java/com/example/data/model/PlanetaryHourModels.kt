package com.example.data.model

enum class CelestialPlanet(
    val englishName: String,
    val latinName: String,
    val glyph: String,
    val metal: String,
    val metalGlyph: String,
    val element: String,
    val incense: String,
    val auspiciousWork: String,
    val colorHex: Long,
    val hermeticAxiom: String
) {
    SOL(
        englishName = "The Sun",
        latinName = "Sol",
        glyph = "☉",
        metal = "Gold (Aurum)",
        metalGlyph = "🜚",
        element = "Fire / Quintessence",
        incense = "Frankincense, Bay Laurel, Saffron",
        auspiciousWork = "Solar enlightenment, noble ambitions, illumination of spirit, vital energy, and multiplying the Philosopher's Stone.",
        colorHex = 0xFFFFD700,
        hermeticAxiom = "The Father of it is the Sun, its power is integrating and whole."
    ),
    VENUS(
        englishName = "Venus",
        latinName = "Venus",
        glyph = "♀",
        metal = "Copper (Cuprum)",
        metalGlyph = "🜠",
        element = "Water & Air",
        incense = "Benzoin, Rose, Sandalwood",
        auspiciousWork = "Harmonizing polarities, artistic inspiration, cultivating mystical love, and the alchemical sacred marriage (Coniunctio).",
        colorHex = 0xFF50C878,
        hermeticAxiom = "Gender is in all things; everything has its Masculine and Feminine Principles."
    ),
    MERCURY(
        englishName = "Mercury",
        latinName = "Mercurius",
        glyph = "☿",
        metal = "Quicksilver (Hydrargyrum)",
        metalGlyph = "🜔",
        element = "Air & Subtle Fluid",
        incense = "Mastic, Storax, Lavender, Cloves",
        auspiciousWork = "Deciphering ciphers, translation of arcane manuscripts, intellectual transmutation, and swift communication with the Noûs.",
        colorHex = 0xFF9370DB,
        hermeticAxiom = "Mind (as well as metals and elements) may be transmuted from state to state."
    ),
    LUNA(
        englishName = "The Moon",
        latinName = "Luna",
        glyph = "☽",
        metal = "Silver (Argentum)",
        metalGlyph = "🜛",
        element = "Water / Phlegma",
        incense = "Camphor, White Myrrh, Jasmine",
        auspiciousWork = "Scrying, dream interpretation, lunar purification (Albedo), uncovering hidden subconscious truths, and aqueous dissolution.",
        colorHex = 0xFFE0E6ED,
        hermeticAxiom = "The Mother of it is the Moon; the Wind carried it in its belly."
    ),
    SATURN(
        englishName = "Saturn",
        latinName = "Saturnus",
        glyph = "♄",
        metal = "Lead (Plumbum)",
        metalGlyph = "🜪",
        element = "Earth / Melancholia",
        incense = "Black Myrrh, Opoponax, Patchouli",
        auspiciousWork = "Deep contemplation, confronting the Shadow (Nigredo), grounding, mastering patience, and transmuting base lead into spiritual gold.",
        colorHex = 0xFF4A4E69,
        hermeticAxiom = "Nothing rests; everything moves; everything vibrates, yet in patience the Stone matures."
    ),
    JUPITER(
        englishName = "Jupiter",
        latinName = "Iuppiter",
        glyph = "♃",
        metal = "Tin (Stannum)",
        metalGlyph = "🜩",
        element = "Air & Sanguine Spirit",
        incense = "Cedar, Nutmeg, Saffron, Ash",
        auspiciousWork = "Expanding wisdom, benevolent philosophy, legal rectitude, abundance, and higher cosmic alignment.",
        colorHex = 0xFF4682B4,
        hermeticAxiom = "Everything flows, out and in; the pendulum-swing manifests in all things."
    ),
    MARS(
        englishName = "Mars",
        latinName = "Mars",
        glyph = "♂",
        metal = "Iron (Ferrum)",
        metalGlyph = "🜧",
        element = "Fire / Cholera",
        incense = "Dragon's Blood, Pepper, Tobacco",
        auspiciousWork = "Calcination by intense flame, spiritual courage, severing karmic ties, and breaking through stubborn mental obstacles.",
        colorHex = 0xFFB22222,
        hermeticAxiom = "Under and back of the Universe of Time, Space and Change is ever to be found The Substantial Reality."
    )
}

data class PlanetaryHourInfo(
    val hourNumber: Int,          // 1..24
    val isDiurnal: Boolean,        // Day or Night
    val planet: CelestialPlanet,
    val startTimeFormatted: String,
    val endTimeFormatted: String,
    val dayRulerPlanet: CelestialPlanet,
    val dayNameLatin: String,
    val minutesRemaining: Int
)
