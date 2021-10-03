package dev.sebastiano.bundel.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun BundelTheme(
    darkModeOverride: Boolean? = null,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = bundelColors(darkModeOverride),
        typography = bundelTypography,
        // some people just want to watch the world burn
        shapes = MaterialTheme.shapes.copy(large = MaterialTheme.shapes.small)
    ) {
        content()
    }
}

@Composable
internal fun BundelOnboardingTheme(
    darkModeOverride: Boolean? = null,
    content: @Composable () -> Unit
) {
    MaterialTheme(bundelOnboardingColors(darkModeOverride), bundelTypography) {
        content()
    }
}
