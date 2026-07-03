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

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Branded indigo theme by default, let's keep dynamicColor as false for pristine design presentation
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
