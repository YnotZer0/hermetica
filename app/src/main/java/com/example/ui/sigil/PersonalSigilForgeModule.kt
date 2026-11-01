package com.example.ui.sigil

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AlchemicalAudioEngine
import com.example.data.model.*
import com.example.data.repository.HermeticRepository
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*
import kotlin.math.*

/**
 * 17th-Century Personal Sigil Forge & Alchemical Seal Creator:
 * Provides an interactive drag-and-drop parchment/wax matrix canvas allowing adepts to compose
 * bespoke alchemical seals using authentic Renaissance geometry, sacred glyphs, and motto inscriptions.
 */
@Composable
fun PersonalSigilForgeModule(
    repository: HermeticRepository,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val audioEngine = remember { AlchemicalAudioEngine.getInstance() }
    val personalSigils by repository.personalSigils.collectAsState()

    // Seal Form State
    var sigilName by remember { mutableStateOf("Seal of the Philosopher") }
    var sigilMotto by remember { mutableStateOf("Solve et Coagula") }
    var selectedGeometry by remember { mutableStateOf(SealBaseGeometry.SOLOMON_HEXAGRAM) }
    var selectedTincture by remember { mutableStateOf(SealTincture.ALCHEMICAL_GOLD) }

    // Placed symbols on canvas
    var placedGlyphs by remember {
        mutableStateOf(
            listOf(
                PlacedGlyph("g_sol", "☉", "Sol", 0f, -0.42f, 1.25f, 0f, GoldBright),
                PlacedGlyph("g_luna", "☽", "Luna", -0.40f, 0.28f, 1.15f, 0f, Color(0xFFE2E8F0)),
                PlacedGlyph("g_merc", "☿", "Mercurius", 0.40f, 0.28f, 1.15f, 0f, AntiqueGold),
                PlacedGlyph("g_lapis", "🜛", "Lapis", 0f, 0.05f, 1.4f, 0f, RubedoCrimson)
            )
        )
    }

    var selectedGlyphId by remember { mutableStateOf<String?>("g_lapis") }
    val selectedGlyph = placedGlyphs.find { it.id == selectedGlyphId }

    // Palette Category filter
    val categories = listOf("All", "Planetary", "Elements", "Tria Prima", "Vessels", "Archetypes")
    var selectedCategory by remember { mutableStateOf("All") }

    var consecrationSuccess by remember { mutableStateOf(false) }
    var showSavedLibrary by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireCover)
            .testTag("personal_sigil_forge")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🜚", fontSize = 20.sp, color = AntiqueGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PERSONAL SIGIL FORGE",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 2.sp,
                                color = AntiqueGold
                            )
                        }
                        Text(
                            text = "Matrix of Renaissance Hermetic Talismans",
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = CitrinitasAmber
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                showSavedLibrary = !showSavedLibrary
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChamberSurface),
                            border = BorderStroke(1.dp, ChamberBorder),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (showSavedLibrary) "✦ Forge" else "🏛 Vault (${personalSigils.size})",
                                fontFamily = CinzelFont,
                                fontSize = 11.sp,
                                color = AntiqueGold
                            )
                        }
                    }
                }
            }

            if (showSavedLibrary) {
                // Saved Sigils Vault View
                item {
                    SavedSigilsVaultSection(
                        sigils = personalSigils,
                        onLoadSigil = { loaded ->
                            sigilName = loaded.name
                            sigilMotto = loaded.motto
                            selectedGeometry = loaded.geometry
                            selectedTincture = loaded.tincture
                            placedGlyphs = loaded.glyphs.map { it.copy() }
                            selectedGlyphId = loaded.glyphs.firstOrNull()?.id
                            showSavedLibrary = false
                        },
                        onDeleteSigil = { id ->
                            repository.deletePersonalSigil(id)
                        }
                    )
                }
            } else {
                // Main Forge Canvas Item
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ChamberDark),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "“AS ABOVE, SO BELOW • AS WITHIN, SO WITHOUT”",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.5.sp,
                                color = CitrinitasAmber
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Interactive Drag & Drop Sigil Matrix Canvas
                            InteractiveSigilCanvas(
                                geometry = selectedGeometry,
                                tincture = selectedTincture,
                                motto = sigilMotto,
                                glyphs = placedGlyphs,
                                selectedGlyphId = selectedGlyphId,
                                onSelectGlyph = { id ->
                                    selectedGlyphId = id
                                    audioEngine.playCipherClick()
                                },
                                onMoveGlyph = { id, normX, normY ->
                                    placedGlyphs = placedGlyphs.map {
                                        if (it.id == id) it.copy(normX = normX, normY = normY) else it
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Drag symbols on canvas to position. Tap any symbol to inspect.",
                                fontFamily = GaramondFont,
                                fontStyle = FontStyle.Italic,
                                fontSize = 11.sp,
                                color = ParchmentDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Selected Glyph Inspector / Adjustment Bar
                if (selectedGlyph != null) {
                    item {
                        GlyphInspectorCard(
                            glyph = selectedGlyph,
                            onUpdate = { updated ->
                                placedGlyphs = placedGlyphs.map { if (it.id == updated.id) updated else it }
                            },
                            onRemove = {
                                audioEngine.playCipherClick()
                                placedGlyphs = placedGlyphs.filter { it.id != selectedGlyph.id }
                                selectedGlyphId = placedGlyphs.firstOrNull()?.id
                            },
                            onDuplicate = {
                                audioEngine.playCipherClick()
                                val newGlyph = selectedGlyph.copy(
                                    id = "g_${System.currentTimeMillis()}",
                                    normX = (selectedGlyph.normX + 0.1f).coerceIn(-0.7f, 0.7f),
                                    normY = (selectedGlyph.normY + 0.1f).coerceIn(-0.7f, 0.7f)
                                )
                                placedGlyphs = placedGlyphs + newGlyph
                                selectedGlyphId = newGlyph.id
                            }
                        )
                    }
                }

                // Motif Catalog Palette (Tap-to-add / Drag)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SACRED MOTIF CATALOG (${placedGlyphs.size} on canvas):",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = AntiqueGold
                            )

                            Text(
                                text = "Tap symbol to place",
                                fontFamily = GaramondFont,
                                fontStyle = FontStyle.Italic,
                                fontSize = 11.sp,
                                color = ParchmentDark
                            )
                        }

                        // Category Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { cat ->
                                val isSelected = cat == selectedCategory
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) AntiqueGold else ChamberSurface)
                                        .border(1.dp, if (isSelected) GoldBright else ChamberBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        audioEngine.playCipherClick()
                                        selectedCategory = cat
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontFamily = CinzelFont,
                                        fontSize = 10.sp,
                                        color = if (isSelected) NigredoBlack else ParchmentLight
                                    )
                                }
                            }
                        }

                        // Motif Symbols Horizontal Grid
                        val filteredMotifs = remember(selectedCategory) {
                            if (selectedCategory == "All") SigilMotifCatalog.allMotifs
                            else SigilMotifCatalog.allMotifs.filter { it.category == selectedCategory }
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(filteredMotifs) { motif ->
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ChamberSurface)
                                        .border(1.dp, AntiqueGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            audioEngine.playInkSpread()
                                            // Add to center with slight offset
                                            val newId = "g_${System.currentTimeMillis()}"
                                            val newG = PlacedGlyph(
                                                id = newId,
                                                glyph = motif.glyph,
                                                name = motif.name,
                                                normX = (placedGlyphs.size % 3 - 1) * 0.28f,
                                                normY = ((placedGlyphs.size / 3) % 3 - 1) * 0.28f,
                                                scale = 1.15f,
                                                rotationDeg = 0f,
                                                color = selectedTincture.primaryColor
                                            )
                                            placedGlyphs = placedGlyphs + newG
                                            selectedGlyphId = newId
                                        }
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = motif.glyph, fontSize = 22.sp, color = GoldBright)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = motif.name.take(9),
                                            fontFamily = CinzelFont,
                                            fontSize = 8.sp,
                                            color = ParchmentDark,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Seal Inscription & Material Configuration
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ChamberSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder))),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "SEAL INSCRIPTION & MATERIAL:",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = AntiqueGold
                            )

                            // Seal Name Field
                            OutlinedTextField(
                                value = sigilName,
                                onValueChange = { sigilName = it },
                                label = { Text("Seal Designation", fontFamily = CinzelFont, color = AntiqueGold) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AntiqueGold,
                                    unfocusedBorderColor = ChamberBorder,
                                    focusedTextColor = ParchmentLight,
                                    unfocusedTextColor = ParchmentLight
                                )
                            )

                            // Latin Motto Field
                            OutlinedTextField(
                                value = sigilMotto,
                                onValueChange = { sigilMotto = it },
                                label = { Text("Border Motto / Word of Power", fontFamily = CinzelFont, color = AntiqueGold) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AntiqueGold,
                                    unfocusedBorderColor = ChamberBorder,
                                    focusedTextColor = ParchmentLight,
                                    unfocusedTextColor = ParchmentLight
                                )
                            )

                            // Geometry Matrix Selector
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "GEOMETRIC BACKDROP MATRIX:",
                                    fontFamily = CinzelFont,
                                    fontSize = 10.sp,
                                    color = CitrinitasAmber
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(SealBaseGeometry.values()) { geom ->
                                        val isSel = geom == selectedGeometry
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSel) AntiqueGold else ChamberDark)
                                                .border(1.dp, if (isSel) GoldBright else ChamberBorder, RoundedCornerShape(6.dp))
                                                .clickable {
                                                    audioEngine.playCipherClick()
                                                    selectedGeometry = geom
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(geom.glyph, fontSize = 12.sp, color = if (isSel) NigredoBlack else GoldBright)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    geom.title,
                                                    fontFamily = CinzelFont,
                                                    fontSize = 9.sp,
                                                    color = if (isSel) NigredoBlack else ParchmentLight
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Tincture Selector
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "SEAL TINCTURE & MATERIAL:",
                                    fontFamily = CinzelFont,
                                    fontSize = 10.sp,
                                    color = CitrinitasAmber
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    SealTincture.values().forEach { tinc ->
                                        val isSel = tinc == selectedTincture
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSel) AntiqueGold else ChamberDark)
                                                .border(1.dp, if (isSel) GoldBright else ChamberBorder, RoundedCornerShape(6.dp))
                                                .clickable {
                                                    audioEngine.playCipherClick()
                                                    selectedTincture = tinc
                                                }
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .clip(CircleShape)
                                                        .background(tinc.primaryColor)
                                                        .border(0.5.dp, Color.White, CircleShape)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = tinc.title.split(" ").first(),
                                                    fontFamily = CinzelFont,
                                                    fontSize = 8.5.sp,
                                                    color = if (isSel) NigredoBlack else ParchmentLight
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Consecration Action Bar
                item {
                    Button(
                        onClick = {
                            if (placedGlyphs.isEmpty()) return@Button
                            audioEngine.playWaxSeal()
                            audioEngine.playTransmutationHarmonic()

                            val newSigil = PersonalSigil(
                                id = "sigil_${System.currentTimeMillis()}",
                                name = sigilName.ifBlank { "Personal Sigil" },
                                motto = sigilMotto.ifBlank { "Solve et Coagula" },
                                geometry = selectedGeometry,
                                tincture = selectedTincture,
                                glyphs = placedGlyphs.map { it.copy() },
                                createdTimestamp = System.currentTimeMillis()
                            )
                            repository.savePersonalSigil(newSigil)
                            consecrationSuccess = true
                        },
                        enabled = placedGlyphs.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(8.dp, RoundedCornerShape(10.dp), spotColor = AntiqueGold),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WaxSealRed,
                            contentColor = ParchmentLight
                        ),
                        border = BorderStroke(1.5.dp, GoldBright),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✦", fontSize = 16.sp, color = GoldBright)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONSECRATE & INSCRIBE SEAL (+100 XP)",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("✦", fontSize = 16.sp, color = GoldBright)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Consecration Success Dialog
        if (consecrationSuccess) {
            AlertDialog(
                onDismissRequest = { consecrationSuccess = false },
                containerColor = ChamberDark,
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🜚", fontSize = 28.sp, color = GoldBright)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SEAL CONSECRATED",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.5.sp,
                            color = AntiqueGold
                        )
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "“$sigilName”",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ParchmentLight
                        )
                        Text(
                            text = "“$sigilMotto”",
                            fontFamily = GaramondFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = CitrinitasAmber
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Thy personal talisman has been permanently inscribed into thy Grimoire. +100 Aether Experience points awarded.",
                            fontFamily = GaramondFont,
                            fontSize = 13.sp,
                            color = ParchmentBase,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { consecrationSuccess = false },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                    ) {
                        Text("Benediction ✦ Close", fontFamily = CinzelFont, color = NigredoBlack)
                    }
                }
            )
        }
    }
}

