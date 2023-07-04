@file:Suppress("ktlint:filename")

package dev.sebastiano.bundel.ui.modifiers.snowfall

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.appcompat.content.res.AppCompatResources
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rotateRad
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import dev.sebastiano.bundel.ui.R
import kotlinx.coroutines.isActive
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("MagicNumber") // Default values...
fun Modifier.snowfall(
    heightRange: ClosedRange<Float> = 8f..17f,
    incrementFactorRange: ClosedRange<Float> = 0.4f..0.8f,
    fallAngleSeed: Float = 25f,
    fallAngleSeedRange: ClosedRange<Float> = -fallAngleSeed..fallAngleSeed,
    fallAngleVariance: Float = .1f,
    fallAngleDivider: Float = 10_000f,
    rotationAngleRadSeed: Float = 45f,
    rotationSpeedRadPerTick: ClosedRange<Float> = -0.05f..0.05f,
    @FloatRange(from = 0.0, to = 1.0) snowDensity: Float = .04f
): Modifier = composed {
    val context = LocalContext.current
    var fluffyState by remember {
        mutableStateOf(
            SnowfallState(
                snowflakes = emptyList(),
                heightRange = heightRange,
                incrementFactorRange = incrementFactorRange,
                fallAngleSeed = fallAngleSeed,
                fallAngleSeedRange = fallAngleSeedRange,
                fallAngleVariance = fallAngleVariance,
                fallAngleDivider = fallAngleDivider,
                rotationAngleRadSeed = rotationAngleRadSeed,
                rotationSpeedRadPerTick = rotationSpeedRadPerTick,
                snowDensity = snowDensity,
                context = context
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
    val heightRange: ClosedRange<Float>,
    val incrementFactorRange: ClosedRange<Float>,
    val fallAngleSeed: Float,
    val fallAngleSeedRange: ClosedRange<Float>,
    val fallAngleVariance: Float,
    val fallAngleDivider: Float,
    val rotationAngleRadSeed: Float,
    val rotationSpeedRadPerTick: ClosedRange<Float>,
    @FloatRange(from = 0.0, to = 1.0) val snowDensity: Float,
    val context: Context
) {

    init {
        require(snowDensity in 0f..1f) { "Snow density must be between 0f and 1f, inclusive" }
    }

    fun resize(size: IntSize): SnowfallState = copy(
        snowflakes = createSnowflakes(
            canvasSize = size,
            displayDensity = context.resources.displayMetrics.density,
            heightRange = heightRange,
            incrementFactorRange = incrementFactorRange,
            fallAngleRadSeed = fallAngleSeed,
            fallAngleRadSeedRange = fallAngleSeedRange,
            fallAngleVariance = fallAngleVariance,
            fallAngleDivider = fallAngleDivider,
            rotationAngleRadSeed = rotationAngleRadSeed,
            rotationSpeedRadPerTick = rotationSpeedRadPerTick,
            snowDensity = snowDensity
        ) {
            checkNotNull(AppCompatResources.getDrawable(context, it)) { "Snowflake drawable not found" }
                .apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        }
    )

    companion object {

        private const val DENSITY_DIVIDER = 500f

        private fun createSnowflakes(
            canvasSize: IntSize,
            displayDensity: Float,
            heightRange: ClosedRange<Float>,
            incrementFactorRange: ClosedRange<Float>,
            fallAngleRadSeed: Float,
            fallAngleRadSeedRange: ClosedRange<Float>,
            fallAngleVariance: Float,
            fallAngleDivider: Float,
            rotationAngleRadSeed: Float,
            rotationSpeedRadPerTick: ClosedRange<Float>,
            snowDensity: Float,
            snowflakeDrawableProvider: (id: Int) -> Drawable
        ): List<Snowflake> {
            val snowflakesCount = (canvasSize.area * snowDensity / DENSITY_DIVIDER).roundToInt()
            return List(snowflakesCount) {
                Snowflake(
                    drawable = snowflakeDrawableProvider(SnowflakeDrawable.pick().id),
                    height = heightRange.random() * displayDensity,
                    position = canvasSize.randomPosition(),
                    fallAngleRad = fallAngleRadSeed.random() / fallAngleRadSeed * fallAngleVariance + Math.PI.toFloat() / 2f - fallAngleVariance / 2f,
                    fallAngleSeedRadRange = fallAngleRadSeedRange,
                    fallAngleDivider = fallAngleDivider,
                    rotationAngleRad = rotationAngleRadSeed.random(),
                    rotationSpeedRadPerTick = rotationSpeedRadPerTick.random(),
                    incrementFactor = incrementFactorRange.random(),
                    canvasSize = canvasSize
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

@Suppress("unused")
private enum class SnowflakeDrawable(@DrawableRes val id: Int) {

    BERT(R.drawable.snowflake01), // Short for Englebert
    OLAF(R.drawable.snowflake02),
    PIPPO(R.drawable.snowflake03),
    FRANCO(R.drawable.snowflake04);

    companion object {

        fun pick() = values().random()
    }
}

private class Snowflake(
    private val drawable: Drawable,
    private val height: Float,
    private val canvasSize: IntSize,
    private val incrementFactor: Float,
    private val fallAngleSeedRadRange: ClosedRange<Float>,
    private val fallAngleDivider: Float,
    private val rotationSpeedRadPerTick: Float,
    position: Offset,
    fallAngleRad: Float,
    rotationAngleRad: Float
) {

    private var position by mutableStateOf(position)
    private var fallAngleRad by mutableStateOf(fallAngleRad)
    private var rotationAngleRad by mutableStateOf(rotationAngleRad)

    fun draw(canvas: Canvas) {
        // TODO draw shadow
        canvas.withSave {
            val halfWidth = drawable.intrinsicWidth / 2f
            val halfHeight = drawable.intrinsicHeight / 2f

            canvas.translate(position.x, position.y)
            canvas.scale(height / drawable.intrinsicHeight)
            canvas.rotateRad(rotationAngleRad, halfWidth, halfHeight)

            drawable.draw(canvas.nativeCanvas)
        }
    }

    fun update(elapsedMillis: Long) {
        val increment = incrementFactor * (elapsedMillis / frameDurationAt60Fps) * baseSpeedAt60Fps

        val deltaX = increment * cos(fallAngleRad)
        val deltaY = increment * sin(fallAngleRad)
        position = Offset(position.x + deltaX, position.y + deltaY)

        fallAngleRad += fallAngleSeedRadRange.random() / fallAngleDivider

        rotationAngleRad += rotationSpeedRadPerTick
        if (rotationAngleRad > 2 * Math.PI) rotationAngleRad = 0f

        if (position.y - height > canvasSize.height) {
            position = position.copy(y = -height)
        }
        if (position.x < -height || position.x > canvasSize.width + height) {
            position = position.copy(x = canvasSize.width.random().toFloat(), y = -height)
        }
    }

    companion object {

        private const val frameDurationAt60Fps = 16f
        private const val baseSpeedAt60Fps = 2f
    }
}
