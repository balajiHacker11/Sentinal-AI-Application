package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CrimsonPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDE7E7),
    onPrimaryContainer = Color(0xFFD32F2F),
    secondary = TrustBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F0FE),
    onSecondaryContainer = Color(0xFF1A73E8),
    tertiary = AmberWarning,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = OnSurfaceDark,
    surface = SurfaceCard,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFFF2F3F7),
    onSurfaceVariant = TextSecondary,
    outline = BorderOutline,
    error = RiskCriticalRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonLight,
    onPrimary = Color(0xFF690005),
    primaryContainer = CrimsonPrimaryDark,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = TrustBlueLight,
    onSecondary = Color(0xFF003068),
    secondaryContainer = TrustBlueDark,
    onSecondaryContainer = Color(0xFFD6E3FF),
    tertiary = AmberLight,
    onTertiary = Color(0xFF451B00),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE1E2E5),
    surface = Color(0xFF24262A),
    onSurface = Color(0xFFE1E2E5),
    outline = Color(0xFF44474E),
    error = CrimsonLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Light theme app default
    dynamicColor: Boolean = false, // Set false to keep brand vibrant safety identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
