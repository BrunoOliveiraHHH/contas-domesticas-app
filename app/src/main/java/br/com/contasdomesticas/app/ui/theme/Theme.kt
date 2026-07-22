package br.com.contasdomesticas.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta identica ao projeto anterior (Contas Domesticas web).
private val LightColors = lightColorScheme(
    primary = Color(0xFF613178),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF8B5A96),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFD82A76),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF4F6FB),
    onBackground = Color(0xFF1A1A2E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFEDE7F0),
    outline = Color(0xFFBB97BA),
    error = Color(0xFFEF4444)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9B59B6),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFB07CC6),
    onSecondary = Color(0xFF1A1625),
    tertiary = Color(0xFFE74C8C),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFF1A1625),
    onBackground = Color(0xFFE8E6F0),
    surface = Color(0xFF241E30),
    onSurface = Color(0xFFE8E6F0),
    surfaceVariant = Color(0xFF352E42),
    outline = Color(0xFF3D3550),
    error = Color(0xFFE74C3C)
)

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
