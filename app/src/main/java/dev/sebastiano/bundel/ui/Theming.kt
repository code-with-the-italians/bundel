package dev.sebastiano.bundel.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun BundelTheme(
    darkModeOverride: Boolean? = null,
    content: @Composable () -> Unit
) {
    MaterialTheme(bundelColors(darkModeOverride), bundelTypography) {
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
