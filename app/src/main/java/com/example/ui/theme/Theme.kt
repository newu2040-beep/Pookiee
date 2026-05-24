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

import com.example.PookieeTheme

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
  pookieeTheme: PookieeTheme = PookieeTheme.LAVENDER,
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val baseScheme = when (pookieeTheme) {
    PookieeTheme.LAVENDER -> LightColorScheme
    PookieeTheme.SAKURA -> LightColorScheme.copy(
        primary = Color(0xFFF06292),
        secondaryContainer = Color(0xFFFCE4EC),
        onSecondaryContainer = Color(0xFF880E4F)
    )
    PookieeTheme.MINT -> LightColorScheme.copy(
        primary = Color(0xFF4DB6AC),
        secondaryContainer = Color(0xFFE0F2F1),
        onSecondaryContainer = Color(0xFF004D40)
    )
    PookieeTheme.PEACH -> LightColorScheme.copy(
        primary = Color(0xFFFF8A65),
        secondaryContainer = Color(0xFFFBE9E7),
        onSecondaryContainer = Color(0xFFBF360C)
    )
    PookieeTheme.OCEAN -> LightColorScheme.copy(
        primary = Color(0xFF42A5F5),
        secondaryContainer = Color(0xFFE3F2FD),
        onSecondaryContainer = Color(0xFF0D47A1)
    )
    PookieeTheme.SUNSET -> LightColorScheme.copy(
        primary = Color(0xFFFF7043),
        secondaryContainer = Color(0xFFFFF3E0),
        onSecondaryContainer = Color(0xFFE64A19)
    )
    PookieeTheme.ARCTIC -> LightColorScheme.copy(
        primary = Color(0xFF26C6DA),
        secondaryContainer = Color(0xFFE0F7FA),
        onSecondaryContainer = Color(0xFF006064)
    )
  }

  val colorScheme = if (darkTheme) {
    DarkColorScheme.copy(
        primary = baseScheme.primary,
        secondaryContainer = baseScheme.primary.copy(alpha = 0.2f),
        onSecondaryContainer = baseScheme.primary,
        background = BentoDarkSurface,
        surface = BentoDarkSurface
    )
  } else {
    baseScheme
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
