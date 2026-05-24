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
import androidx.compose.ui.unit.dp

private val DarkColorScheme =
  darkColorScheme(
    primary = BentoPrimary,
    secondary = BentoSecondaryContainer,
    background = BentoDarkSurface,
    surface = BentoDarkSurface,
    onPrimary = Color.White,
    onSecondary = BentoOnSecondaryContainer,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = BentoOutline,
    surfaceVariant = BentoBackground,
    onSurfaceVariant = Color.Gray
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BentoPrimary,
    secondary = BentoSecondaryContainer,
    background = BentoBackground,
    surface = BentoSurface,
    onPrimary = Color.White,
    onSecondary = BentoOnSecondaryContainer,
    onBackground = Color.Black,
    onSurface = Color.Black,
    outline = BentoOutline,
    surfaceVariant = BentoSecondaryContainer,
    onSurfaceVariant = BentoOnSecondaryContainer
  )

@Composable
fun Theme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
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

  MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      shapes = androidx.compose.material3.Shapes(
          extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
          small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
          medium = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
          large = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
          extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
      ),
      content = content
  )
}
