package com.example.ui.manuscripts

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*

data class HistoricalManuscript(
    val id: String,
    val title: String,
    val latinTitle: String,
    val author: String,
    val publicationDate: String,
    val discipline: String, // "Esoteric Science", "Alchemy", "Magic", "Philosophy", "Psychology"
    val sealGlyph: String,
    val rubricTitle: String,
    val initialLetter: String,
    val originalLatinText: String,
    val translatedText: String,
    val hiddenMarginalia: String,
    val sympatheticHeatSecret: String,
    val woodcutType: String // "ouroboros", "caduceus", "solomon", "hermetic_sun"
)

@Composable
fun HistoricalManuscriptModule(
    modifier: Modifier = Modifier
) {
    val manuscripts = remember {
        listOf(
            HistoricalManuscript(
                id = "emerald_tablet",
                title = "The Emerald Tablet",
                latinTitle = "Tabula Smaragdina Hermetis",
                author = "Hermes Trismegistus (Annotated by Isaac Newton, 1680)",
                publicationDate = "Ancient Origin • 17th Century Translation",
                discipline = "Esoteric Science",
                sealGlyph = "🜫",
                rubricTitle = "DE ALCHIMIA ET OPERE NATURALI",
                initialLetter = "V",
                originalLatinText = "Verum sine mendacio, certum et verissimum: Quod est inferius est sicut quod est superius, et quod est superius est sicut quod est inferius, ad perpetranda miracula rei unius.",
                translatedText = "Tis true without lying, certain and most true: That which is Below corresponds to that which is Above, and that which is Above corresponds to that which is Below, to accomplish the miracles of the One Thing. Its father is the Sun, its mother the Moon, the wind carried it in its belly, its nurse is the Earth.",
                hiddenMarginalia = "Newton's Marginal Note (MS 416): 'The Sun represents the Solar Spirit, active sulfur; the Moon is philosophical quicksilver. Without both in equilibrium, the Great Work fails.'",
                sympatheticHeatSecret = "SECRET SPAGYRIC CIPHER: Separate the subtle from the gross with great industry and gentle heat. It ascends from earth to heaven and descends again, uniting the powers of above and below.",
                woodcutType = "hermetic_sun"
            ),
            HistoricalManuscript(
                id = "atalanta_fugiens",
                title = "Atalanta Fugiens (Emblem XXI)",
                latinTitle = "Atalanta Fugiens: Emblemata Nova",
                author = "Count Michael Maier (Physician to Rudolf II)",
                publicationDate = "Oppenheim, Anno Domini 1618",
                discipline = "Alchemy",
                sealGlyph = "🜛",
                rubricTitle = "DE QUADRATURA CIRCULI ET LAPIDE",
                initialLetter = "F",
                originalLatinText = "Fac ex mare et foemina circulum, inde quadrangulum, hinc triangulum, fac circulum et habebis lapidem Philosophorum.",
                translatedText = "Make of a man and woman a circle; then a quadrangle; out of this a triangle; make again a circle, and thou wilt possess the Philosopher's Stone. The masculine solar fire must wed the feminine lunar water, square the physical four elements, and rise into the divine triune spirit.",
                hiddenMarginalia = "Rosicrucian Seal: 'The circle is the Monad; the square represents the four earthly elements; the triangle is the Tria Prima: Sulfur, Mercury, and Salt.'",
                sympatheticHeatSecret = "SECRET RECIPE OF MAIER: Dissolve the Red King in the Lac Virginis. Let the raven consume its own feathers before the white swan appears on the surface of the flask.",
                woodcutType = "ouroboros"
            ),
            HistoricalManuscript(
                id = "de_occulta_philosophia",
                title = "Three Books of Occult Philosophy",
                latinTitle = "De Occulta Philosophia Libri Tres",
                author = "Heinrich Cornelius Agrippa",
                publicationDate = "Cologne, Anno Domini 1533",
                discipline = "Magic",
                sealGlyph = "✡",
                rubricTitle = "DE TRIBUS MUNDIS ET NEXU HARMONIAE",
                initialLetter = "C",
                originalLatinText = "Cum triplex sit mundus—Elementalis, Coelestis, et Intellectualis—omnisque inferior a suo superiore regatur, sapientes arbitrantur nihil absurdum esse a minimo ad supremum ascendere.",
                translatedText = "Seeing there is a three-fold World—Elementary, Celestial, and Intellectual—and every inferior is governed by its superior, wise men judge it no absurd thing to climb from the lowest to the highest by the sacred ladder of Correspondences.",
                hiddenMarginalia = "Cabalistic Marginalia: 'The celestial characters are written upon the vault of heaven. He who understands the signatures of plants can heal the astral body.'",
                sympatheticHeatSecret = "THEURGIC SEALS: Combine the planetary seal of Mercury with amber resin on Dies Mercurii to invoke clarity of prophetic dreams.",
                woodcutType = "solomon"
            ),
            HistoricalManuscript(
                id = "kybalion_manuscript",
                title = "The Kybalion Scroll of the Seven Laws",
                latinTitle = "Rotula Kybalionis Aegyptiaca",
                author = "The Three Initiates (Hermetic Oral Tradition)",
                publicationDate = "Ancient Hermetic Gnosis",
                discipline = "Philosophy",
                sealGlyph = "🜚",
                rubricTitle = "DE SEPTEM PRINCIPIIS VERITATIS",
                initialLetter = "P",
                originalLatinText = "Principia Veritatis septem sunt; qui haec novit, intellegens, clavem possidet magicam cuius ad tactum omnes Templi ianuae aperiuntur.",
                translatedText = "The Principles of Truth are Seven; he who knows these, understandingly, possesses the Magic Key before whose touch all the Doors of the Temple fly open: Mentalism, Correspondence, Vibration, Polarity, Rhythm, Cause & Effect, and Gender.",
                hiddenMarginalia = "Axiom of Hermes: 'Mind may be transmuted from state to state, degree to degree, pole to pole, vibration to vibration. True transmutation is a mental art.'",
                sympatheticHeatSecret = "KEY TO MENTAL ALCHEMY: Change your polarity rather than fighting the lower state. To conquer fear, focus wholly upon cultivating courage.",
                woodcutType = "caduceus"
            ),
            HistoricalManuscript(
                id = "jung_alchemical_psych",
                title = "Mysterium Coniunctionis & Liber Novus",
                latinTitle = "Mysterium Coniunctionis: The Red Book",
                author = "Dr. Carl Gustav Jung",
                publicationDate = "Zürich, Psychological Treatises",
                discipline = "Psychology",
                sealGlyph = "☿",
                rubricTitle = "DE ANIMA, SHADOW ET INDIVIDUATIONE",
                initialLetter = "O",
                originalLatinText = "Opus alchemicum est transformatio animae per coniunctionem oppositorum. Lapis non est extra te, sed intus in profundo inconscii.",
                translatedText = "The alchemical opus is nothing less than the transformation of the psyche through the conjunction of opposites. The Philosopher's Stone is not an external talisman, but the integrated Self shining from the depths of the unconscious.",
                hiddenMarginalia = "Jung's Active Imagination Note: 'The confrontation with the Shadow is the Nigredo. The whitening is the integration of the Anima; the reddening is the realization of wholeness.'",
                sympatheticHeatSecret = "PSYCHOLOGICAL TRANSMUTATION: What is not brought to consciousness manifests externally as fate. Make the darkness conscious.",
                woodcutType = "ouroboros"
            )
        )
    }

    var selectedManuscriptIndex by remember { mutableIntStateOf(0) }
    var showParacelsusLoupe by remember { mutableStateOf(false) }
    var isAthanorHeatApplied by remember { mutableStateOf(false) }

    val currentManuscript = manuscripts[selectedManuscriptIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("historical_manuscript_module"),
        colors = CardDefaults.cardColors(containerColor = ChamberDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder))
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WaxSealBadge(glyph = currentManuscript.sealGlyph, size = 38.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CODEX ILLUMINATUS",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp,
                            color = AntiqueGold
                        )
                        Text(
                            text = "Interactive 17th-Century Manuscript Folio",
                            fontFamily = GaramondFont,
                            fontSize = 12.sp,
                            color = ParchmentDark
                        )
                    }
                }

                Text(
                    text = "FOLIO ${selectedManuscriptIndex + 1}/${manuscripts.size}",
                    fontFamily = CinzelFont,
                    fontSize = 11.sp,
                    color = CitrinitasAmber
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Manuscript Selector Horizontal Ribbon
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(manuscripts.indices.toList()) { idx ->
                    val ms = manuscripts[idx]
                    val isSelected = idx == selectedManuscriptIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) AntiqueGold else ChamberSurface)
                            .border(1.dp, if (isSelected) GoldBright else ChamberBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                selectedManuscriptIndex = idx
                                showParacelsusLoupe = false
                                isAthanorHeatApplied = false
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = ms.title,
                            fontFamily = CinzelFont,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NigredoBlack else ParchmentLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // The Physical Weather-Beaten Manuscript Folio View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                if (isAthanorHeatApplied) Color(0xFFFAF0DC) else ParchmentLight,
                                if (isAthanorHeatApplied) Color(0xFFF3DFB8) else ParchmentBase,
                                if (isAthanorHeatApplied) Color(0xFFE8CA94) else ParchmentDark
                            )
                        )
                    )
                    .border(1.5.dp, ChamberBorder, RoundedCornerShape(10.dp))
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Rubric Header in Red Iron Ink
                    Text(
                        text = currentManuscript.rubricTitle,
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        color = WaxSealRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentManuscript.title,
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = IronGallInk,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Ex Officina: ${currentManuscript.author} • ${currentManuscript.publicationDate}",
                        fontFamily = GaramondFont,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = FadedInk,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Emblem Woodcut Canvas Drawing
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ParchmentDark.copy(alpha = 0.35f))
                            .border(1.dp, FadedInk.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    ) {
                        HistoricalWoodcutCanvas(
                            woodcutType = currentManuscript.woodcutType,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Latin Text with Historiated Initial Drop-Cap
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Drop-Cap Initial
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(WaxSealRed)
                                .border(1.dp, AntiqueGold, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentManuscript.initialLetter,
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = ParchmentLight
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = currentManuscript.originalLatinText.removePrefix(currentManuscript.initialLetter),
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = IronGallInk
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // English Translation
                    Text(
                        text = "VERNACULAR TRANSLATION:",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp,
                        color = WaxSealRed
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentManuscript.translatedText,
                        fontFamily = GaramondFont,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = IronGallInk
                    )

                    // Secret 1: Paracelsus Loupe (Reveals hidden marginalia)
                    AnimatedVisibility(visible = showParacelsusLoupe) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(AntiqueGold.copy(alpha = 0.2f))
                                .border(1.dp, AntiqueGold, RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔍 LOUPE OF PARACELSUS — REVEALED MARGINALIA", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = IronGallInk)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentManuscript.hiddenMarginalia,
                                fontFamily = GaramondFont,
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = IronGallInk
                            )
                        }
                    }

                    // Secret 2: Athanor Heat (Reveals invisible sympathetic ink)
                    AnimatedVisibility(visible = isAthanorHeatApplied) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(WaxSealRed.copy(alpha = 0.15f))
                                .border(1.dp, WaxSealRed, RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔥 ATHANOR HEAT: SYMPATHETIC INK CIPHER", fontFamily = CinzelFont, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = WaxSealRed)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentManuscript.sympatheticHeatSecret,
                                fontFamily = GaramondFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = WaxSealRed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Alchemical Tools for Manuscript Inspection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Loupe of Paracelsus toggle
                Button(
                    onClick = {
                        com.example.audio.AlchemicalAudioEngine.getInstance().playWaxSeal()
                        showParacelsusLoupe = !showParacelsusLoupe
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showParacelsusLoupe) AntiqueGold else ChamberSurface,
                        contentColor = if (showParacelsusLoupe) NigredoBlack else ParchmentLight
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (showParacelsusLoupe) "Conceal Loupe" else "Paracelsus Loupe", fontFamily = CinzelFont, fontSize = 11.sp)
                }

                // Athanor Heat toggle (warm the parchment)
                Button(
                    onClick = {
                        com.example.audio.AlchemicalAudioEngine.getInstance().playBubblingCauldron()
                        isAthanorHeatApplied = !isAthanorHeatApplied
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAthanorHeatApplied) WaxSealRed else ChamberSurface,
                        contentColor = ParchmentLight
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAthanorHeatApplied) "Cool Folio" else "Heat Folio", fontFamily = CinzelFont, fontSize = 11.sp)
                }
            }
        }
    }
}

