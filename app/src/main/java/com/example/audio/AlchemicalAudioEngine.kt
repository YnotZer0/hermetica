package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Random
import kotlin.math.*

/**
 * Alchemical Audio Engine:
 * Generates programmatic 17th-century atmospheric soundscapes (bubbling cauldron,
 * turning parchment folios, distant cathedral bells, athanor embers, and transmutation chords)
 * directly via PCM AudioTrack synthesis.
 */
class AlchemicalAudioEngine private constructor() {

    companion object {
        @Volatile
        private var instance: AlchemicalAudioEngine? = null

        fun getInstance(): AlchemicalAudioEngine =
            instance ?: synchronized(this) {
                instance ?: AlchemicalAudioEngine().also { instance = it }
            }

        private const val SAMPLE_RATE = 44100
        private const val TAG = "AlchemicalAudio"
    }

    private val audioScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val random = Random()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isAmbientPlaying = MutableStateFlow(false)
    val isAmbientPlaying: StateFlow<Boolean> = _isAmbientPlaying.asStateFlow()

    private val _masterVolume = MutableStateFlow(0.75f)
    val masterVolume: StateFlow<Float> = _masterVolume.asStateFlow()

    private var ambientJob: Job? = null

    fun setMuted(muted: Boolean) {
        _isMuted.value = muted
        if (muted && _isAmbientPlaying.value) {
            stopAmbientSoundscape()
        }
    }

    fun setMasterVolume(vol: Float) {
        _masterVolume.value = vol.coerceIn(0f, 1f)
    }

    fun toggleAmbient() {
        if (_isAmbientPlaying.value) {
            stopAmbientSoundscape()
        } else {
            startAmbientSoundscape()
        }
    }

