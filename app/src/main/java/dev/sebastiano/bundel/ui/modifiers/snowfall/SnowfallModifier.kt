package dev.sebastiano.bundel.ui.modifiers.snowfall

import androidx.annotation.FloatRange
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.isActive
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

internal fun Modifier.rudolf(
    sizeRange: ClosedRange<Float> = 5f..12f,
    incrementFactorRange: ClosedRange<Float> = 0.4f..0.8f,
    angleSeed: Float = 25f,
    angleSeedRange: ClosedRange<Float> = -angleSeed..angleSeed,
    angleVariance: Float = .1f,
    angleDivider: Float = 10_000f,
    @FloatRange(from = 0.0, to = 1.0) density: Float = .1f,
    snowflakeColor: Color = Color.White
): Modifier = composed {
    var fluffyState by remember {
        mutableStateOf(
            SnowfallState(
                snowflakes = emptyList(),
                sizeRange = sizeRange,
                incrementFactorRange = incrementFactorRange,
                angleSeed = angleSeed,
                angleSeedRange = angleSeedRange,
                angleVariance = angleVariance,
                angleDivider = angleDivider,
                density = density,
                color = snowflakeColor
            )
        )
    }
    var lastTick by remember { mutableStateOf(-1L) }

    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameMillis { newTick ->
                val elapsedMillis = newTick - lastTick
                val wasFirstFrame = lastTick < 0
                lastTick = newTick
                if (wasFirstFrame) return@withFrameMillis

                for (snowflake in fluffyState.snowflakes) {
                    snowflake.update(elapsedMillis)
                }
            }
        }
    }

    onSizeChanged { size -> fluffyState = fluffyState.resize(size) }
        .clipToBounds()
        .drawWithContent {
            drawContent()
            val canvas = drawContext.canvas
            for (snowflake in fluffyState.snowflakes) {
                snowflake.draw(canvas)
            }
        }
}

private data class SnowfallState(
    val snowflakes: List<Snowflake>,
    val sizeRange: ClosedRange<Float> = 5f..12f,
    val incrementFactorRange: ClosedRange<Float> = 0.4f..0.8f,
    val angleSeed: Float = 25f,
    val angleSeedRange: ClosedRange<Float> = -angleSeed..angleSeed,
    val angleVariance: Float = .1f,
    val angleDivider: Float = 10_000f,
    @FloatRange(from = 0.0, to = 1.0) val density: Float = .1f,
    val color: Color
) {

    init {
        require(density in 0f..1f) { "Density must be between 0f and 1f, inclusive" }
    }

    fun resize(size: IntSize): SnowfallState = copy(
        snowflakes = createSnowflakes(size, sizeRange, incrementFactorRange, angleSeed, angleSeedRange, angleVariance, angleDivider, density, color)
    )

    companion object {

        private const val DENSITY_DIVIDER = 500f

        private fun createSnowflakes(
            canvasSize: IntSize,
            sizeRange: ClosedRange<Float>,
            incrementFactorRange: ClosedRange<Float>,
            angleSeed: Float,
            angleSeedRange: ClosedRange<Float>,
            angleVariance: Float,
            angleDivider: Float,
            density: Float,
            color: Color
        ): List<Snowflake> {
            val snowflakesCount = (canvasSize.area * density / DENSITY_DIVIDER).roundToInt()
            return List(snowflakesCount) {
                Snowflake(
                    radius = sizeRange.random(),
                    position = canvasSize.randomPosition(),
                    angle = angleSeed.random() / angleSeed * angleVariance + Math.PI.toFloat() / 2f - angleVariance / 2f,
                    angleSeedRange = angleSeedRange,
                    angleDivider = angleDivider,
                    incrementFactor = incrementFactorRange.random(),
                    canvasSize = canvasSize,
                    paintColor = color
                )
            }
        }

        private fun IntSize.randomPosition() = Offset(width.random().toFloat(), height.random().toFloat())

        private val IntSize.area: Int
            get() = width * height
    }
}

private fun ClosedRange<Float>.random() = ThreadLocalRandom.current().nextFloat() * (endInclusive - start) + start

private fun Int.random(): Int = ThreadLocalRandom.current().nextInt(this)

private fun Float.random(): Float = ThreadLocalRandom.current().nextFloat() * this

private class Snowflake(
    private val radius: Float,
    private val canvasSize: IntSize,
    private val incrementFactor: Float,
    private val angleSeedRange: ClosedRange<Float>,
    private val angleDivider: Float,
    position: Offset,
    angle: Float,
    paintColor: Color
) {

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        color = paintColor
        style = PaintingStyle.Fill
        alpha = incrementFactor
    }

    private var position by mutableStateOf(position)
    private var angle by mutableStateOf(angle)

    fun draw(canvas: Canvas) {
        canvas.drawCircle(center = position, radius = radius, paint = paint)
    }

    fun update(elapsedMillis: Long) {
        val increment = incrementFactor * (elapsedMillis / frameDurationAt60Fps) * baseSpeedAt60Fps

        val deltaX = increment * cos(angle)
        val deltaY = increment * sin(angle)
        position = Offset(position.x + deltaX, position.y + deltaY)

        angle += angleSeedRange.random() / angleDivider

        if (position.y - radius > canvasSize.height) {
            position = position.copy(y = -radius)
        }
        if (position.x < -radius || position.x > canvasSize.width + radius) {
            position = position.copy(x = canvasSize.width.random().toFloat(), y = -radius)
        }
    }

    companion object {

        private const val frameDurationAt60Fps = 16f
        private const val baseSpeedAt60Fps = 2f
    }
}
