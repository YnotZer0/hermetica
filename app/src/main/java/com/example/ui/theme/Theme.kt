package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HermeticaColorScheme = darkColorScheme(
    primary = AntiqueGold,
    onPrimary = NigredoBlack,
    primaryContainer = ChamberSurfaceLight,
    onPrimaryContainer = ParchmentLight,
    secondary = BrassChamber,
    onSecondary = NigredoBlack,
    secondaryContainer = ChamberSurface,
    onSecondaryContainer = ParchmentBase,
    tertiary = EmeraldHermetic,
    onTertiary = ParchmentLight,
    tertiaryContainer = ChamberBorder,
    onTertiaryContainer = ParchmentLight,
    background = NigredoBlack,
    onBackground = ParchmentLight,
    surface = ChamberSurface,
    onSurface = ParchmentLight,
    surfaceVariant = ChamberSurfaceLight,
    onSurfaceVariant = ParchmentDark,
    outline = ChamberBorder,
    outlineVariant = AntiqueGold.copy(alpha = 0.4f),
    error = WaxSealRed,
    onError = ParchmentLight
)

@Composable
fun HermeticaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HermeticaColorScheme,
        typography = Typography,
        content = content
    )
}