    /**
     * Plays a distant 17th-century bronze monastery/cathedral bell chime with authentic bell harmonics.
     */
    fun playBellChime() {
        if (_isMuted.value) return
        audioScope.launch {
            try {
                val durationSec = 3.5
                val numSamples = (durationSec * SAMPLE_RATE).toInt()
                val samples = ShortArray(numSamples)

                val baseFreq = 392.0 // G4 bell fundamental
                // Harmonics of a bronze bell: Fundamental, Tierce (minor 3rd), Quint, Nominal (octave)
                val f1 = baseFreq
                val f2 = baseFreq * 1.19 // minor third
                val f3 = baseFreq * 1.50 // fifth
                val f4 = baseFreq * 2.00 // octave
                val f5 = baseFreq * 2.75 // super-octave

                val vol = _masterVolume.value

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE

                    // Complex bell envelope with fast strike attack and differing decay rates per harmonic
                    val env1 = exp(-t * 1.1)
                    val env2 = exp(-t * 1.8)
                    val env3 = exp(-t * 2.4)
                    val env4 = exp(-t * 3.2)
                    val env5 = exp(-t * 4.5)

                    val s = (sin(2 * PI * f1 * t) * 0.45 * env1) +
                            (sin(2 * PI * f2 * t) * 0.25 * env2) +
                            (sin(2 * PI * f3 * t) * 0.18 * env3) +
                            (sin(2 * PI * f4 * t) * 0.12 * env4) +
                            (sin(2 * PI * f5 * t) * 0.08 * env5)

                    // Soft strike ping at start
                    val strikeNoise = if (t < 0.03) (random.nextDouble() * 2 - 1) * exp(-t * 90) * 0.2 else 0.0

                    val sampleValue = ((s + strikeNoise) * vol * Short.MAX_VALUE * 0.75)
                        .coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())

                    samples[i] = sampleValue.toInt().toShort()
                }

                playPcmBuffer(samples)
            } catch (e: Exception) {
                Log.w(TAG, "Bell sound error", e)
            }
        }
    }

    /**
     * Plays the sound of bubbling alchemical fluids in the cauldron or alembic.
     */
    fun playBubblingCauldron() {
        if (_isMuted.value) return
        audioScope.launch {
            try {
                val durationSec = 1.2
                val numSamples = (durationSec * SAMPLE_RATE).toInt()
                val samples = ShortArray(numSamples)
                val vol = _masterVolume.value

                // Generate random bubbling pop bursts
                val bubblePops = List(8) {
                    Triple(
                        random.nextDouble() * 0.9,       // start time
                        220.0 + random.nextDouble() * 450.0, // bubble pitch
                        0.04 + random.nextDouble() * 0.05    // bubble duration
                    )
                }

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    var s = 0.0

                    for ((startT, freq, dur) in bubblePops) {
                        if (t >= startT && t < startT + dur) {
                            val bt = (t - startT) / dur
                            // Upward pitch sweep characteristic of liquid bubbles
                            val bubbleFreq = freq * (1.0 + bt * 1.8)
                            val env = sin(PI * bt) * exp(-bt * 3.5)
                            s += sin(2 * PI * bubbleFreq * (t - startT)) * env * 0.4
                        }
                    }

                    // Low liquid rumble
                    val waterRumble = sin(2 * PI * 65.0 * t) * 0.08 * (sin(2 * PI * 3.0 * t).absoluteValue)

                    val sampleValue = ((s + waterRumble) * vol * Short.MAX_VALUE * 0.8)
                        .coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())

                    samples[i] = sampleValue.toInt().toShort()
                }

                playPcmBuffer(samples)
            } catch (e: Exception) {
                Log.w(TAG, "Bubbling sound error", e)
            }
        }
    }

    /**
     * Plays the sound of a firm wax seal stamp and brittle seal fracture.
     */
    fun playWaxSeal() {
        if (_isMuted.value) return
        audioScope.launch {
            try {
                val durationSec = 0.35
                val numSamples = (durationSec * SAMPLE_RATE).toInt()
                val samples = ShortArray(numSamples)
                val vol = _masterVolume.value

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    // Deep thud impact
                    val thud = sin(2 * PI * 95.0 * t) * exp(-t * 30.0) * 0.65
                    // Crisp wax snap/crackle
                    val snap = if (t in 0.015..0.08) (random.nextDouble() * 2 - 1) * exp(-t * 40.0) * 0.35 else 0.0

                    val sampleValue = ((thud + snap) * vol * Short.MAX_VALUE)
                        .coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())

                    samples[i] = sampleValue.toInt().toShort()
                }

                playPcmBuffer(samples)
            } catch (e: Exception) {
                Log.w(TAG, "Wax seal sound error", e)
            }
        }
    }

    /**
     * Plays a crisp brass ratchet click of a 17th-century cipher volvelle.
     */
    fun playCipherClick() {
        if (_isMuted.value) return
        audioScope.launch {
            try {
                val durationSec = 0.06
                val numSamples = (durationSec * SAMPLE_RATE).toInt()
                val samples = ShortArray(numSamples)
                val vol = _masterVolume.value

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    // High metallic ping click with fast dampening
                    val ping = sin(2 * PI * 1850.0 * t) * exp(-t * 120.0) * 0.7
                    val click = sin(2 * PI * 680.0 * t) * exp(-t * 90.0) * 0.4
                    val noise = (random.nextDouble() * 2 - 1) * exp(-t * 180.0) * 0.2

                    val sampleValue = ((ping + click + noise) * vol * Short.MAX_VALUE)
                        .coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())

                    samples[i] = sampleValue.toInt().toShort()
                }

                playPcmBuffer(samples)
            } catch (e: Exception) {
                Log.w(TAG, "Cipher click sound error", e)
            }
        }
    }

    /**
     * Plays a subtle wet ink stroke & vellum absorption sound when switching folios.
     */
    fun playInkSpread() {
        if (_isMuted.value) return
        audioScope.launch {
            try {
                val durationSec = 0.42
                val numSamples = (durationSec * SAMPLE_RATE).toInt()
                val samples = ShortArray(numSamples)
                val vol = _masterVolume.value

                var filterState = 0.0
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    // Gentle envelope: rises smoothly and tapers
                    val env = (sin(PI * (t / durationSec))).pow(1.5) * exp(-t * 2.5)

                    // Soft parchment friction noise filtered
                    val whiteNoise = (random.nextDouble() * 2.0 - 1.0)
                    filterState += (whiteNoise - filterState) * 0.12 // Low-pass filter for smooth whisper

                    // Whispering quill harmonic (420 Hz subtly sweeping to 280 Hz)
                    val freq = 420.0 - (t / durationSec) * 140.0
                    val quillTone = sin(2 * PI * freq * t) * 0.22

                    val combined = (filterState * 0.78 + quillTone) * env * vol * 0.35 * Short.MAX_VALUE
                    val clamped = combined.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())
                    samples[i] = clamped.toInt().toShort()
                }

                playPcmBuffer(samples)
            } catch (e: Exception) {
                Log.w(TAG, "Ink spread sound error", e)
            }
        }
    }

    /**
     * Plays an alchemical transmutation celestial chord (Pythagorean harmonic series).
     */
    fun playTransmutationHarmonic() {
        if (_isMuted.value) return
        audioScope.launch {
            try {
                val durationSec = 2.4
                val numSamples = (durationSec * SAMPLE_RATE).toInt()
                val samples = ShortArray(numSamples)
                val vol = _masterVolume.value

                // Pythagorean Perfect Fifth and Octaves (261.63 Hz C4, 392.44 Hz G4, 523.25 Hz C5, 654 Hz E5)
                val freqs = listOf(261.63, 392.00, 523.25, 659.25, 784.00)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val env = (sin(PI * (t / durationSec))).pow(0.7) * exp(-t * 0.4)

                    var wave = 0.0
                    freqs.forEachIndexed { idx, f ->
                        val phaseOffset = idx * 0.12
                        val amp = if (t > phaseOffset) sin(2 * PI * f * (t - phaseOffset)) * (1.0 / (idx + 1)) else 0.0
                        wave += amp
                    }

                    // Shimmer tremolo
                    val shimmer = 1.0 + 0.15 * sin(2 * PI * 6.5 * t)

                    val sampleValue = (wave * env * shimmer * vol * Short.MAX_VALUE * 0.45)
                        .coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())

                    samples[i] = sampleValue.toInt().toShort()
                }

                playPcmBuffer(samples)
            } catch (e: Exception) {
                Log.w(TAG, "Transmutation sound error", e)
            }
        }
    }

    /**
     * Starts continuous subtle 17th-century laboratory atmosphere:
     * Gentle bubbling alembic fluids, quiet athanor furnace flame rumble, and periodic distant abbey chimes.
     */
    fun startAmbientSoundscape() {
        if (_isMuted.value) return
        if (_isAmbientPlaying.value) return

        _isAmbientPlaying.value = true
        ambientJob = audioScope.launch {
            val bufferSize = SAMPLE_RATE // 1-second chunks
            val buffer = ShortArray(bufferSize)

            var track: AudioTrack? = null
            try {
                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2 * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()

                var phaseFlame = 0.0
                var phaseLiquid = 0.0
                var secondCounter = 0

                while (isActive && _isAmbientPlaying.value) {
                    val vol = _masterVolume.value * 0.35f // keep ambient gentle in background

                    for (i in 0 until bufferSize) {
                        // 1. Subtle warm athanor flame murmur (low rumble 45-90 Hz with soft crackle)
                        phaseFlame += (2 * PI * (52.0 + sin(i * 0.0003) * 12.0)) / SAMPLE_RATE
                        val flame = sin(phaseFlame) * 0.25

                        // 2. Distillation fluid drip/bubble
                        phaseLiquid += (2 * PI * (120.0 + cos(i * 0.0005) * 40.0)) / SAMPLE_RATE
                        val liquid = sin(phaseLiquid) * 0.15 * (sin(i * 0.002).pow(4))

                        // 3. Occasional soft ember crackle
                        val crackle = if (random.nextDouble() < 0.0004) (random.nextDouble() * 2 - 1) * 0.3 else 0.0

                        val mixed = (flame + liquid + crackle) * vol * Short.MAX_VALUE
                        buffer[i] = mixed.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }

                    track.write(buffer, 0, bufferSize)

                    secondCounter++
                    // Every ~45 seconds, play a distant bell in the background
                    if (secondCounter % 45 == 0) {
                        playBellChime()
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Ambient loop error", e)
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (ignored: Exception) {}
            }
        }
    }

    fun stopAmbientSoundscape() {
        _isAmbientPlaying.value = false
        ambientJob?.cancel()
        ambientJob = null
    }

    // ---- Contemplation Timer sounds ----

    private var droneJob: Job? = null
    @Volatile private var droneStopRequested = false

    /**
     * Starts a soft, sustained harmonic drone for contemplation sessions, like a resonating
     * singing bowl or temple tanpura. Built from a G3 fundamental with fifth, octave and upper
     * partials (pitched high enough to be audible on phone speakers), each paired with a
     * slightly detuned twin for a slow shimmer, plus a gentle "breathing" swell.
     * Fades in over 4 seconds. Call [stopMeditationDrone] to fade it out.
     */
    fun startMeditationDrone() {
        if (_isMuted.value) return
        if (droneJob?.isActive == true && !droneStopRequested) return
        droneJob?.cancel()
        droneStopRequested = false

        droneJob = audioScope.launch {
            val chunk = SAMPLE_RATE / 4 // 250 ms chunks keep stop/fade responsive
            val buffer = ShortArray(chunk)
            val partials = doubleArrayOf(196.0, 293.66, 392.0, 587.33, 784.0)
            val amps = doubleArrayOf(0.35, 0.25, 0.22, 0.12, 0.06)
            val detune = doubleArrayOf(0.0, 0.4)
            val phases = DoubleArray(partials.size * 2)
            val fadeInSamples = SAMPLE_RATE * 4
            val fadeOutSamples = SAMPLE_RATE * 2
            var sampleIndex = 0L
            var fadeOutRemaining = -1

            var track: AudioTrack? = null
            try {
                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(chunk * 2 * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
                track.play()

                while (isActive) {
                    if (droneStopRequested && fadeOutRemaining < 0) fadeOutRemaining = fadeOutSamples
                    if (_isMuted.value) break
                    val vol = _masterVolume.value * 0.22

                    for (i in 0 until chunk) {
                        val t = sampleIndex.toDouble() / SAMPLE_RATE
                        var fade = min(1.0, sampleIndex.toDouble() / fadeInSamples)
                        if (fadeOutRemaining >= 0) {
                            fade *= fadeOutRemaining.toDouble() / fadeOutSamples
                            if (fadeOutRemaining > 0) fadeOutRemaining--
                        }
                        val breath = 0.75 + 0.25 * sin(2 * PI * 0.08 * t)

                        var s = 0.0
                        for (k in partials.indices) {
                            for (j in detune.indices) {
                                val idx = k * 2 + j
                                phases[idx] = (phases[idx] + 2 * PI * (partials[k] + detune[j]) / SAMPLE_RATE) % (2 * PI)
                                s += sin(phases[idx]) * amps[k] * 0.5
                            }
                        }

                        val value = s * breath * fade * vol * Short.MAX_VALUE
                        buffer[i] = value.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        sampleIndex++
                    }

                    track.write(buffer, 0, chunk)
                    if (fadeOutRemaining == 0) break
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Meditation drone error", e)
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (ignored: Exception) {}
            }
        }
    }

    /** Fades the contemplation drone out over about 2 seconds, then stops it. */
    fun stopMeditationDrone() {
        droneStopRequested = true
    }

    /**
     * Rings the monastery bell [strikes] times, spaced apart, as a ritual signal
     * (one strike to open a session, one at the halfway point, three to close).
     */
    fun playRitualBells(strikes: Int, spacingMs: Long = 2600L) {
        if (_isMuted.value) return
        audioScope.launch {
            repeat(strikes) { index ->
                playBellChime()
                if (index < strikes - 1) delay(spacingMs)
            }
        }
    }

    private fun playPcmBuffer(samples: ShortArray) {
        var track: AudioTrack? = null
        try {
            val minBuf = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufSize = max(minBuf, samples.size * 2)

            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()

            // Calculate duration and release after playback completes
            val durationMs = (samples.size.toDouble() / SAMPLE_RATE * 1000).toLong() + 200
            audioScope.launch {
                delay(durationMs)
                try {
                    track.stop()
                    track.release()
                } catch (ignored: Exception) {}
            }
        } catch (e: Exception) {
            Log.w(TAG, "playPcmBuffer failed", e)
            try { track?.release() } catch (ignored: Exception) {}
        }
    }
}
