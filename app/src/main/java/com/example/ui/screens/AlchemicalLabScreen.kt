package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AlchemicalElement
import com.example.data.model.MagnumOpusStage
import com.example.data.repository.HermeticData
import com.example.data.repository.HermeticRepository
import com.example.data.repository.TransmutationResult
import com.example.ui.components.AlchemicalApparatusCanvas
import com.example.ui.components.ParchmentScroll
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*

@Composable
fun AlchemicalLabScreen(
    repository: HermeticRepository,
    modifier: Modifier = Modifier
) {
    val adeptProfile by repository.adeptProfile.collectAsState()
    var selectedElements by remember { mutableStateOf<List<AlchemicalElement>>(emptyList()) }
    var heatLevel by remember { mutableFloatStateOf(0.7f) }
    var isHeating by remember { mutableStateOf(true) }
    var transmutationResult by remember { mutableStateOf<TransmutationResult?>(null) }
    var currentLiquidColor by remember { mutableStateOf(EmeraldHermetic) }

    Box(modifier = modifier.fillMaxSize().background(GrimoireCover)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero 17th Century Alchemical Laboratory Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("lab_banner_card"),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AntiqueGold, ChamberBorder)))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_alchemical_lab),
                            contentDescription = "17th Century Alchemical Laboratory",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, ChamberDark.copy(alpha = 0.85f), ChamberDark)
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "LABORATORIUM SPAGYRICUM",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 1.5.sp,
                                color = AntiqueGold
                            )
                            Text(
                                text = "“Solve et Coagula” — Dissolve the gross, coagulate the subtle",
                                fontFamily = GaramondFont,
                                fontStyle = FontStyle.Italic,
                                fontSize = 13.sp,
                                color = ParchmentLight
                            )
                        }
                    }
                }
            }

            // Interactive Alchemical Apparatus Canvas (Cauldron & Alembic)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("apparatus_card"),
                    colors = CardDefaults.cardColors(containerColor = ChamberDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ChamberBorder, AntiqueGold))),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ATHANOR & DISTILLATION ALEMBIC",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                color = CitrinitasAmber
                            )
                            Text(
                                text = "Transmutations: ${adeptProfile.transmutationsPerformed}",
                                fontFamily = CinzelFont,
                                fontSize = 11.sp,
                                color = ParchmentDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Apparatus Visual
                        AlchemicalApparatusCanvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .testTag("apparatus_canvas"),
                            liquidColor = currentLiquidColor,
                            isHeating = isHeating,
                            heatLevel = heatLevel
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Athanor Heat Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = AthanorEmber, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Athanor Fire Heat:", fontFamily = CinzelFont, fontSize = 11.sp, color = ParchmentLight)
                            }
                            Slider(
                                value = heatLevel,
                                onValueChange = {
                                    heatLevel = it
                                    isHeating = it > 0.1f
                                },
                                modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldBright,
                                    activeTrackColor = AthanorEmber,
                                    inactiveTrackColor = ChamberBorder
                                )
                            )
                            Button(
                                onClick = {
                                    heatLevel = (heatLevel + 0.15f).coerceAtMost(1.0f)
                                    isHeating = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ChamberSurfaceLight,
                                    contentColor = GoldBright
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Stoke", fontFamily = CinzelFont, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // Crucible Ingredient Chamber
            item {
                ParchmentScroll(
                    modifier = Modifier.testTag("crucible_scroll"),
                    title = "The Crucible Vessel",
                    latinMotto = "Vessel of Transformation",
                    sealGlyph = "🜔"
                ) {
                    Text(
                        text = "Select 2 or 3 alchemical principles into the vessel to catalyze transmutation:",
                        fontFamily = GaramondFont,
                        fontSize = 14.sp,
                        color = FadedInk
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selected Ingredients Slot Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (i in 0 until 3) {
                            val el = selectedElements.getOrNull(i)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (el != null) ParchmentLight else ParchmentDark.copy(alpha = 0.4f))
                                    .border(1.2.dp, if (el != null) AntiqueGold else FadedInk.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (el != null) {
                                            selectedElements = selectedElements.filterIndexed { index, _ -> index != i }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (el != null) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = el.symbol, fontSize = 20.sp, color = IronGallInk)
                                        Text(text = el.name, fontFamily = CinzelFont, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = IronGallInk)
                                    }
                                } else {
                                    Text(
                                        text = "+ Slot ${i + 1}",
                                        fontFamily = CinzelFont,
                                        fontSize = 11.sp,
                                        color = FadedInk.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Element Grid
                    Text(
                        text = "AVAILABLE SPAGYRIC ELEMENTS:",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = IronGallInk
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Element Chips Flow
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HermeticData.elements.chunked(3).forEach { rowElements ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowElements.forEach { element ->
                                    val isChosen = selectedElements.any { it.id == element.id }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isChosen) WaxSealRed else ParchmentLight)
                                            .border(1.dp, if (isChosen) GoldBright else ChamberBorder, RoundedCornerShape(8.dp))
                                            .clickable {
                                                if (isChosen) {
                                                    selectedElements = selectedElements.filter { it.id != element.id }
                                                } else if (selectedElements.size < 3) {
                                                    selectedElements = selectedElements + element
                                                }
                                            }
                                            .padding(vertical = 8.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = element.symbol,
                                                fontSize = 18.sp,
                                                color = if (isChosen) ParchmentLight else IronGallInk
                                            )
                                            Text(
                                                text = element.name,
                                                fontFamily = CinzelFont,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChosen) ParchmentLight else IronGallInk,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Transmute Action Button
                    Button(
                        onClick = {
                            val ids = selectedElements.map { it.id }
                            val result = repository.performTransmutation(ids)
                            transmutationResult = result

                            com.example.audio.AlchemicalAudioEngine.getInstance().playTransmutationHarmonic()
                            com.example.audio.AlchemicalAudioEngine.getInstance().playBubblingCauldron()

                            if (result is TransmutationResult.Success) {
                                currentLiquidColor = when (result.formula.stage) {
                                    MagnumOpusStage.NIGREDO -> NigredoBlack
                                    MagnumOpusStage.ALBEDO -> AlbedoWhite
                                    MagnumOpusStage.CITRINITAS -> CitrinitasAmber
                                    MagnumOpusStage.RUBEDO -> RubedoCrimson
                                }
                            }
                        },
                        enabled = selectedElements.size >= 2,
                        modifier = Modifier.fillMaxWidth().testTag("transmute_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WaxSealRed,
                            contentColor = ParchmentLight
                        )
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "IGNITE TRANSMUTATION (SOLVE ET COAGULA)",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            // Transmutation Outcome Revelation Card
            transmutationResult?.let { result ->
                item {
                    when (result) {
                        is TransmutationResult.Success -> {
                            val formula = result.formula
                            ParchmentScroll(
                                modifier = Modifier.testTag("transmutation_success_card"),
                                title = "Transmutation Achieved!",
                                latinMotto = formula.stage.latinName,
                                sealGlyph = formula.outputSymbol
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (formula.stage) {
                                                MagnumOpusStage.NIGREDO -> NigredoBlack.copy(alpha = 0.8f)
                                                MagnumOpusStage.ALBEDO -> AlbedoWhite.copy(alpha = 0.9f)
                                                MagnumOpusStage.CITRINITAS -> CitrinitasAmber.copy(alpha = 0.2f)
                                                MagnumOpusStage.RUBEDO -> RubedoCrimson.copy(alpha = 0.2f)
                                            }
                                        )
                                        .border(1.5.dp, AntiqueGold, RoundedCornerShape(8.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "SUBSTANCE: ${formula.outputName.uppercase()}",
                                            fontFamily = CinzelFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (formula.stage == MagnumOpusStage.NIGREDO) AntiqueGold else IronGallInk
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Stage of the Great Work: ${formula.stage.stageName} (${formula.stage.meaning})",
                                            fontFamily = GaramondFont,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 14.sp,
                                            color = if (formula.stage == MagnumOpusStage.NIGREDO) ParchmentLight else FadedInk
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "“${formula.crypticRevelation}”",
                                            fontFamily = GaramondFont,
                                            fontSize = 15.sp,
                                            lineHeight = 22.sp,
                                            color = if (formula.stage == MagnumOpusStage.NIGREDO) ParchmentLight else IronGallInk
                                        )

                                        if (formula.unlockedArtifactId != null) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = "✦ A Relic has awakened in thy Cabinet! (+150 XP)",
                                                fontFamily = CinzelFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = AntiqueGold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is TransmutationResult.Partial -> {
                            ParchmentScroll(
                                modifier = Modifier.testTag("transmutation_partial_card"),
                                title = "Residue in Crucible",
                                latinMotto = "Incompleta",
                                sealGlyph = "🜀"
                            ) {
                                Text(
                                    text = result.substanceName,
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = IronGallInk
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = result.hint,
                                    fontFamily = GaramondFont,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 14.sp,
                                    color = FadedInk
                                )
                            }
                        }
                    }
                }
            }

            // Codex of Magnum Opus Formulas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ChamberSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ChamberBorder, AntiqueGold))),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            text = "MAGNUM OPUS CODEX",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AntiqueGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Formulas recorded by 17th-century adepts:",
                            fontFamily = GaramondFont,
                            fontSize = 13.sp,
                            color = ParchmentDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        HermeticData.transmutationRecipes.forEach { formula ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${formula.outputSymbol}  ${formula.name}",
                                    fontFamily = GaramondFont,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = ParchmentLight
                                )
                                Text(
                                    text = formula.stage.stageName,
                                    fontFamily = CinzelFont,
                                    fontSize = 11.sp,
                                    color = CitrinitasAmber
                                )
                            }
                            HorizontalDivider(color = ChamberBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
