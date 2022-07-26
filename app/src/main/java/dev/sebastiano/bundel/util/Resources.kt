@file:Suppress("ktlint:filename")

package dev.sebastiano.bundel.util

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
@ReadOnlyComposable
fun pluralsResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
    val resources = resources()
    return resources.getQuantityString(id, quantity, *formatArgs)
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    // Copied from Compose itself. No idea why it does what it does.
    LocalConfiguration.current
    return LocalContext.current.resources
}
