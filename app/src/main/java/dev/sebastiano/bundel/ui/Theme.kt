package dev.sebastiano.bundel.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme as Material3MaterialTheme
import androidx.glance.LocalContext as GlanceLocalContext

@Composable
fun BundelYouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    context: Context = LocalContext.current,
    content: @Composable () -> Unit
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(context)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(context)
        darkTheme -> DarkThemeColors
        else -> LightThemeColors
    }

    BundelTheme(darkTheme = darkTheme) {
        Material3MaterialTheme(
            colorScheme = colorScheme,
            typography = BundelYouTypography,
            content = content
        )
    }
}

@Composable
internal fun BundelGlanceTheme(
    glanceContext: Context = GlanceLocalContext.current,
    darkTheme: Boolean = glanceContext.isDarkTheme,
    content: @Composable () -> Unit
) {
    BundelYouTheme(darkTheme, glanceContext, content)
}

private val Context.isDarkTheme: Boolean
    get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

@Composable
internal fun BundelTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = bundelColors(darkTheme),
        typography = bundelTypography,
        // some people just want to watch the world burn
        shapes = MaterialTheme.shapes.copy(large = MaterialTheme.shapes.small),
        content = content
    )
}
