package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.manuscripts.HistoricalManuscriptModule
import com.example.ui.components.ParchmentScroll
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*

data class ManuscriptTreatise(
    val id: String,
    val title: String,
    val latinTitle: String,
    val author: String,
    val era: String,
    val category: String,
    val glyph: String,
    val excerpt: String,
    val deepLore: String
)

@Composable
fun CompendiumScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Treatises") }
    var expandedTreatiseId by remember { mutableStateOf<String?>("emerald_tablet_full") }

    val categories = listOf(
        "All Treatises",
        "The Kybalion",
        "The Emerald Tablet",
        "Corpus Hermeticum",
        "Spiritual Alchemy",
        "Ancient Magic",
        "Alchemical Psychology"
    )

    val treatises = remember {
        listOf(
            ManuscriptTreatise(
                id = "emerald_tablet_full",
                title = "The Emerald Tablet of Hermes",
                latinTitle = "Tabula Smaragdina Hermetis",
                author = "Hermes Trismegistus (Trans. Isaac Newton, c. 1680)",
                era = "Ancient / 17th Century Translation",
                category = "The Emerald Tablet",
                glyph = "🜫",
                excerpt = "“That which is Below corresponds to that which is Above, and that which is Above corresponds to that which is Below, to accomplish the miracles of the One Thing.”",
                deepLore = "Composed of thirteen cryptic precepts, the Emerald Tablet is the bedrock of Western esotericism. Discovered in an emerald vault beneath the tomb of Hermes, it details how the Universal Substance (Telesma) is received from the celestial realms, nourished by the earth, separated from gross matter by gentle heat, and reunited with supreme power. Sir Isaac Newton devoted over thirty years of his secret life in Cambridge translating and studying this text alongside his optics and gravitation treatises."
            ),
            ManuscriptTreatise(
                id = "kybalion_origins",
                title = "The Kybalion: Master Key to the Arcana",
                latinTitle = "Clavis Aurea Kybalionis",
                author = "The Three Initiates (William Walker Atkinson)",
                era = "Hermetic Tradition of Ancient Egypt & Greece",
                category = "The Kybalion",
                glyph = "🜚",
                excerpt = "“The lips of wisdom are closed, except to the ears of Understanding. When the ears of the student are ready to hear, then cometh the lips to fill them with Wisdom.”",
                deepLore = "The Kybalion summarizes the secret teachings transmitted orally for thousands of years from the temples of Karnak and Alexandria. It details how the entire visible universe is a mental creation within the Infinite Living Mind of THE ALL. Master Hermetists do not break physical laws, but employ higher mental laws against lower physical laws to achieve Mental Transmutation."
            ),
            ManuscriptTreatise(
                id = "magnum_opus_stages",
                title = "The Four Operations of the Magnum Opus",
                latinTitle = "Quattuor Gradus Operis Magni",
                author = "Gerhard Dorn & Nicolas Flamel",
                era = "16th–17th Century Spagyrics",
                category = "Spiritual Alchemy",
                glyph = "🜛",
                excerpt = "“Solve et Coagula: Dissolve the rigid pride of the conscious mind, and coagulate the purified wisdom into enduring spiritual reality.”",
                deepLore = "The Great Work proceeds in four sacred chromatic stages:\n1. NIGREDO (The Blackening): The Putrefactio, where the prideful ego is dissolved in the black raven's ash.\n2. ALBEDO (The Whitening): The lunar cleansing by dew, revealing the shining white swan of pristine consciousness.\n3. CITRINITAS (The Yellowing): The golden sunrise of active intellect, symbolized by the soaring eagle.\n4. RUBEDO (The Reddening): The supreme union of Sol and Luna, producing the Philosopher's Stone which transmutes all baseness into incorruptible gold."
            ),
            ManuscriptTreatise(
                id = "poimandres_nous",
                title = "Poimandres: The Shepherd of Men",
                latinTitle = "Poimandres seu De Potestate Divina",
                author = "Hermes Trismegistus (Corpus Hermeticum)",
                era = "2nd Century Hellenistic Egypt",
                category = "Corpus Hermeticum",
                glyph = "☉",
                excerpt = "“I am Poimandres, the Mind of Sovereign Authority. I know what thou desirest, and I am with thee everywhere... Holy is God, the Father of all, whose Will is performed by His own Powers.”",
                deepLore = "In a state of deep lucid trance, Hermes is visited by the infinite dragon of light, Poimandres, who reveals the cosmogenesis of the universe. Man was created in the image of the Divine Mind, endowed with creative power, but descended into the seven planetary spheres (the Hebdomad) and became ensnared by matter. Salvation consists in shedding the planetary vices upon ascending back through the spheres to the Eighth Heaven of pure light."
            ),
            ManuscriptTreatise(
                id = "agrippa_occult_phil",
                title = "Three Books of Occult Philosophy",
                latinTitle = "De Occulta Philosophia Libri Tres",
                author = "Heinrich Cornelius Agrippa of Nettesheim",
                era = "Published 1533, Widely Studied in 17th Century",
                category = "Ancient Magic",
                glyph = "✡",
                excerpt = "“Magic is a faculty of wonderful virtue, full of most high mysteries, containing the most profound contemplation of most secret things, their nature, power, quality, and substance.”",
                deepLore = "Agrippa systematically categorized magical science into three hierarchical tiers:\n1. Natural Magic (Elementary World): Studying the occult properties of herbs, gems, animals, and the physical elements.\n2. Celestial Magic (Astrological World): Planetary talismans, mathematical harmonies, and musical ratios.\n3. Ceremonial Magic (Intelligible World): Invoking divine names, angelology, and the sacred theurgy of the soul."
            ),
            ManuscriptTreatise(
                id = "jung_alchemical_psych",
                title = "Psychology and Alchemy: The Inner Crucible",
                latinTitle = "Psychologia et Alchimia",
                author = "Dr. Carl Gustav Jung",
                era = "Modern Archetypal Psychology",
                category = "Alchemical Psychology",
                glyph = "☿",
                excerpt = "“The alchemical opus deals with the transformation of the personality through the blending and fusion of the noble with the base components, of the conscious with the unconscious.”",
                deepLore = "Jung discovered that the cryptic Latin manuscripts, alembics, and dragons of the medieval and 17th-century alchemists were spontaneous symbolic projections of the human individuation process. The 'Prima Materia' is the repressed Shadow. The 'Vessel of Hermes' is the safe analytical container. The 'Lapis' is the integrated Self—the divine spark realized in mortal form."
            )
        )
    }

    val filteredTreatises = treatises.filter { treatise ->
        val matchesCategory = selectedCategory == "All Treatises" || treatise.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                treatise.title.contains(searchQuery, ignoreCase = true) ||
                treatise.deepLore.contains(searchQuery, ignoreCase = true) ||
                treatise.author.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize().background(GrimoireCover)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BIBLIOTHECA HERMETICA",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 1.5.sp,
                        color = AntiqueGold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ancient Manuscripts, Treatises & Occult Sciences",
                        fontFamily = GaramondFont,
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp,
                        color = ParchmentDark,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Interactive 17th Century Historical Manuscript Folio Viewer
            item {
                HistoricalManuscriptModule(modifier = Modifier.fillMaxWidth())
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().testTag("compendium_search_field"),
                    placeholder = {
                        Text(
                            "Search manuscripts, authors, Latin maxims...",
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            color = ParchmentDark.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = AntiqueGold)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ParchmentLight,
                        unfocusedTextColor = ParchmentLight,
                        focusedBorderColor = AntiqueGold,
                        unfocusedBorderColor = ChamberBorder,
                        focusedContainerColor = ChamberDark,
                        unfocusedContainerColor = ChamberDark
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Category Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AntiqueGold else ChamberSurface)
                                .border(1.dp, if (isSelected) GoldBright else ChamberBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat,
                                fontFamily = CinzelFont,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp,
                                color = if (isSelected) NigredoBlack else ParchmentLight
                            )
                        }
                    }
                }
            }

            // Treatises List
            items(filteredTreatises) { treatise ->
                val isExpanded = expandedTreatiseId == treatise.id

                ParchmentScroll(
                    modifier = Modifier.fillMaxWidth().testTag("treatise_${treatise.id}"),
                    title = treatise.title,
                    latinMotto = treatise.latinTitle,
                    sealGlyph = treatise.glyph,
                    onSealClick = {
                        expandedTreatiseId = if (isExpanded) null else treatise.id
                    }
                ) {
                    // Author and era are stacked vertically. Previously they shared a Row
                    // with no width limits: the long author name consumed the full width and
                    // the era text was squeezed to ~0px, wrapping one character per line.
                    // That produced the tall blank area (Emerald Tablet) and the characters
                    // spilling off the right-hand edge (Kybalion and following treatises).
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "AUTHOR: ${treatise.author}",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = WaxSealRed,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = treatise.era,
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = FadedInk,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Excerpt
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ParchmentDark.copy(alpha = 0.5f))
                            .border(0.8.dp, FadedInk.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = treatise.excerpt,
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = IronGallInk
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            Text(
                                text = "TREATISE COMMENTARY & ESOTERIC EXPOSITION",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = IronGallInk,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = treatise.deepLore,
                                fontFamily = GaramondFont,
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                color = FadedInk
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Expand / Collapse Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedTreatiseId = if (isExpanded) null else treatise.id
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "Seal Treatise" else "Unroll Full Folio",
                            fontFamily = CinzelFont,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaxSealRed
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = WaxSealRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
