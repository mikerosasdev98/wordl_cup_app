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

private val DarkColorScheme = darkColorScheme(
    primary = DarkBoldPrimary,
    secondary = DarkBoldSecondary,
    tertiary = DarkBoldTertiary,
    background = DarkBoldBg,
    surface = DarkBoldSurface,
    onPrimary = DarkBoldOnPrimary,
    onSecondary = DarkBoldOnSecondary,
    onBackground = DarkBoldOnBg,
    onSurface = DarkBoldOnSurface,
    surfaceVariant = DarkBoldSurfaceVariant,
    onSurfaceVariant = DarkBoldOnSurfaceVariant,
    outline = DarkBoldOutline
)

private val LightColorScheme = lightColorScheme(
    primary = BoldPrimary,
    secondary = BoldSecondary,
    tertiary = BoldTertiary,
    background = BoldBg,
    surface = BoldSurface,
    onPrimary = BoldOnPrimary,
    onSecondary = BoldOnSecondary,
    onBackground = BoldOnBg,
    onSurface = BoldOnSurface,
    surfaceVariant = BoldSurfaceVariant,
    onSurfaceVariant = BoldOnSurfaceVariant,
    outline = BoldOutline
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color disabled by default to force the custom "Bold Typography" design colors
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