/**
 * Interactive Circular Seal Canvas supporting drag detection on individual glyphs,
 * rendering concentric Renaissance geometry, and showing real-time positioning.
 */
@Composable
fun InteractiveSigilCanvas(
    geometry: SealBaseGeometry,
    tincture: SealTincture,
    motto: String,
    glyphs: List<PlacedGlyph>,
    selectedGlyphId: String?,
    onSelectGlyph: (String) -> Unit,
    onMoveGlyph: (String, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    var canvasSize by remember { mutableStateOf(Size(600f, 600f)) }

    Box(
        modifier = modifier
            .size(260.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        tincture.primaryColor.copy(alpha = 0.22f),
                        ChamberDark,
                        NigredoBlack
                    )
                )
            )
            .border(2.5.dp, tincture.primaryColor, CircleShape)
            .pointerInput(glyphs) {
                detectTapGestures { tapOffset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val r = size.width / 2f

                    // Find closest glyph
                    var closestId: String? = null
                    var minDist = Float.MAX_VALUE

                    for (g in glyphs) {
                        val gx = center.x + g.normX * (r * 0.72f)
                        val gy = center.y + g.normY * (r * 0.72f)
                        val dist = sqrt((tapOffset.x - gx).pow(2) + (tapOffset.y - gy).pow(2))
                        if (dist < 40.dp.toPx() && dist < minDist) {
                            minDist = dist
                            closestId = g.id
                        }
                    }
                    if (closestId != null) {
                        onSelectGlyph(closestId)
                    }
                }
            }
            .pointerInput(selectedGlyphId, glyphs) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val targetId = selectedGlyphId ?: return@detectDragGestures
                    val glyph = glyphs.find { it.id == targetId } ?: return@detectDragGestures

                    val r = size.width / 2f * 0.72f
                    val newNormX = (glyph.normX + dragAmount.x / r).coerceIn(-0.88f, 0.88f)
                    val newNormY = (glyph.normY + dragAmount.y / r).coerceIn(-0.88f, 0.88f)

                    // Ensure within circle
                    val distFromCenter = sqrt(newNormX * newNormX + newNormY * newNormY)
                    if (distFromCenter <= 0.88f) {
                        onMoveGlyph(targetId, newNormX, newNormY)
                    } else {
                        val angle = atan2(newNormY, newNormX)
                        val clampedX = cos(angle) * 0.88f
                        val clampedY = sin(angle) * 0.88f
                        onMoveGlyph(targetId, clampedX, clampedY)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            canvasSize = size
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f - 4f
            val innerRadius = outerRadius * 0.76f

            // 1. Draw Sacred Geometry Matrix Backdrop
            drawGeometryMatrix(
                geometry = geometry,
                center = center,
                outerR = outerRadius,
                innerR = innerRadius,
                primaryColor = tincture.primaryColor,
                accentColor = tincture.accentColor
            )

            // 2. Draw Motto Text on Ring Border
            val mottoClean = motto.ifBlank { "SOLVE ET COAGULA" }.uppercase()
            val textLayout = textMeasurer.measure(
                text = mottoClean,
                style = TextStyle(
                    fontFamily = CinzelFont,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = tincture.primaryColor
                )
            )
            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(center.x - textLayout.size.width / 2f, 8.dp.toPx())
            )

            // 3. Draw All Placed Glyphs
            for (g in glyphs) {
                val isSelected = g.id == selectedGlyphId
                val gx = center.x + g.normX * (innerRadius * 0.95f)
                val gy = center.y + g.normY * (innerRadius * 0.95f)

                // Selection Halo
                if (isSelected) {
                    drawCircle(
                        color = GoldBright.copy(alpha = 0.35f),
                        radius = 22.dp.toPx() * g.scale,
                        center = Offset(gx, gy),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                // Render Glyph Symbol
                val glyphLayout = textMeasurer.measure(
                    text = g.glyph,
                    style = TextStyle(
                        fontSize = (22 * g.scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = g.color
                    )
                )

                rotate(
                    degrees = g.rotationDeg,
                    pivot = Offset(gx, gy)
                ) {
                    drawText(
                        textLayoutResult = glyphLayout,
                        topLeft = Offset(gx - glyphLayout.size.width / 2f, gy - glyphLayout.size.height / 2f)
                    )
                }
            }
        }
    }
}

/**
 * Draws the selected Renaissance geometric talisman backdrop on canvas.
 */
private fun DrawScope.drawGeometryMatrix(
    geometry: SealBaseGeometry,
    center: Offset,
    outerR: Float,
    innerR: Float,
    primaryColor: Color,
    accentColor: Color
) {
    // Outer concentric borders
    drawCircle(color = primaryColor, radius = outerR, center = center, style = Stroke(1.5.dp.toPx()))
    drawCircle(color = accentColor.copy(alpha = 0.6f), radius = innerR, center = center, style = Stroke(1.dp.toPx()))

    // 12 Astrolabe Divisions in the outer margin
    for (i in 0 until 12) {
        val a = (i * 30f) * (PI / 180f)
        val p1 = Offset(center.x + innerR * cos(a).toFloat(), center.y + innerR * sin(a).toFloat())
        val p2 = Offset(center.x + outerR * cos(a).toFloat(), center.y + outerR * sin(a).toFloat())
        drawLine(color = accentColor.copy(alpha = 0.45f), start = p1, end = p2, strokeWidth = 1f)
    }

    when (geometry) {
        SealBaseGeometry.SOLOMON_HEXAGRAM -> {
            // Hexagram: Fire △ & Water ▽
            val ptsUp = List(3) { idx ->
                val a = (idx * 120f - 90f) * (PI / 180f)
                Offset(center.x + innerR * cos(a).toFloat(), center.y + innerR * sin(a).toFloat())
            }
            val ptsDown = List(3) { idx ->
                val a = (idx * 120f + 90f) * (PI / 180f)
                Offset(center.x + innerR * cos(a).toFloat(), center.y + innerR * sin(a).toFloat())
            }

            drawLine(primaryColor.copy(alpha = 0.55f), ptsUp[0], ptsUp[1], strokeWidth = 1.2f)
            drawLine(primaryColor.copy(alpha = 0.55f), ptsUp[1], ptsUp[2], strokeWidth = 1.2f)
            drawLine(primaryColor.copy(alpha = 0.55f), ptsUp[2], ptsUp[0], strokeWidth = 1.2f)

            drawLine(accentColor.copy(alpha = 0.55f), ptsDown[0], ptsDown[1], strokeWidth = 1.2f)
            drawLine(accentColor.copy(alpha = 0.55f), ptsDown[1], ptsDown[2], strokeWidth = 1.2f)
            drawLine(accentColor.copy(alpha = 0.55f), ptsDown[2], ptsDown[0], strokeWidth = 1.2f)
        }

        SealBaseGeometry.OUROBOROS_SERPENT -> {
            drawCircle(color = primaryColor.copy(alpha = 0.35f), radius = innerR * 0.85f, center = center, style = Stroke(2.5.dp.toPx()))
            // Serpent star scale ticks
            for (i in 0 until 24) {
                val a = (i * 15f) * (PI / 180f)
                val p = Offset(center.x + (innerR * 0.85f) * cos(a).toFloat(), center.y + (innerR * 0.85f) * sin(a).toFloat())
                drawCircle(color = accentColor, radius = 1.2.dp.toPx(), center = p)
            }
        }

        SealBaseGeometry.SEVEN_PLANETS -> {
            // 7 concentric planetary spheres
            for (step in 1..7) {
                val r = innerR * (step / 7f)
                drawCircle(color = primaryColor.copy(alpha = 0.22f + step * 0.05f), radius = r, center = center, style = Stroke(0.8.dp.toPx()))
            }
        }

        SealBaseGeometry.HERMETIC_OCTAGRAM -> {
            // 8-fold star of Hermes
            for (i in 0 until 8) {
                val a1 = (i * 45f) * (PI / 180f)
                val a2 = ((i + 3) * 45f) * (PI / 180f)
                val p1 = Offset(center.x + innerR * cos(a1).toFloat(), center.y + innerR * sin(a1).toFloat())
                val p2 = Offset(center.x + innerR * cos(a2).toFloat(), center.y + innerR * sin(a2).toFloat())
                drawLine(color = primaryColor.copy(alpha = 0.45f), start = p1, end = p2, strokeWidth = 1.2f)
            }
        }

        SealBaseGeometry.ELEMENTAL_QUADRATE -> {
            // Square and Elemental Cross
            val half = innerR * 0.707f
            drawRect(
                color = primaryColor.copy(alpha = 0.4f),
                topLeft = Offset(center.x - half, center.y - half),
                size = Size(half * 2f, half * 2f),
                style = Stroke(1.2f)
            )
            drawLine(accentColor.copy(alpha = 0.5f), Offset(center.x - innerR, center.y), Offset(center.x + innerR, center.y), strokeWidth = 1f)
            drawLine(accentColor.copy(alpha = 0.5f), Offset(center.x, center.y - innerR), Offset(center.x, center.y + innerR), strokeWidth = 1f)
        }

        SealBaseGeometry.MINIMAL_WAX -> {
            drawCircle(color = primaryColor.copy(alpha = 0.45f), radius = innerR * 0.5f, center = center, style = Stroke(1.dp.toPx()))
        }
    }
}

/**
 * Inspector Card allowing ADEPT to scale, rotate, tint, center, duplicate, or delete
 * the actively selected glyph on the canvas.
 */
@Composable
private fun GlyphInspectorCard(
    glyph: PlacedGlyph,
    onUpdate: (PlacedGlyph) -> Unit,
    onRemove: () -> Unit,
    onDuplicate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ChamberSurfaceLight),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(glyph.glyph, fontSize = 24.sp, color = glyph.color)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SELECTED: ${glyph.name.uppercase()}",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = AntiqueGold
                        )
                        Text(
                            text = "Pos: (${(glyph.normX * 100).toInt()}%, ${(glyph.normY * 100).toInt()}%) • Scale: ${(glyph.scale * 100).toInt()}% • Rot: ${glyph.rotationDeg.toInt()}°",
                            fontFamily = GaramondFont,
                            fontSize = 10.sp,
                            color = ParchmentDark
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                        Text("⧉", fontSize = 16.sp, color = GoldBright)
                    }
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WaxSealRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            HorizontalDivider(color = ChamberBorder, thickness = 0.8.dp)

            // Adjustments: Scale, Rotate, Center
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Scale Controls
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Scale:", fontFamily = CinzelFont, fontSize = 9.sp, color = CitrinitasAmber)
                    Button(
                        onClick = { onUpdate(glyph.copy(scale = (glyph.scale - 0.15f).coerceAtLeast(0.6f))) },
                        colors = ButtonDefaults.buttonColors(containerColor = ChamberDark),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) { Text("-", fontSize = 12.sp, color = AntiqueGold) }

                    Button(
                        onClick = { onUpdate(glyph.copy(scale = (glyph.scale + 0.15f).coerceAtMost(2.5f))) },
                        colors = ButtonDefaults.buttonColors(containerColor = ChamberDark),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) { Text("+", fontSize = 12.sp, color = AntiqueGold) }
                }

                // Rotate Controls
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Rotate:", fontFamily = CinzelFont, fontSize = 9.sp, color = CitrinitasAmber)
                    Button(
                        onClick = { onUpdate(glyph.copy(rotationDeg = (glyph.rotationDeg - 30f + 360f) % 360f)) },
                        colors = ButtonDefaults.buttonColors(containerColor = ChamberDark),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) { Text("⟲", fontSize = 12.sp, color = AntiqueGold) }

                    Button(
                        onClick = { onUpdate(glyph.copy(rotationDeg = (glyph.rotationDeg + 30f) % 360f)) },
                        colors = ButtonDefaults.buttonColors(containerColor = ChamberDark),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) { Text("⟳", fontSize = 12.sp, color = AntiqueGold) }
                }

                // Snap Center Button
                Button(
                    onClick = { onUpdate(glyph.copy(normX = 0f, normY = 0f)) },
                    colors = ButtonDefaults.buttonColors(containerColor = ChamberDark),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) { Text("Center", fontFamily = CinzelFont, fontSize = 9.sp, color = AntiqueGold) }
            }

            // Tint Color Palette for Selected Glyph
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tint:", fontFamily = CinzelFont, fontSize = 9.sp, color = CitrinitasAmber)
                val colors = listOf(
                    GoldBright,
                    AntiqueGold,
                    Color(0xFFE2E8F0), // Quicksilver Silver
                    WaxSealRed,
                    RubedoCrimson,
                    EmeraldHermetic,
                    CitrinitasAmber,
                    IronGallInk
                )
                colors.forEach { c ->
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(if (glyph.color == c) 1.5.dp else 0.5.dp, Color.White, CircleShape)
                            .clickable { onUpdate(glyph.copy(color = c)) }
                    )
                }
            }
        }
    }
}