/**
 * Custom Canvas drawing of 17th-century alchemical emblem woodcuts:
 * Ouroboros, Hermetic Sun, Caduceus, and Seal of Solomon.
 */
@Composable
private fun HistoricalWoodcutCanvas(
    woodcutType: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        // Woodcut framing lines
        drawRect(
            color = FadedInk.copy(alpha = 0.4f),
            topLeft = Offset(8f, 8f),
            size = Size(w - 16f, h - 16f),
            style = Stroke(1.5f)
        )

        when (woodcutType) {
            "ouroboros" -> {
                // Ouroboros Serpent devouring tail
                val radius = h * 0.36f
                drawCircle(
                    color = IronGallInk,
                    radius = radius,
                    center = center,
                    style = Stroke(5f)
                )
                drawCircle(
                    color = AntiqueGold,
                    radius = radius * 0.85f,
                    center = center,
                    style = Stroke(1.5f)
                )
                // Head / Eye
                drawCircle(
                    color = WaxSealRed,
                    radius = 5f,
                    center = Offset(center.x + radius, center.y - 10f)
                )
                // Greek "En To Pan" in center
                // Central gold point
                drawCircle(AntiqueGold, radius = 4f, center = center)
            }
            "hermetic_sun" -> {
                // The Radiant Hermetic Sun with 16 rays
                val sunR = h * 0.22f
                drawCircle(
                    brush = Brush.radialGradient(listOf(GoldBright, AntiqueGold, WaxSealRed)),
                    radius = sunR,
                    center = center
                )
                drawCircle(IronGallInk, radius = sunR, center = center, style = Stroke(2f))

                for (i in 0 until 16) {
                    val angle = (i * (2 * Math.PI / 16)).toFloat()
                    val x1 = center.x + sunR * kotlin.math.cos(angle)
                    val y1 = center.y + sunR * kotlin.math.sin(angle)
                    val rayLen = if (i % 2 == 0) 24f else 14f
                    val x2 = center.x + (sunR + rayLen) * kotlin.math.cos(angle)
                    val y2 = center.y + (sunR + rayLen) * kotlin.math.sin(angle)
                    drawLine(IronGallInk, Offset(x1, y1), Offset(x2, y2), strokeWidth = 2f)
                }
            }
            "solomon" -> {
                // The Great Hexagram
                val hexR = h * 0.35f
                val p1 = Path().apply {
                    val a1 = Offset(center.x, center.y - hexR)
                    val a2 = Offset(center.x + hexR * 0.866f, center.y + hexR * 0.5f)
                    val a3 = Offset(center.x - hexR * 0.866f, center.y + hexR * 0.5f)
                    moveTo(a1.x, a1.y); lineTo(a2.x, a2.y); lineTo(a3.x, a3.y); close()
                }
                val p2 = Path().apply {
                    val b1 = Offset(center.x, center.y + hexR)
                    val b2 = Offset(center.x + hexR * 0.866f, center.y - hexR * 0.5f)
                    val b3 = Offset(center.x - hexR * 0.866f, center.y - hexR * 0.5f)
                    moveTo(b1.x, b1.y); lineTo(b2.x, b2.y); lineTo(b3.x, b3.y); close()
                }
                drawPath(p1, color = AntiqueGold, style = Stroke(2.5f))
                drawPath(p2, color = IronGallInk, style = Stroke(2.5f))
                drawCircle(WaxSealRed, radius = 6f, center = center)
            }
            else -> {
                // Caduceus of Hermes
                val staffTop = h * 0.18f
                val staffBottom = h * 0.82f
                drawLine(AntiqueGold, Offset(center.x, staffTop), Offset(center.x, staffBottom), strokeWidth = 3f)

                // Serpents
                drawArc(
                    color = IronGallInk,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(center.x - 25f, center.y - 30f),
                    size = Size(50f, 30f),
                    style = Stroke(2f)
                )
                drawArc(
                    color = IronGallInk,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(center.x - 25f, center.y),
                    size = Size(50f, 30f),
                    style = Stroke(2f)
                )
                drawCircle(GoldBright, radius = 7f, center = Offset(center.x, staffTop))
            }
        }
    }
}
