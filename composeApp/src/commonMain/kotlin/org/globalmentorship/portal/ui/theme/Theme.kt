package org.globalmentorship.portal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// GMI Portal Brand Palette
val GmiNavy = Color(0xFF16255C) // Deep Navy header and active state
val GmiNavyDark = Color(0xFF0F1A42)
val GmiNavySurface = Color(0xFF1B2A6B)

val GmiPrimaryBlue = Color(0xFF1A73E8) // Action primary blue
val GmiBlueHover = Color(0xFF1557B0)
val GmiBlueLight = Color(0xFFE8F0FE)

val GmiOrangeCurrent = Color(0xFFE65100) // Orange accent for "Current" session / "Suggested" time
val GmiOrangeLight = Color(0xFFFFF3E0)

val GmiGreenCompleted = Color(0xFF2E7D32) // Green accent for "Completed" session / "Confirmed" meeting
val GmiGreenLight = Color(0xFFE8F5E9)

val GmiGrayLocked = Color(0xFF757575)
val GmiGrayLight = Color(0xFFF1F3F4)

val GmiBackground = Color(0xFFF8F9FA) // Very light gray background
val GmiSurface = Color(0xFFFFFFFF)
val GmiBorder = Color(0xFFE0E0E0)
val GmiTextPrimary = Color(0xFF202124)
val GmiTextSecondary = Color(0xFF5F6368)

private val LightColorScheme = lightColorScheme(
    primary = GmiNavy,
    onPrimary = Color.White,
    primaryContainer = GmiBlueLight,
    onPrimaryContainer = GmiNavy,
    secondary = GmiPrimaryBlue,
    onSecondary = Color.White,
    secondaryContainer = GmiBlueLight,
    onSecondaryContainer = GmiPrimaryBlue,
    tertiary = GmiOrangeCurrent,
    onTertiary = Color.White,
    tertiaryContainer = GmiOrangeLight,
    onTertiaryContainer = GmiOrangeCurrent,
    background = GmiBackground,
    onBackground = GmiTextPrimary,
    surface = GmiSurface,
    onSurface = GmiTextPrimary,
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = GmiTextSecondary,
    outline = GmiBorder
)

val GmiShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun GmiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme, // Maintain GMI brand identity
        shapes = GmiShapes,
        content = content
    )
}
