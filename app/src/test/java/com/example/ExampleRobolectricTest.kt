package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.AlchemicalAudioEngine
import com.example.data.model.AlchemicalElement
import com.example.data.repository.HermeticData
import com.example.data.journal.JournalEntry
import com.example.data.repository.HermeticRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  /**
   * Inscriptions are saved to the Room journal asynchronously, so wait (up to 5 s) for an
   * entry matching [predicate] to appear in the database.
   */
  private fun awaitJournalEntry(repo: HermeticRepository, predicate: (JournalEntry) -> Boolean): Boolean =
    runBlocking {
      withTimeout(5_000) {
        repo.journalReflections.first { list -> list.any(predicate) }
      }.any(predicate)
    }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Hermetica", appName)
  }

  @Test
  fun `verify seven hermetic principles present`() {
    val principles = HermeticData.principles
    assertEquals(7, principles.size)
    assertEquals("The Principle of Mentalism", principles[0].name)
    assertEquals("The Principle of Correspondence", principles[1].name)
    assertEquals("The Principle of Vibration", principles[2].name)
    assertEquals("The Principle of Polarity", principles[3].name)
    assertEquals("The Principle of Rhythm", principles[4].name)
    assertEquals("The Principle of Cause and Effect", principles[5].name)
    assertEquals("The Principle of Gender", principles[6].name)
  }

  @Test
  fun `verify repository progression and unlock logic`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = HermeticRepository(context)

    val initialExp = repo.adeptProfile.value.experiencePoints
    repo.markPrincipleStudied(2)
    assertTrue(repo.adeptProfile.value.experiencePoints > initialExp)
    assertTrue(repo.adeptProfile.value.principlesMastered.contains(2))

    // Test element transmutation
    val result = repo.performTransmutation(listOf("sulfur", "mercury", "salt"))
    assertNotNull(result)
    assertTrue(repo.adeptProfile.value.transmutationsPerformed > 0)

    // Test journal entry addition
    repo.addJournalEntry(
      title = "The Kybalion Study",
      category = "Philosophy",
      content = "The lips of wisdom are closed, except to the ears of Understanding.",
      sealGlyph = "🜚"
    )
    assertTrue(awaitJournalEntry(repo) { it.title == "The Kybalion Study" })
  }

  @Test
  fun `verify audio engine initializes cleanly`() {
    val audioEngine = AlchemicalAudioEngine.getInstance()
    assertNotNull(audioEngine)
    // Verify sounds can be triggered without exception in test env
    audioEngine.playBubblingCauldron()
    audioEngine.playBellChime()
    audioEngine.playWaxSeal()
    audioEngine.playTransmutationHarmonic()
    audioEngine.playCipherClick()
    audioEngine.playInkSpread()
  }

  @Test
  fun `verify hermetic cipher puzzles and knowledge nodes`() {
    val puzzles = HermeticData.cipherPuzzles
    val nodes = HermeticData.knowledgeNodes

    assertEquals(7, puzzles.size)
    assertEquals(7, nodes.size)

    assertEquals("The Cipher of the All-Mind", puzzles[0].title)
    assertEquals("node_mentalism", puzzles[0].associatedNodeId)
    assertEquals("THE ALL IS MIND THE UNIVERSE IS MENTAL", puzzles[0].solutionText)
  }

  @Test
  fun `verify cipher solving unlocks knowledge node and awards xp`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = HermeticRepository(context)

    val initialExp = repo.adeptProfile.value.experiencePoints
    val puzzleId = "cipher_mentalism"

    val isSolved = repo.solveCipher(puzzleId)
    assertTrue(isSolved)

    // Check profile updated
    assertTrue(repo.adeptProfile.value.solvedCiphers.contains(puzzleId))
    assertTrue(repo.adeptProfile.value.experiencePoints > initialExp)

    // Check associated knowledge node is unlocked
    val mentalismNode = repo.knowledgeNodes.value.find { it.id == "node_mentalism" }
    assertNotNull(mentalismNode)
    assertTrue(mentalismNode!!.isUnlocked)

    // Check the journal (shown in the Grimoire) has the inscription
    assertTrue(awaitJournalEntry(repo) { it.title.contains("The Cipher of the All-Mind") })
  }

  @Test
  fun `verify alchemical touch particle system simulation`() {
    val system = com.example.ui.particles.AlchemicalParticleSystem(maxParticles = 50)
    assertEquals(0, system.particles.size)
    assertFalse(system.isSimulating)

    // Spawn touch particles
    system.spawn(200f, 300f, count = 4)
    assertEquals(4, system.particles.size)
    assertTrue(system.isSimulating)

    // Simulate forward in time
    val now = System.currentTimeMillis()
    system.update(now + 100, 0.1f)
    assertEquals(4, system.particles.size)

    // Fast-forward past max life
    system.update(now + 2000, 1.9f)
    assertEquals(0, system.particles.size)
    assertFalse(system.isSimulating)
  }

  @Test
  fun `verify personal sigil forge creation and consecration`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = HermeticRepository(context)

    val initialExp = repo.adeptProfile.value.experiencePoints
    val initialSigilsCount = repo.personalSigils.value.size

    val testSigil = com.example.data.model.PersonalSigil(
        id = "test_sigil_1",
        name = "Amulet of Paracelsus",
        motto = "In Sole et Luna",
        geometry = com.example.data.model.SealBaseGeometry.OUROBOROS_SERPENT,
        tincture = com.example.data.model.SealTincture.ALCHEMICAL_GOLD,
        glyphs = listOf(
            com.example.data.model.PlacedGlyph("g1", "☉", "Sol", 0f, -0.3f, 1.2f, 0f),
            com.example.data.model.PlacedGlyph("g2", "🜛", "Lapis", 0f, 0.2f, 1.4f, 0f)
        ),
        createdTimestamp = System.currentTimeMillis()
    )

    // Save/consecrate sigil
    repo.savePersonalSigil(testSigil)

    // Verify added to personalSigils
    assertTrue(repo.personalSigils.value.any { it.id == "test_sigil_1" })
    assertEquals(initialSigilsCount + 1, repo.personalSigils.value.size)

    // Verify awarded +100 XP
    assertTrue(repo.adeptProfile.value.experiencePoints >= initialExp + 100)

    // Verify inscribed into the journal (category is stored as the entry's theme)
    assertTrue(awaitJournalEntry(repo) { it.title.contains("Amulet of Paracelsus") && it.theme == "Personal Sigil" })

    // Verify JSON roundtrip
    val json = testSigil.toJson()
    val restored = com.example.data.model.PersonalSigil.fromJson(json)
    assertEquals(testSigil.id, restored.id)
    assertEquals(testSigil.name, restored.name)
    assertEquals(testSigil.glyphs.size, restored.glyphs.size)

    // Verify deletion
    repo.deletePersonalSigil("test_sigil_1")
    assertFalse(repo.personalSigils.value.any { it.id == "test_sigil_1" })
  }
}

