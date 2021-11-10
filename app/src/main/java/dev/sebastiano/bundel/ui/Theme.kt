package dev.sebastiano.bundel.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme as Material3MaterialTheme

@Composable
fun BundelYouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkThemeColors
        else -> LightThemeColors
    }

    BundelTheme {
        Material3MaterialTheme(
            colorScheme = colorScheme,
            typography = BundelYouTypography,
            content = content
        )
    }
}

@Composable
internal fun BundelTheme(
    darkModeOverride: Boolean? = null,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = bundelColors(darkModeOverride),
        typography = bundelTypography,
        // some people just want to watch the world burn
        shapes = MaterialTheme.shapes.copy(large = MaterialTheme.shapes.small),
        content = content
    )
}
