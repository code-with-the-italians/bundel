package dev.sebastiano.bundel.util

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
internal fun currentOrientation(): PembaaaOrientation =
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> PembaaaOrientation.Landscape
        else -> PembaaaOrientation.Portrait
    }
