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

private val TindaDarkColorScheme =
  darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    secondary = EmeraldInteractive,
    onSecondary = EmeraldOnPrimary,
    secondaryContainer = BrandSurfaceElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = WarningAmber,
    onTertiary = TextPrimary,
    background = BrandBackground,
    onBackground = TextPrimary,
    surface = BrandSurfacePrimary,
    onSurface = TextPrimary,
    surfaceVariant = BrandSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderElevated,
    outlineVariant = BorderSubtle,
    error = DangerSoftRed,
    onError = EmeraldOnPrimary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = TindaDarkColorScheme,
    typography = Typography,
    content = content
  )
}
