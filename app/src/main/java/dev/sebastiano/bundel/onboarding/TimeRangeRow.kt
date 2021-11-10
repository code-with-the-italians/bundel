@file:OptIn(ExperimentalAnimationApi::class)

package dev.sebastiano.bundel.onboarding

import android.content.res.Configuration
import androidx.annotation.Px
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.composables.MaterialPill
import dev.sebastiano.bundel.composables.MaterialPillAppearance
import dev.sebastiano.bundel.composables.checkedMaterialPillAppearance
import dev.sebastiano.bundel.preferences.schedule.ExpandedRangeExtremity
import dev.sebastiano.bundel.preferences.schedule.PartOfHour
import dev.sebastiano.bundel.preferences.schedule.TimePickerModel
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.ui.BundelYouTheme
import dev.sebastiano.bundel.ui.singlePadding
import dev.sebastiano.bundel.util.appendIf
import java.time.LocalTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

@Suppress("unused")
internal class OnboardingPreviews {

    @Suppress("MagicNumber") // It's a preview
    @Preview(name = "Inactive", group = "Onboarding")
    @Preview(name = "Inactive Night", group = "Onboarding", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TimeRangeRowOnboardingInactivePreview() {
        BundelYouTheme {
            Surface {
                TimeRangeRow(
                    modifier = Modifier.fillMaxWidth(),
                    pickerBackgroundColor = MaterialTheme.colorScheme.secondary,
                    enabled = false
                )
            }
        }
    }

    @Suppress("MagicNumber") // It's a preview
    @Preview(name = "Active", group = "Onboarding")
    @Preview(name = "Active Night", group = "Onboarding", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TimeRangeRowOnboardingActivePreview() {
        BundelYouTheme {
            Surface {
                TimeRangeRow(
                    modifier = Modifier.fillMaxWidth(),
                    pickerBackgroundColor = MaterialTheme.colorScheme.secondary,
                    timeRange = TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 30)),
                    enabled = true,
                    canBeRemoved = true
                )
            }
        }
    }
}

@Suppress("unused")
internal class AppThemePreviews {

    @Suppress("MagicNumber") // It's a preview
    @Preview(name = "Inactive", group = "App theme")
    @Preview(name = "Inactive Night", group = "App theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TimeRangeRowInactivePreview() {
        BundelYouTheme {
            Surface {
                TimeRangeRow(
                    modifier = Modifier.fillMaxWidth(),
                    expandedPillAppearance = checkedMaterialPillAppearance(
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    normalPillAppearance = checkedMaterialPillAppearance(
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    enabled = false
                )
            }
        }
    }

    @Suppress("MagicNumber") // It's a preview
    @Preview(name = "Active", group = "App theme")
    @Preview(name = "Active Night", group = "App theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TimeRangeRowActivePreview() {
        BundelYouTheme {
            Surface {
                TimeRangeRow(
                    modifier = Modifier.fillMaxWidth(),
                    expandedPillAppearance = onboardingCheckedPillAppearance(),
                    normalPillAppearance = onboardingUncheckedPillAppearance(),
                    timeRange = TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 30)),
                    enabled = true,
                    canBeRemoved = true
                )
            }
        }
    }
}

@Composable
internal fun TimeRangeRow(
    modifier: Modifier = Modifier,
    expandedPillAppearance: MaterialPillAppearance = onboardingCheckedPillAppearance(),
    normalPillAppearance: MaterialPillAppearance = onboardingUncheckedPillAppearance(),
    pickerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    timeRange: TimeRange? = null,
    enabled: Boolean = true,
    canBeRemoved: Boolean = false,
    minimumAllowableFrom: LocalTime? = null,
    maximumAllowableTo: LocalTime? = null,
    onRemoved: (TimeRange) -> Unit = {},
    onTimeRangeChanged: (TimeRange) -> Unit = {}
) {
    val timeFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .toFormatter(LocalConfiguration.current.locales[0])

    Column(modifier = modifier) {
        var expandedExtremity by remember { mutableStateOf(ExpandedRangeExtremity.NONE) }
        @Px var fromPillCenterX by remember { mutableStateOf(0f) }
        @Px var toPillCenterX by remember { mutableStateOf(0f) }
        @Px var stemPosition by remember { mutableStateOf(0f) }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RemoveIcon(canBeRemoved, timeRange, onRemoved)

            Text(text = "From")

            Spacer(modifier = Modifier.width(singlePadding()))

            TimePillButton(
                text = timeRange?.let { timeFormatter.format(timeRange.from) },
                enabled = enabled,
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    fromPillCenterX = layoutCoordinates.positionInParent().x + layoutCoordinates.size.width / 2
                },
                pillAppearance = if (expandedExtremity == ExpandedRangeExtremity.FROM) expandedPillAppearance else normalPillAppearance,
            ) {
                expandedExtremity = if (expandedExtremity != ExpandedRangeExtremity.FROM) ExpandedRangeExtremity.FROM else ExpandedRangeExtremity.NONE
                stemPosition = fromPillCenterX
            }

            Spacer(modifier = Modifier.width(singlePadding()))

            Text(text = "to")

            Spacer(modifier = Modifier.width(singlePadding()))

            TimePillButton(
                text = timeRange?.let { timeFormatter.format(timeRange.to) },
                enabled = enabled,
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    toPillCenterX = layoutCoordinates.positionInParent().x + layoutCoordinates.size.width / 2
                },
                pillAppearance = if (expandedExtremity == ExpandedRangeExtremity.TO) expandedPillAppearance else normalPillAppearance,
            ) {
                expandedExtremity = if (expandedExtremity != ExpandedRangeExtremity.TO) ExpandedRangeExtremity.TO else ExpandedRangeExtremity.NONE
                stemPosition = toPillCenterX
            }
        }

        ExpandableTimePicker(
            expanded = expandedExtremity,
            stemPosition = stemPosition,
            backgroundColor = pickerBackgroundColor,
            timeRange = timeRange,
            minimumAllowableFrom = minimumAllowableFrom,
            maximumAllowableTo = maximumAllowableTo,
            onTimeRangeChanged = onTimeRangeChanged
        )
    }
}

