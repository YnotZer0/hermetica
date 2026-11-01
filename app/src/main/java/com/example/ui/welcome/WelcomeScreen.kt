package com.example.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ParchmentScroll
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.*

/**
 * Introductory first page: explains what Hermetica is, how to use each section,
 * and what the user can gain from it. Shown when the app opens, before the main tabs.
 */
@Composable
fun WelcomeScreen(
    onEnter: (dontShowAgain: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var dontShowAgain by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireCover)
            .testTag("welcome_screen")
            // Absorb taps on empty areas so they don't reach the app screens underneath
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title block
            WaxSealBadge(glyph = "🜚", size = 64.dp)
            Text(
                text = "HERMETICA",
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 3.sp,
                color = AntiqueGold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "A pocket grimoire of Hermetic philosophy, alchemy and reflective practice",
                fontFamily = GaramondFont,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = ParchmentDark,
                textAlign = TextAlign.Center
            )

            // What the app is
            ParchmentScroll(
                modifier = Modifier.fillMaxWidth(),
                title = "What is Hermetica?",
                latinMotto = "Quid est"
            ) {
                BodyText(
                    "Hermetica is a study companion for the Western esoteric tradition: The Kybalion and its " +
                        "Seven Hermetic Principles, the Emerald Tablet of Hermes Trismegistus, the Corpus Hermeticum, " +
                        "Paracelsus, Agrippa, and Carl Jung's reading of alchemy as a psychology of inner change."
                )
                Spacer(modifier = Modifier.height(8.dp))
                BodyText(
                    "Rather than a dry textbook, the teachings are presented as a 17th-century alchemist's " +
                        "study, with scrolls to unroll, ciphers to solve, experiments to run and a personal " +
                        "journal to keep."
                )
            }

            // How to use it
            ParchmentScroll(
                modifier = Modifier.fillMaxWidth(),
                title = "How to use it",
                latinMotto = "Modus operandi"
            ) {
                GuideItem("☉", "Oracle",
                    "Choose a source text (Kybalion, Emerald Tablet, Corpus Hermeticum and more) to receive a " +
                        "daily teaching with its meaning, a psychological reading and a short exercise. " +
                        "You can also ask the Oracle your own question, start a timed contemplation " +
                        "session, or write in your Hermetic Journal.")
                GuideItem("🜎", "Cipher",
                    "Solve a daily cryptogram drawn from the Kybalion. Each solved cipher unlocks a deeper " +
                        "knowledge node.")
                GuideItem("🜚", "Laws",
                    "Study the Seven Hermetic Principles one at a time on the celestial wheel, with practical " +
                        "ways to apply each in daily life.")
                GuideItem("🜔", "Crucible",
                    "Combine alchemical elements in the virtual laboratory to discover the classic formulas of " +
                        "the Great Work and awaken relics.")
                GuideItem("🜫", "Archive",
                    "Read summaries and excerpts of the key Hermetic and alchemical treatises, searchable by " +
                        "title, author or subject.")
                GuideItem("☿", "Grimoire",
                    "Your cabinet of relics and sigil forge, with the folios of your Hermetic Journal. " +
                        "Everything you write or save is kept here, from your own reflections to Oracle " +
                        "answers, deciphered axioms and sigils.",
                    isLast = true)
                Spacer(modifier = Modifier.height(8.dp))
                BodyText(
                    "Studying, solving and journaling earn experience points, which raise your rank from " +
                        "Neophyte towards Magus and track your progress."
                )
            }

            // Benefits
            ParchmentScroll(
                modifier = Modifier.fillMaxWidth(),
                title = "What you can gain",
                latinMotto = "Fructus operis"
            ) {
                BenefitItem("A clear grounding in Hermetic philosophy and its key texts, learned in small daily steps.")
                BenefitItem("A daily moment of reflection: each teaching ends with a short contemplation exercise, and the Contemplation Timer gives you 5 to 30 minutes of focused stillness.")
                BenefitItem("Practical tools for perspective on everyday life, such as using the Principles of Polarity and Rhythm to reframe moods and setbacks.")
                BenefitItem("An introduction to Jung's view of alchemy as a map of personal growth and shadow work.")
                BenefitItem("A growing personal journal, saved on your device, with streaks and progress to show how your practice develops.")
                BenefitItem("A habit of curiosity, kept up with ciphers, experiments and progress ranks.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hermetica is for learning and reflection. It is not a substitute for medical, " +
                        "psychological or other professional advice.",
                    fontFamily = GaramondFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = FadedInk
                )
            }

            // "Don't show again" option; the whole row is tappable, not just the box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .toggleable(
                        value = dontShowAgain,
                        role = Role.Checkbox,
                        onValueChange = { dontShowAgain = it }
                    )
                    .padding(vertical = 4.dp)
                    .testTag("welcome_dont_show_again"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = dontShowAgain,
                    onCheckedChange = null, // handled by the row's toggleable
                    colors = CheckboxDefaults.colors(
                        checkedColor = AntiqueGold,
                        uncheckedColor = ParchmentDark,
                        checkmarkColor = NigredoBlack
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Don't show this page again",
                    fontFamily = GaramondFont,
                    fontSize = 16.sp,
                    color = ParchmentLight,
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = { onEnter(dontShowAgain) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("welcome_enter_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AntiqueGold,
                    contentColor = NigredoBlack
                )
            ) {
                Text(
                    text = "ENTER THE SANCTUM",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        fontFamily = GaramondFont,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = IronGallInk,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun GuideItem(glyph: String, name: String, description: String, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        WaxSealBadge(glyph = glyph, size = 34.dp)
        Spacer(modifier = Modifier.width(12.dp))
        // weight(1f) keeps the text inside the card width so it wraps rather than overflowing
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name.uppercase(),
                fontFamily = CinzelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                color = WaxSealRed
            )
            Text(
                text = description,
                fontFamily = GaramondFont,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                color = IronGallInk
            )
        }
    }
}

@Composable
private fun BenefitItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "✦",
            fontSize = 13.sp,
            color = GoldBurnished,
            modifier = Modifier.padding(top = 2.dp, end = 10.dp)
        )
        Text(
            text = text,
            fontFamily = GaramondFont,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            color = IronGallInk,
            modifier = Modifier.weight(1f)
        )
    }
}
