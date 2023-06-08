package dev.sebastiano.bundel.ui.modifiers.overlay

import androidx.compose.ui.graphics.drawscope.DrawScope

interface AnimatedOverlay {

    fun drawOverlay(drawScope: DrawScope)
}
