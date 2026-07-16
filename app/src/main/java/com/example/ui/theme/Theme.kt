package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = VibrantDarkPrimary,
    onPrimary = VibrantDarkOnPrimary,
    primaryContainer = VibrantDarkPrimaryContainer,
    onPrimaryContainer = VibrantDarkOnPrimaryContainer,
    secondary = VibrantDarkSecondary,
    onSecondary = VibrantDarkOnSecondary,
    background = VibrantDarkBackground,
    surface = VibrantDarkSurface,
    surfaceVariant = VibrantDarkSurfaceVariant,
    onBackground = VibrantDarkOnBackground,
    onSurface = VibrantDarkOnSurface,
    onSurfaceVariant = VibrantDarkOnSurfaceVariant,
    errorContainer = VibrantDarkErrorContainer,
    onErrorContainer = VibrantDarkOnErrorContainer,
    outline = VibrantDarkOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VibrantLightPrimary,
    onPrimary = VibrantLightOnPrimary,
    primaryContainer = VibrantLightPrimaryContainer,
    onPrimaryContainer = VibrantLightOnPrimaryContainer,
    secondary = VibrantLightSecondary,
    onSecondary = VibrantLightOnSecondary,
    background = VibrantLightBackground,
    surface = VibrantLightSurface,
    surfaceVariant = VibrantLightSurfaceVariant,
    onBackground = VibrantLightOnBackground,
    onSurface = VibrantLightOnSurface,
    onSurfaceVariant = VibrantLightOnSurfaceVariant,
    errorContainer = VibrantLightErrorContainer,
    onErrorContainer = VibrantLightOnErrorContainer,
    outline = VibrantLightOutline
  )

// Lavender Palette (pastel purple)
private val LavenderLight = lightColorScheme(
    primary = Color(0xFF673AB7),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEDE7F6),
    onPrimaryContainer = Color(0xFF311B92),
    secondary = Color(0xFF9C27B0),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFAF8FF),
    onBackground = Color(0xFF1C1A22),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1A22),
    surfaceVariant = Color(0xFFF3E5F5),
    onSurfaceVariant = Color(0xFF5E35B1),
    outline = Color(0xFFB39DDB),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)
private val LavenderDark = darkColorScheme(
    primary = Color(0xFFD1C4E9),
    onPrimary = Color(0xFF311B92),
    primaryContainer = Color(0xFF4527A0),
    onPrimaryContainer = Color(0xFFEDE7F6),
    secondary = Color(0xFFE1BEE7),
    onSecondary = Color(0xFF4A148C),
    background = Color(0xFF121016),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1A22),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2C243C),
    onSurfaceVariant = Color(0xFFD1C4E9),
    outline = Color(0xFF7E57C2),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// Mint Palette (pastel teal/green)
private val MintLight = lightColorScheme(
    primary = Color(0xFF00796B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0F2F1),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = Color(0xFF009688),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF4FAF8),
    onBackground = Color(0xFF0E1614),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0E1614),
    surfaceVariant = Color(0xFFD0EBE6),
    onSurfaceVariant = Color(0xFF004D40),
    outline = Color(0xFF80CBC4),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)
private val MintDark = darkColorScheme(
    primary = Color(0xFF80CBC4),
    onPrimary = Color(0xFF004D40),
    primaryContainer = Color(0xFF00695C),
    onPrimaryContainer = Color(0xFFE0F2F1),
    secondary = Color(0xFF4DB6AC),
    onSecondary = Color(0xFF004D40),
    background = Color(0xFF0E1614),
    onBackground = Color(0xFFE0E3E1),
    surface = Color(0xFF16221E),
    onSurface = Color(0xFFE0E3E1),
    surfaceVariant = Color(0xFF1A352E),
    onSurfaceVariant = Color(0xFF80CBC4),
    outline = Color(0xFF26A69A),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// Ocean Palette (beautiful blues)
private val OceanLight = lightColorScheme(
    primary = Color(0xFF0288D1),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE1F5FE),
    onPrimaryContainer = Color(0xFF01579B),
    secondary = Color(0xFF0097A7),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF4F9FC),
    onBackground = Color(0xFF0E1317),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0E1317),
    surfaceVariant = Color(0xFFD6EEFA),
    onSurfaceVariant = Color(0xFF01579B),
    outline = Color(0xFF81D4FA),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)
private val OceanDark = darkColorScheme(
    primary = Color(0xFF81D4FA),
    onPrimary = Color(0xFF01579B),
    primaryContainer = Color(0xFF0277BD),
    onPrimaryContainer = Color(0xFFE1F5FE),
    secondary = Color(0xFF80DEEA),
    onSecondary = Color(0xFF006064),
    background = Color(0xFF0E1317),
    onBackground = Color(0xFFE1E2E5),
    surface = Color(0xFF161E24),
    onSurface = Color(0xFFE1E2E5),
    surfaceVariant = Color(0xFF193240),
    onSurfaceVariant = Color(0xFF81D4FA),
    outline = Color(0xFF29B6F6),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// Cherry Palette (gorgeous soft rose/red)
private val CherryLight = lightColorScheme(
    primary = Color(0xFFC2185B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFCE4EC),
    onPrimaryContainer = Color(0xFF880E4F),
    secondary = Color(0xFFD81B60),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFFF8FA),
    onBackground = Color(0xFF1A0F11),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A0F11),
    surfaceVariant = Color(0xFFFAD2E1),
    onSurfaceVariant = Color(0xFF880E4F),
    outline = Color(0xFFF48FB1),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)
private val CherryDark = darkColorScheme(
    primary = Color(0xFFF48FB1),
    onPrimary = Color(0xFF880E4F),
    primaryContainer = Color(0xFFAD1457),
    onPrimaryContainer = Color(0xFFFCE4EC),
    secondary = Color(0xFFF06292),
    onSecondary = Color(0xFF5C0029),
    background = Color(0xFF170F11),
    onBackground = Color(0xFFE9E0E2),
    surface = Color(0xFF24161B),
    onSurface = Color(0xFFE9E0E2),
    surfaceVariant = Color(0xFF401F29),
    onSurfaceVariant = Color(0xFFF48FB1),
    outline = Color(0xFFEC407A),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  themePreset: String = "Classic",
  // Branded indigo theme by default, let's keep dynamicColor as false for pristine design presentation
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when (themePreset) {
      "Lavender" -> if (darkTheme) LavenderDark else LavenderLight
      "Mint" -> if (darkTheme) MintDark else MintLight
      "Ocean" -> if (darkTheme) OceanDark else OceanLight
      "Cherry" -> if (darkTheme) CherryDark else CherryLight
      else -> {
        when {
          dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
          }

          darkTheme -> DarkColorScheme
          else -> LightColorScheme
        }
      }
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