@Composable
private fun RemoveIcon(
    canBeRemoved: Boolean,
    timeRange: TimeRange?,
    onRemoved: (TimeRange) -> Unit
) {
    AnimatedContent(
        targetState = canBeRemoved && timeRange != null,
        transitionSpec = {
            fadeIn(animationSpec = tween()) with fadeOut(animationSpec = tween())
        }
    ) { showRemoveAction ->
        if (showRemoveAction) {
            checkNotNull(timeRange) { "Time range can't be null when canBeRemoved == true" }

            IconButton(onClick = { onRemoved(timeRange) }) {
                Icon(Icons.Rounded.Clear, contentDescription = "Remove")
            }

            Spacer(modifier = Modifier.width(singlePadding()))
        } else {
            Box(Modifier.size(48.dp))
            Spacer(modifier = Modifier.width(singlePadding()))
        }
    }
}

@Composable
private fun ColumnScope.ExpandableTimePicker(
    expanded: ExpandedRangeExtremity,
    @Px stemPosition: Float,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    timeRange: TimeRange?,
    minimumAllowableFrom: LocalTime?,
    maximumAllowableTo: LocalTime?,
    onTimeRangeChanged: (TimeRange) -> Unit,
) {
    @Px var cardX by remember { mutableStateOf(0f) }

    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .onGloballyPositioned { layoutCoordinates -> cardX = layoutCoordinates.positionInParent().x },
        visible = expanded != ExpandedRangeExtremity.NONE
    ) {
        checkNotNull(timeRange) { "The time picker is only available when the timeRange is not null" }

        val stemSize = 16.dp
        val cornerRadius = 16.dp

        val startPadding = 48.dp
        val startPaddingPx = with(LocalDensity.current) { startPadding.toPx() }
        Card(
            modifier = Modifier.padding(start = startPadding),
            shape = SpeechBubbleShape(
                cornerRadius = cornerRadius,
                stemPosition = stemPosition - cardX - startPaddingPx,
                stemSize = stemSize
            ),
            elevation = 4.dp,
            backgroundColor = backgroundColor
        ) {
            TimePicker(
                expanded = expanded,
                timeRange = timeRange,
                contentColor = contentColorFor(backgroundColor),
                stemHeight = stemSize,
                minimumAllowableFrom = minimumAllowableFrom,
                maximumAllowableTo = maximumAllowableTo,
                onTimeRangeChanged = onTimeRangeChanged
            )
        }
    }
}

