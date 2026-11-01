package com.example.ui.particles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.example.ui.theme.*
import kotlinx.coroutines.isActive
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data model for an alchemical touch trail particle.
 * Simulates glowing furnace embers and celestial gold dust motes.
 */
class AlchemicalParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val initialRadius: Float,
    val maxLifeMs: Long,
    val birthTime: Long,
    val color: Color,
    val isSparkle: Boolean,
    val wobbleFreq: Float,
    val wobblePhase: Float
)

/**
 * Controller managing the active particles, spawning, and physics simulation.
 */
class AlchemicalParticleSystem(
    val maxParticles: Int = 90
) {
    val particles = CopyOnWriteArrayList<AlchemicalParticle>()
    var isSimulating by mutableStateOf(false)
        private set

    private val random = Random(1684)

    // Esoteric ember and gold color palette
    private val emberColors = listOf(
        GoldBright,
        CitrinitasAmber,
        AthanorEmber,
        AntiqueGold,
        RubedoCrimson,
        Color(0xFFFFE082), // Pale solar gold
        Color(0xFFFFAB40)  // Luminous flame amber
    )

    fun spawn(originX: Float, originY: Float, count: Int = 2) {
        val now = System.currentTimeMillis()
        val toAdd = mutableListOf<AlchemicalParticle>()

        for (i in 0 until count) {
            if (particles.size + toAdd.size >= maxParticles) break

            val angle = random.nextDouble(0.0, 2 * Math.PI)
            val speed = random.nextDouble(0.6, 2.8).toFloat()

            // Buoyant upward drift (vy < 0)
            val vx = (cos(angle) * speed).toFloat()
            val vy = (sin(angle) * speed * 0.7f - random.nextDouble(1.0, 3.2)).toFloat()

            val radius = random.nextDouble(2.2, 5.5).toFloat()
            val lifespan = random.nextLong(450, 850)
            val selectedColor = emberColors[random.nextInt(emberColors.size)]
            val isSparkle = random.nextDouble() < 0.28 // 28% of particles are 4-pointed alchemical sparks

            toAdd.add(
                AlchemicalParticle(
                    x = originX + random.nextInt(-8, 9).toFloat(),
                    y = originY + random.nextInt(-8, 9).toFloat(),
                    vx = vx,
                    vy = vy,
                    initialRadius = radius,
                    maxLifeMs = lifespan,
                    birthTime = now,
                    color = selectedColor,
                    isSparkle = isSparkle,
                    wobbleFreq = random.nextDouble(0.008, 0.02).toFloat(),
                    wobblePhase = random.nextDouble(0.0, Math.PI * 2).toFloat()
                )
            )
        }

        particles.addAll(toAdd)
        if (!isSimulating) {
            isSimulating = true
        }
    }

    fun update(now: Long, dtSec: Float) {
        val iterator = particles.iterator()
        val toRemove = mutableListOf<AlchemicalParticle>()

        while (iterator.hasNext()) {
            val p = iterator.next()
            val age = now - p.birthTime
            if (age >= p.maxLifeMs) {
                toRemove.add(p)
                continue
            }

            // Physics: Slight air friction and buoyant upward thermal draft
            p.vx *= 0.94f
            p.vy = (p.vy - 0.08f) * 0.96f // upward chimney drift

            // Sine wobble for atmospheric flame turbulence
            val wobble = sin(age * p.wobbleFreq + p.wobblePhase) * 0.45f
            p.x += (p.vx + wobble)
            p.y += p.vy
        }

        if (toRemove.isNotEmpty()) {
            particles.removeAll(toRemove)
        }

        if (particles.isEmpty()) {
            isSimulating = false
        }
    }
}

/**
 * Top-level container providing the interactive touch-trail particle effect overlay.
 * Intercepts touch movements using PointerEventPass.Initial without consuming events,
 * allowing child UI components (buttons, sliders, lists) to function without impediment.
 */
