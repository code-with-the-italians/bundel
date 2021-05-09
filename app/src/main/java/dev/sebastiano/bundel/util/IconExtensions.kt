package dev.sebastiano.bundel.util

import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap

@Composable
internal fun Icon.asImageBitmap(): ImageBitmap? =
    loadDrawable(LocalContext.current)
        ?.toBitmap()
        ?.asImageBitmap()