private class SpeechBubbleShape(
    private val cornerRadius: Dp,
    @Px private val stemPosition: Float,
    private val stemSize: Dp
) : Shape {

    private val roundedRect = Path()
    private val stem = Path()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = with(density) {
        Outline.Generic(createSpeechBubblePath(size, cornerRadius.toPx(), stemPosition, stemSize.toPx()))
    }

    private fun createSpeechBubblePath(
        size: Size,
        @Px cornerRadius: Float,
        @Px stemPosition: Float,
        @Px stemSize: Float
    ): Path {
        //       /\
        //      /  \
        //  ___/____\___
        // /            \
        // |            |
        // |            |
        // \------------/

        roundedRect.apply {
            reset()
            addRoundRect(
                RoundRect(
                    rect = Rect(0f, stemSize, size.width, size.height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            )
        }

        val adjustedStemPosition = kotlin.runCatching { stemPosition.coerceIn(cornerRadius, size.width - cornerRadius - stemSize / 2) }
            .getOrNull() ?: stemPosition
        stem.apply {
            reset()
            moveTo(adjustedStemPosition, 0f)
            lineTo(adjustedStemPosition - stemSize / 2, stemSize)
            lineTo(adjustedStemPosition + stemSize / 2, stemSize)
            close()
        }
        return Path.combine(PathOperation.Union, roundedRect, stem)
    }
}

@Composable
private fun TimePicker(
    expanded: ExpandedRangeExtremity,
    timeRange: TimeRange,
    contentColor: Color,
    stemHeight: Dp,
    minimumAllowableFrom: LocalTime? = null,
    maximumAllowableTo: LocalTime? = null,
    onTimeRangeChanged: (TimeRange) -> Unit
) {
    val hourOfDay = if (expanded == ExpandedRangeExtremity.FROM) timeRange.from else timeRange.to

    Row(
        modifier = Modifier.padding(start = 24.dp, top = stemHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val textStyle = MaterialTheme.typography.displayLarge
        var selectedPart by remember { mutableStateOf(PartOfHour.HOUR) }

        val selectedPartColor = MaterialTheme.colorScheme.primary

        val numbersSlidingAnimation: AnimatedContentScope<Int>.() -> ContentTransform = {
            if (initialState > targetState) {
                slideInVertically(initialOffsetY = { it }) + fadeIn() with slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            } else {
                slideInVertically(initialOffsetY = { -it }) + fadeIn() with slideOutVertically(targetOffsetY = { it }) + fadeOut()
            }
        }

        SelectableAnimatedHourPart(
            numbersSlidingAnimation = numbersSlidingAnimation,
            value = hourOfDay.hour,
            textStyle = textStyle,
            color = if (selectedPart == PartOfHour.HOUR) selectedPartColor else contentColor,
            onClick = { selectedPart = PartOfHour.HOUR }
        )

        Text(":", style = textStyle)

        SelectableAnimatedHourPart(
            numbersSlidingAnimation = numbersSlidingAnimation,
            value = hourOfDay.minute,
            textStyle = textStyle,
            color = if (selectedPart == PartOfHour.MINUTE) selectedPartColor else contentColor,
            onClick = { selectedPart = PartOfHour.MINUTE }
        )

        Spacer(modifier = Modifier.padding(start = singlePadding()))

        val modifiableTimeRange = TimePickerModel(timeRange, expanded, selectedPart, minimumAllowableFrom, maximumAllowableTo)
        UpDownButtons(modifiableTimeRange, onTimeRangeChanged)
    }
}

@Composable
private fun SelectableAnimatedHourPart(
    numbersSlidingAnimation: AnimatedContentScope<Int>.() -> ContentTransform,
    value: Int,
    textStyle: TextStyle,
    color: Color,
    onClick: () -> Unit
) {
    val partColor by animateColorAsState(
        targetValue = color
    )
    AnimatedContent(
        targetState = value,
        transitionSpec = numbersSlidingAnimation
    ) { hours ->
        Text(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
            text = hours.toString().padStart(2, '0'),
            style = textStyle,
            color = partColor
        )
    }
}

@Composable
private fun UpDownButtons(
    timePickerModel: TimePickerModel,
    onTimeRangeChanged: (TimeRange) -> Unit
) {
    Column {
        IconButton(
            enabled = timePickerModel.canIncrement,
            onClick = {
                val newTimeRange = timePickerModel.incrementTimeRangePart()
                onTimeRangeChanged(newTimeRange)
            }
        ) {
            Icon(Icons.Rounded.ArrowDropUp, contentDescription = stringResource(R.string.content_description_time_picker_increase))
        }

        IconButton(
            enabled = timePickerModel.canDecrement,
            onClick = {
                val newTimeRange = timePickerModel.decrementTimeRangePart()
                onTimeRangeChanged(newTimeRange)
            }
        ) {
            Icon(Icons.Rounded.ArrowDropDown, contentDescription = stringResource(R.string.content_description_time_picker_decrease))
        }
    }
}

@Composable
private fun TimePillButton(
    text: String?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    pillAppearance: MaterialPillAppearance,
    onClick: () -> Unit
) {
    MaterialPill(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .appendIf(enabled) { clickable(role = Role.Button, onClick = onClick) },
        appearance = pillAppearance
    ) {
        // HACK we should be using tabular numbers on the text instead
        Box(contentAlignment = Alignment.Center) {
            Text(text = text ?: "00:00")
            Text(text = "00:00", color = Color.Transparent)
        }
    }
}
