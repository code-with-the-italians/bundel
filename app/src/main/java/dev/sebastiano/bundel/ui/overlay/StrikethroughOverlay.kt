package dev.sebastiano.bundel.ui.overlay

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal class StrikethroughOverlay(
    private val color: Color = Color.Black,
    private var widthDp: Dp = 4.dp,
    private val getProgress: () -> Float
) : AnimatedOverlay {

    @Suppress("MagicNumber")
    override fun drawOverlay(drawScope: DrawScope) {
        with(drawScope) {
            val width = density.run { widthDp.toPx() }
            val halfWidth = width / 2f
            val progressHeight = size.height * getProgress()
            rotate(-45f) {
                drawLine(
                    color = color,
                    start = Offset(size.center.x + halfWidth, 0f),
                    end = Offset(size.center.x + halfWidth, progressHeight),
                    strokeWidth = width,
                    blendMode = BlendMode.Clear
                )
                drawLine(
                    color = color,
                    start = Offset(size.center.x - halfWidth, 0f),
                    end = Offset(size.center.x - halfWidth, progressHeight),
                    strokeWidth = width
                )
            }
        }
    }
}