@Composable
fun AlchemicalParticleContainer(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val particleSystem = remember { AlchemicalParticleSystem() }
    var lastSpawnPos by remember { mutableStateOf<Offset?>(null) }
    var frameTrigger by remember { mutableLongStateOf(0L) }

    // Run animation loop only when particles are active to conserve battery & CPU
    LaunchedEffect(particleSystem.isSimulating) {
        if (particleSystem.isSimulating) {
            var lastTime = System.currentTimeMillis()
            while (isActive && particleSystem.isSimulating) {
                withFrameMillis { frameTime ->
                    val now = System.currentTimeMillis()
                    val dt = ((now - lastTime) / 1000f).coerceIn(0.001f, 0.1f)
                    lastTime = now

                    particleSystem.update(now, dt)
                    frameTrigger = frameTime
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        // Pass.Initial observes gesture without consuming it
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        for (change in event.changes) {
                            if (change.pressed) {
                                val currentPos = change.position
                                val lastPos = lastSpawnPos

                                if (lastPos == null) {
                                    particleSystem.spawn(currentPos.x, currentPos.y, count = 3)
                                } else {
                                    val dx = currentPos.x - lastPos.x
                                    val dy = currentPos.y - lastPos.y
                                    val dist = kotlin.math.sqrt(dx * dx + dy * dy)

                                    if (dist > 12f) {
                                        // Interpolate along rapid stroke so trail is continuous
                                        val steps = (dist / 14f).toInt().coerceIn(1, 4)
                                        for (step in 1..steps) {
                                            val t = step.toFloat() / steps
                                            val ix = lastPos.x + dx * t
                                            val iy = lastPos.y + dy * t
                                            particleSystem.spawn(ix, iy, count = 2)
                                        }
                                        lastSpawnPos = currentPos
                                    }
                                }
                            } else {
                                lastSpawnPos = null
                            }
                        }
                    }
                }
            }
    ) {
        // App Content beneath the overlay
        content()

        // Non-interactive Canvas rendering the glowing ember motes on top
        if (enabled && (particleSystem.isSimulating || particleSystem.particles.isNotEmpty())) {
            // Read frameTrigger so canvas invalidates on each frame
            @Suppress("UNUSED_VARIABLE")
            val frame = frameTrigger

            Canvas(modifier = Modifier.fillMaxSize()) {
                val now = System.currentTimeMillis()

                for (p in particleSystem.particles) {
                    val age = now - p.birthTime
                    if (age >= p.maxLifeMs) continue

                    val progress = (age.toFloat() / p.maxLifeMs).coerceIn(0f, 1f)
                    // Fade out near end of life
                    val alpha = (1f - progress).coerceIn(0f, 1f)
                    // Radius shrinks as ember burns out
                    val currentRadius = p.initialRadius * (1f - progress * 0.65f)

                    if (p.isSparkle) {
                        // Draw 4-pointed diamond spark of Philosopher's Gold ✦
                        val sparkColor = p.color.copy(alpha = alpha)
                        val armLen = currentRadius * 2.2f
                        val center = Offset(p.x, p.y)

                        val path = Path().apply {
                            moveTo(center.x, center.y - armLen)
                            lineTo(center.x + currentRadius * 0.45f, center.y)
                            lineTo(center.x + armLen, center.y)
                            lineTo(center.x + currentRadius * 0.45f, center.y)
                            lineTo(center.x, center.y + armLen)
                            lineTo(center.x - currentRadius * 0.45f, center.y)
                            lineTo(center.x - armLen, center.y)
                            close()
                        }
                        drawPath(path = path, color = sparkColor)
                    } else {
                        // Glowing spherical ember with soft halo
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    p.color.copy(alpha = alpha),
                                    p.color.copy(alpha = alpha * 0.45f),
                                    Color.Transparent
                                ),
                                center = Offset(p.x, p.y),
                                radius = currentRadius * 2.5f
                            ),
                            radius = currentRadius * 2.5f,
                            center = Offset(p.x, p.y)
                        )

                        // Core bright pinpoint
                        drawCircle(
                            color = Color.White.copy(alpha = alpha * 0.85f),
                            radius = currentRadius * 0.4f,
                            center = Offset(p.x, p.y)
                        )
                    }
                }
            }
        }
    }
}
