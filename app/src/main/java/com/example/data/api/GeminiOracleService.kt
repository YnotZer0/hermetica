package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.DailyInsight
import com.example.data.repository.HermeticData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class GeminiOracleService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateDailyInsight(focusTopic: String? = null): DailyInsight = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("GeminiOracle", "API key not set, using fallback ancient insight")
            return@withContext getOfflineInsight(focusTopic)
        }

        val prompt = """
            You are the venerable 17th-century Hermetic Scholar and Alchemist. 
            Deliver an authentic, profound 'One Amazing Thing to Learn About Today' drawing strictly from the corpus of Hermetic Philosophy, The Kybalion (The 7 Hermetic Principles), The Emerald Tablet of Hermes Trismegistus, Paracelsus, Heinrich Cornelius Agrippa, or Carl Jung's alchemical psychology.
            ${if (!focusTopic.isNullOrBlank()) "The seeker requests insight into: $focusTopic" else "Provide a rare and illuminating ancient secret."}

            Format your response strictly as valid JSON with no markdown backticks, with the following keys:
            {
              "title": "Title of the revelation in 17th century scholarly tone",
              "sourceText": "Specific ancient treatise (e.g. The Kybalion, Tabula Smaragdina, Corpus Hermeticum, or Jung's Mysterium Coniunctionis)",
              "ancientQuote": "Verbatim or authentic translation quote/maxim from the ancient text",
              "esotericExegesis": "Deep metaphysical explanation of the secret meaning behind the words",
              "psychologicalCorrelation": "How this relates to modern mind, shadow work, neuro-psychology or Jungian archetypes",
              "dailyHermeticContemplation": "A practical, meditative exercise or ritual the seeker can perform today"
            }
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                Log.e("GeminiOracle", "HTTP error ${response.code}: $responseBody")
                return@withContext getOfflineInsight(focusTopic)
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val textContent = firstCandidate?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")

            if (textContent.isNullOrBlank()) {
                return@withContext getOfflineInsight(focusTopic)
            }

            val parsedJson = JSONObject(textContent.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim())
            val dateStamp = "Anno Domini 1684 • " + SimpleDateFormat("EEEE, MMMM d", Locale.US).format(Date())

            DailyInsight(
                id = "ai_" + System.currentTimeMillis(),
                date = dateStamp,
                title = parsedJson.optString("title", "The Secret of the Solar Vessel"),
                sourceText = parsedJson.optString("sourceText", "The Kybalion & Tabula Smaragdina"),
                ancientQuote = parsedJson.optString("ancientQuote", "The lips of wisdom are closed, except to the ears of Understanding."),
                esotericExegesis = parsedJson.optString("esotericExegesis", "All external transmutation mirrors the internal refinement of the soul."),
                psychologicalCorrelation = parsedJson.optString("psychologicalCorrelation", "The shadow must be brought to conscious light before the gold can emerge."),
                dailyHermeticContemplation = parsedJson.optString("dailyHermeticContemplation", "Hold stillness for three minutes and observe your mind as the alchemical crucible."),
                glyphSymbol = listOf("☉", "☽", "☿", "🜍", "🜚", "🜛", "🜎").random()
            )
        } catch (e: Exception) {
            Log.e("GeminiOracle", "Error generating daily insight", e)
            getOfflineInsight(focusTopic)
        }
    }

    suspend fun consultOracle(seekerQuery: String, domain: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateOfflineOracleAnswer(seekerQuery, domain)
        }

        val prompt = """
            You are the Grand Oracle of Hermetica, a master of 17th-century esoteric science, natural philosophy, spagyric alchemy, ceremonial magic, and archetypal psychology. 
            You speak with the gravitas, poetic elegance, and profound wisdom of Hermes Trismegistus, Paracelsus, and Carl Jung.
            Domain of inquiry: $domain.
            Seeker's question: "$seekerQuery"

            Provide an illuminating, cryptic yet actionable answer (around 3 to 4 paragraphs). Quote or reference authentic Hermetic axioms (especially the Kybalion principles: Mentalism, Correspondence, Vibration, Polarity, Rhythm, Cause & Effect, Gender) or Latin alchemical maxims (Solve et Coagula, Visita Interiora Terrae, etc.). Include a practical psychological application for the seeker's modern life.
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.75)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                return@withContext generateOfflineOracleAnswer(seekerQuery, domain)
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val textContent = firstCandidate?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")

            textContent ?: generateOfflineOracleAnswer(seekerQuery, domain)
        } catch (e: Exception) {
            Log.e("GeminiOracle", "Error consulting oracle", e)
            generateOfflineOracleAnswer(seekerQuery, domain)
        }
    }

    /**
     * Offline / fallback insight used when no API key is configured or the Gemini call fails.
     *
     * Previously this ignored [topic] and always returned the same Kybalion text, so tapping
     * The Kybalion / Emerald Tablet / Corpus Hermeticum / etc. on the Oracle screen never
     * changed the scroll on a device without a working API connection. It now returns the
     * revelation that belongs to the selected source.
     */
    private fun getOfflineInsight(topic: String?): DailyInsight {
        val dateStamp = "Anno Domini 1684 • " + SimpleDateFormat("EEEE, MMMM d", Locale.US).format(Date())
        val t = topic?.lowercase(Locale.US).orEmpty()
        val insights = HermeticData.initialDailyInsights

        val base: DailyInsight = when {
            "emerald" in t -> insights.first { it.id == "emerald_father_sun" }
            "corpus" in t || "poimandres" in t -> insights.first { it.id == "corpus_poimandres" }
            "paracelsus" in t -> insights.first { it.id == "paracelsus_inner_physician" }
            "agrippa" in t -> insights.first { it.id == "agrippa_three_worlds" }
            "jung" in t -> jungianOfflineInsight
            else -> insights.first { it.id == "kybalion_mind" } // "The Kybalion" and default
        }

        return base.copy(
            id = "offline_${base.id}",
            date = dateStamp,
            glyphSymbol = when {
                "emerald" in t -> "🜫"
                "corpus" in t -> "☉"
                "paracelsus" in t -> "🜍"
                "agrippa" in t -> "✡"
                "jung" in t -> "☿"
                else -> "🜚"
            }
        )
    }

    private val jungianOfflineInsight = DailyInsight(
        id = "jung_shadow_prima_materia",
        date = "",
        title = "The Shadow as Prima Materia",
        sourceText = "C. G. Jung (Psychology and Alchemy)",
        ancientQuote = "The alchemical opus deals with the transformation of the personality through the blending and fusion of the noble with the base components, of the conscious with the unconscious.",
        esotericExegesis = "The alchemists sought the Prima Materia in dung heaps and despised places, insisting the stone 'is found in filth'. Jung read this as a precise psychological instruction: the raw material of transformation is exactly what we reject in ourselves. The Nigredo, the blackening, is the moment we stop projecting that darkness onto others and admit it into the vessel.",
        psychologicalCorrelation = "Shadow work in modern analytical psychology follows the same sequence: notice a strong irritation toward another person, recognise the projected trait, and integrate it consciously. What is integrated stops running the personality from behind the scenes.",
        dailyHermeticContemplation = "Name one quality in another person that provoked you today. Write down where that same quality lives in you, without judgment. Treat the page as the alembic: observe, do not condemn."
    )

    private fun generateOfflineOracleAnswer(query: String, domain: String): String {
        return """
            Hear the ancient voice of Hermes Trismegistus, O seeker of the veiled fires:
            
            Regarding thy query concerning "$query", know that under the celestial vault, 'As above, so below; as below, so above.' What thou perceivest as an external obstacle or dilemma is but the physical shadow cast by a subtler movement in the mental sphere. 
            
            In the ancient art of the Crucible, nothing is destroyed; all is transmuted. Apply the venerable Law of Polarity: that which vexes thee carries within itself the seed of its own resolution. Opposites are identical in nature, differing merely in degree. Seek not to banish the cold, but stoke the philosophical furnace of thy resolve.
            
            Reflect upon this tonight under the stars: Attend to the quiet alembic of thine own mind. Dissolve the rigid pride of certainty ('Solve'), and crystallize thy newfound clarity in deliberate deed ('Coagula'). Thus is the true Philosopher's Stone perfected within thy soul.
        """.trimIndent()
    }
}
