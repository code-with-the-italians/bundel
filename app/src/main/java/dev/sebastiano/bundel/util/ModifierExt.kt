package dev.sebastiano.bundel.util

import androidx.compose.ui.Modifier

internal fun Modifier.appendIf(condition: Boolean, transformer: Modifier.() -> Modifier): Modifier =
    if (!condition) this else transformer()
