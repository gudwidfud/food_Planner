package com.example.foodplanner.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Palette sampled from the app icon: sage background, leaf greens, tomato red.
private val Sage = Color(0xFF9AB298)
private val LeafDark = Color(0xFF647E68)
private val LeafOlive = Color(0xFF7B8F57)
private val Tomato = Color(0xFFE84836)

private val LightColors = lightColorScheme(
    primary = Color(0xFF3F6544),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E6CF),
    onPrimaryContainer = Color(0xFF0F2A14),
    secondary = LeafDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDE8DA),
    onSecondaryContainer = Color(0xFF1B2D1F),
    tertiary = Color(0xFFC53A29),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDAD3),
    onTertiaryContainer = Color(0xFF410002),
    background = Color(0xFFF4F7F2),
    onBackground = Color(0xFF1A1C19),
    surface = Color(0xFFF9FBF7),
    onSurface = Color(0xFF1A1C19),
    surfaceVariant = Color(0xFFE1E9DE),
    onSurfaceVariant = Color(0xFF424940),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF1F5EE),
    surfaceContainer = Color(0xFFEBF1E8),
    surfaceContainerHigh = Color(0xFFE5ECE2),
    outline = Color(0xFF727970),
    outlineVariant = Color(0xFFC3CBC0),
)

private val DarkColors = darkColorScheme(
    primary = Sage,
    onPrimary = Color(0xFF0F2A14),
    primaryContainer = Color(0xFF2C4A31),
    onPrimaryContainer = Color(0xFFD3E6CF),
    secondary = Color(0xFFB4C9B3),
    onSecondary = Color(0xFF203524),
    secondaryContainer = Color(0xFF384C3A),
    onSecondaryContainer = Color(0xFFDDE8DA),
    tertiary = Color(0xFFFFB4A7),
    onTertiary = Color(0xFF690005),
    tertiaryContainer = Color(0xFF93000A),
    onTertiaryContainer = Color(0xFFFFDAD3),
    background = Color(0xFF111411),
    onBackground = Color(0xFFE1E3DD),
    surface = Color(0xFF111411),
    onSurface = Color(0xFFE1E3DD),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC3CBC0),
    surfaceContainerLowest = Color(0xFF0C0F0C),
    surfaceContainerLow = Color(0xFF191C19),
    surfaceContainer = Color(0xFF1D201D),
    surfaceContainerHigh = Color(0xFF272B27),
    outline = Color(0xFF8D948B),
    outlineVariant = Color(0xFF424940),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun FoodPlannerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = AppShapes,
        content = content,
    )
}
