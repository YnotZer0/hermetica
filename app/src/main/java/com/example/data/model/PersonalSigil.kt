package com.example.data.model

import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject

enum class SealBaseGeometry(val title: String, val glyph: String, val description: String) {
    SOLOMON_HEXAGRAM("Seal of Solomon", "✡", "Intertwined Fire △ and Water ▽ within concentric celestial boundary"),
    OUROBOROS_SERPENT("Ouroboros Orbit", "🜔", "The world serpent eating its tail, symbol of eternal cyclical return"),
    SEVEN_PLANETS("Seven Spheres", "☉", "Seven concentric rings representing the Chaldean celestial spheres"),
    HERMETIC_OCTAGRAM("Octagram of Hermes", "🜚", "8-pointed star of Thoth-Hermes symbolizing cosmic equilibrium"),
    ELEMENTAL_QUADRATE("Elemental Cross", "🜂", "Four-fold square and cross of Earth, Water, Air, and Fire"),
    MINIMAL_WAX("Vellum Seal Ring", "🜎", "A pure double-ringed Renaissance signet matrix")
}

enum class SealTincture(val title: String, val primaryColor: Color, val accentColor: Color) {
    ALCHEMICAL_GOLD("Sol Gold", Color(0xFFFFD700), Color(0xFFD4AF37)),
    IMPERIAL_RED_WAX("Vermilion Wax", Color(0xFF8B1E1E), Color(0xFFE64A19)),
    IRON_GALL_INK("Iron Gall Ink", Color(0xFF1B140E), Color(0xFF715438)),
    EMERALD_TABLET("Hermetic Emerald", Color(0xFF1A6B54), Color(0xFF2E8B57)),
    CELESTIAL_SILVER("Luna Silver", Color(0xFFE2E8F0), Color(0xFF94A3B8))
}

data class PlacedGlyph(
    val id: String,
    val glyph: String,
    val name: String,
    var normX: Float, // -1.0 .. 1.0 from seal center
    var normY: Float, // -1.0 .. 1.0 from seal center
    var scale: Float = 1.0f,
    var rotationDeg: Float = 0f,
    var color: Color = Color(0xFFFFD700)
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("glyph", glyph)
        put("name", name)
        put("normX", normX.toDouble())
        put("normY", normY.toDouble())
        put("scale", scale.toDouble())
        put("rotationDeg", rotationDeg.toDouble())
        put("colorHex", color.value.toLong())
    }

    companion object {
        fun fromJson(obj: JSONObject): PlacedGlyph = PlacedGlyph(
            id = obj.optString("id", "g_${System.currentTimeMillis()}"),
            glyph = obj.optString("glyph", "☉"),
            name = obj.optString("name", "Sol"),
            normX = obj.optDouble("normX", 0.0).toFloat(),
            normY = obj.optDouble("normY", 0.0).toFloat(),
            scale = obj.optDouble("scale", 1.0).toFloat(),
            rotationDeg = obj.optDouble("rotationDeg", 0.0).toFloat(),
            color = Color(obj.optLong("colorHex", 0xFFFFD700L).toULong())
        )
    }
}

data class PersonalSigil(
    val id: String,
    val name: String,
    val motto: String,
    val geometry: SealBaseGeometry,
    val tincture: SealTincture,
    val glyphs: List<PlacedGlyph>,
    val createdTimestamp: Long
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("motto", motto)
        put("geometry", geometry.name)
        put("tincture", tincture.name)
        val arr = JSONArray()
        glyphs.forEach { arr.put(it.toJson()) }
        put("glyphs", arr)
        put("createdTimestamp", createdTimestamp)
    }

    companion object {
        fun fromJson(obj: JSONObject): PersonalSigil {
            val glyphsArr = obj.optJSONArray("glyphs") ?: JSONArray()
            val glyphsList = mutableListOf<PlacedGlyph>()
            for (i in 0 until glyphsArr.length()) {
                val gObj = glyphsArr.optJSONObject(i) ?: continue
                glyphsList.add(PlacedGlyph.fromJson(gObj))
            }
            val geomName = obj.optString("geometry", SealBaseGeometry.SOLOMON_HEXAGRAM.name)
            val geometry = try { SealBaseGeometry.valueOf(geomName) } catch (_: Exception) { SealBaseGeometry.SOLOMON_HEXAGRAM }
            val tincName = obj.optString("tincture", SealTincture.ALCHEMICAL_GOLD.name)
            val tincture = try { SealTincture.valueOf(tincName) } catch (_: Exception) { SealTincture.ALCHEMICAL_GOLD }

            return PersonalSigil(
                id = obj.optString("id", "sigil_${System.currentTimeMillis()}"),
                name = obj.optString("name", "Sanctum Sigil"),
                motto = obj.optString("motto", "Solve et Coagula"),
                geometry = geometry,
                tincture = tincture,
                glyphs = glyphsList,
                createdTimestamp = obj.optLong("createdTimestamp", System.currentTimeMillis())
            )
        }
    }
}

