package com.example.mviarchitectureapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = TealLight,
    onPrimaryContainer = NavyDark,
    secondary = Teal,
    onSecondary = Color.White,
    background = SoftBackground,
    onBackground = NavyDark,
    surface = CardSurface,
    onSurface = NavyDark,
    surfaceVariant = Color(0xFFE4EBF0),
    onSurfaceVariant = MutedText,
    error = DebitRed,
)

@Composable
fun BankingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content,
    )
}
