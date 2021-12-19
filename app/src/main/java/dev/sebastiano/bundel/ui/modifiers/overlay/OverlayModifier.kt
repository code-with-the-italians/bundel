package dev.sebastiano.bundel.ui.modifiers.overlay

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer

@Suppress("MagicNumber")
internal fun Modifier.animatedOverlay(animatedOverlay: AnimatedOverlay) = this.then(
    Modifier
        .graphicsLayer {
            // This is required to render to an offscreen buffer
            // The Clear blend mode will not work without it
            alpha = 0.99999f
        }
        .drawWithContent {
            drawContent()
            animatedOverlay.drawOverlay(this)
        }
)