/**
 * Vault section displaying previously consecrated sigils.
 */
@Composable
private fun SavedSigilsVaultSection(
    sigils: List<PersonalSigil>,
    onLoadSigil: (PersonalSigil) -> Unit,
    onDeleteSigil: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ChamberDark),
        border = BorderStroke(1.dp, AntiqueGold),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "CONSECRATED SIGILS VAULT (${sigils.size})",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = AntiqueGold
            )

            if (sigils.isEmpty()) {
                Text(
                    text = "No sigils forged yet. Consecrate thy first talisman in the forge.",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = ParchmentDark
                )
            } else {
                sigils.forEach { sigil ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ChamberSurface)
                            .border(1.dp, ChamberBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = sigil.glyphs.firstOrNull()?.glyph ?: "🜚",
                                fontSize = 24.sp,
                                color = sigil.tincture.primaryColor
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = sigil.name,
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = AntiqueGold
                                )
                                Text(
                                    text = "“${sigil.motto}” • ${sigil.geometry.title}",
                                    fontFamily = GaramondFont,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 10.5.sp,
                                    color = ParchmentLight
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { onLoadSigil(sigil) },
                                colors = ButtonDefaults.buttonColors(containerColor = ChamberDark),
                                border = BorderStroke(1.dp, AntiqueGold),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Load", fontFamily = CinzelFont, fontSize = 9.sp, color = AntiqueGold)
                            }

                            IconButton(
                                onClick = { onDeleteSigil(sigil.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WaxSealRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