data class SigilMotifItem(
    val glyph: String,
    val name: String,
    val category: String,
    val meaning: String
)

object SigilMotifCatalog {
    val allMotifs = listOf(
        // Planetary Spheres
        SigilMotifItem("☉", "Sol", "Planetary", "Solar consciousness, sovereign gold, illumination"),
        SigilMotifItem("☽", "Luna", "Planetary", "Lunar reflection, pure silver, dream intuition"),
        SigilMotifItem("☿", "Mercurius", "Planetary", "Quicksilver, divine speech, mental transmutation"),
        SigilMotifItem("♀", "Venus", "Planetary", "Copper, harmony, aesthetic beauty, love"),
        SigilMotifItem("♂", "Mars", "Planetary", "Iron, calcination fire, martial willpower"),
        SigilMotifItem("♃", "Jupiter", "Planetary", "Tin, philosophical expansion, benevolence"),
        SigilMotifItem("♄", "Saturn", "Planetary", "Lead, the Nigredo stage, time, grounding"),

        // Elemental Forces
        SigilMotifItem("🜂", "Ignis (Fire)", "Elements", "Upward aspiration, purifying heat, light"),
        SigilMotifItem("🜄", "Aqua (Water)", "Elements", "Downward dissolution, receptive depths"),
        SigilMotifItem("🜁", "Aer (Air)", "Elements", "Subtle spirit, intellectual breath, levity"),
        SigilMotifItem("🜃", "Terra (Earth)", "Elements", "Fixation, crystalline substance, matter"),
        SigilMotifItem("🜀", "Quintessence", "Elements", "Aether, the universal animating life-force"),

        // Tria Prima & Magnum Opus
        SigilMotifItem("🜍", "Sulfur", "Tria Prima", "The immortal Soul, combustible principle"),
        SigilMotifItem("🜎", "Mercury", "Tria Prima", "The mediating Spirit, fluid intellect"),
        SigilMotifItem("🜔", "Salt", "Tria Prima", "The physical Body, crystalline permanence"),
        SigilMotifItem("🜚", "Aurum (Gold)", "Magnum Opus", "The perfected solar matter of the Great Work"),
        SigilMotifItem("🜛", "Philosopher's Stone", "Magnum Opus", "Lapis Philosophorum, universal medicine"),
        SigilMotifItem("🜫", "V.I.T.R.I.O.L.", "Magnum Opus", "The occult stone found in earth's interior"),

        // Sacred Vessels & Alchemical Signs
        SigilMotifItem("🝚", "Alembic", "Vessels", "Vessel of distillation and sublimated vapor"),
        SigilMotifItem("🝟", "Crucible", "Vessels", "Vessel of fiery trial and purification"),
        SigilMotifItem("🝪", "Antimony", "Vessels", "The Grey Wolf that devours base metals"),
        SigilMotifItem("🜈", "Tartar", "Vessels", "Salt of wine, coagulated foundation"),
        SigilMotifItem("🜖", "Vitriol of Venus", "Vessels", "Green lion dissolving solar gold"),

        // Esoteric Archetypes
        SigilMotifItem("🜏", "Monas Hieroglyphica", "Archetypes", "John Dee's unified cosmological glyph"),
        SigilMotifItem("𓂀", "Eye of Hermes", "Archetypes", "Omniscient perception and awakened vision"),
        SigilMotifItem("☥", "Ankh of Life", "Archetypes", "Eternal breath and divine vital union"),
        SigilMotifItem("⚕", "Caduceus", "Archetypes", "Twin entwined serpents of polar reconciliation"),
        SigilMotifItem("✦", "Aether Spark", "Archetypes", "Celestial divine spark of light"),
        SigilMotifItem("✺", "Solar Glory", "Archetypes", "Radiant emanations of the Central Sun"),
        SigilMotifItem("♁", "Earth Globus", "Archetypes", "Sovereign realm and grounded reality")
    )
}
