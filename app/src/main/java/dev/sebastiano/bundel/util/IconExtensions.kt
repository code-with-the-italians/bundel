package dev.sebastiano.bundel.util

import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap

@Composable
internal fun Icon.asImageBitmap(): ImageBitmap =
    loadDrawable(LocalContext.current)
        ?.toBitmap()
        ?.asImageBitmap()
        ?: error("Unable to load drawable for icon $this")

@Composable
internal fun rememberIconPainter(icon: Icon?): Painter? {
    val bitmap = icon?.asImageBitmap()
    return remember(icon) { bitmap?.let { BitmapPainter(it) } }
}
