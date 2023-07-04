@file:Suppress("ktlint:filename")

package dev.sebastiano.bundel.ui.modifiers

import androidx.compose.ui.Modifier

fun Modifier.appendIf(condition: Boolean, transformer: Modifier.() -> Modifier): Modifier =
    if (!condition) this else transformer()
