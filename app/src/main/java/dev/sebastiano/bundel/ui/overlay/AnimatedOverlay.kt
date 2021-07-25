package dev.sebastiano.bundel.ui.overlay

import androidx.compose.ui.graphics.drawscope.DrawScope

internal interface AnimatedOverlay {

    fun drawOverlay(drawScope: DrawScope)
}
