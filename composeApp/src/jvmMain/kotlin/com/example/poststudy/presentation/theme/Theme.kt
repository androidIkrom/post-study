package com.example.poststudy.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF3730A3),
    secondary = Color(0xFF0EA5E9),
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    background = Color(0xFFF8FAFC),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFF6366F1)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF38BDF8),
    surface = Color(0xFF0F172A),
    background = Color(0xFF020617),
    onSurface = Color(0xFFF1F5F9),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF818CF8)
)

object AppDesign {
    val BackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFECFDF5), // Very light emerald
            Color(0xFFD1FAE5), // Light emerald
            Color(0xFFFFFFFF)  // White
        )
    )
    
    val CardShape = RoundedCornerShape(32.dp)
    val ComponentShape = RoundedCornerShape(20.dp)
}

@Composable
fun PostStudyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
