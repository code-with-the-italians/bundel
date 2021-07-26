@file:OptIn(ExperimentalAnimationApi::class)

package dev.sebastiano.bundel.onboarding

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.preferences.schedule.ExpandedRangeExtremity
import dev.sebastiano.bundel.preferences.schedule.PartOfHour
import dev.sebastiano.bundel.preferences.schedule.TimePickerModel
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.ui.BundelOnboardingTheme
import dev.sebastiano.bundel.ui.singlePadding
import java.time.LocalTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

@Suppress("MagicNumber") // It's a preview
@Preview(name = "Inactive")
@Composable
fun TimeRangeRowInactivePreview() {
    BundelOnboardingTheme {
        Surface {
            TimeRangeRow(
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
        }
    }
}

@Suppress("MagicNumber") // It's a preview
@Preview(name = "Active")
@Composable
fun TimeRangeRowActivePreview() {
    BundelOnboardingTheme {
        Surface {
            TimeRangeRow(
                modifier = Modifier.fillMaxWidth(),
                timeRange = TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 30)),
                enabled = true,
                canBeRemoved = true
            )
        }
    }
}

@Composable
internal fun TimeRangeRow(
    modifier: Modifier = Modifier,
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
        var expanded by remember { mutableStateOf(ExpandedRangeExtremity.NONE) }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemoveIcon(canBeRemoved, timeRange, onRemoved)

            Text(text = "From")

            Spacer(modifier = Modifier.width(singlePadding()))

            val expandedPillColor = MaterialTheme.colors.primary
            val normalPillColor = MaterialTheme.colors.onSurface

            TimePillButton(
                text = timeRange?.let { timeFormatter.format(timeRange.from) },
                pillBackgroundColor = if (expanded == ExpandedRangeExtremity.FROM) expandedPillColor else normalPillColor,
                enabled = enabled
            ) { expanded = if (expanded != ExpandedRangeExtremity.FROM) ExpandedRangeExtremity.FROM else ExpandedRangeExtremity.NONE }

            Spacer(modifier = Modifier.width(singlePadding()))

            Text(text = "to")

            Spacer(modifier = Modifier.width(singlePadding()))

            TimePillButton(
                text = timeRange?.let { timeFormatter.format(timeRange.to) },
                pillBackgroundColor = if (expanded == ExpandedRangeExtremity.TO) expandedPillColor else normalPillColor,
                enabled = enabled
            ) { expanded = if (expanded != ExpandedRangeExtremity.TO) ExpandedRangeExtremity.TO else ExpandedRangeExtremity.NONE }
        }

        ExpandableTimePicker(expanded, timeRange, minimumAllowableFrom, maximumAllowableTo, onTimeRangeChanged)
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
    timeRange: TimeRange?,
    minimumAllowableFrom: LocalTime?,
    maximumAllowableTo: LocalTime?,
    onTimeRangeChanged: (TimeRange) -> Unit
) {
    AnimatedVisibility(visible = expanded != ExpandedRangeExtremity.NONE) {
        val backgroundColor = MaterialTheme.colors.secondary
        checkNotNull(timeRange) { "The time picker is only available when the timeRange is not null" }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(start = 48.dp + singlePadding(), top = 4.dp, end = 8.dp, bottom = 4.dp),
            backgroundColor = backgroundColor
        ) {
            TimePicker(
                expanded = expanded,
                timeRange = timeRange,
                contentColor = contentColorFor(backgroundColor),
                minimumAllowableFrom = minimumAllowableFrom,
                maximumAllowableTo = maximumAllowableTo,
                onTimeRangeChanged = onTimeRangeChanged
            )
        }
    }
}

@Composable
private fun TimePicker(
    expanded: ExpandedRangeExtremity,
    timeRange: TimeRange,
    contentColor: Color,
    minimumAllowableFrom: LocalTime? = null,
    maximumAllowableTo: LocalTime? = null,
    onTimeRangeChanged: (TimeRange) -> Unit
) {
    val hourOfDay = if (expanded == ExpandedRangeExtremity.FROM) timeRange.from else timeRange.to

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val textStyle = MaterialTheme.typography.h2
        var selectedPart by remember { mutableStateOf(PartOfHour.HOUR) }

        val selectedPartColor = MaterialTheme.colors.primary

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
    pillBackgroundColor: Color = MaterialTheme.colors.onSurface,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(targetValue = pillBackgroundColor)

    val buttonColors = if (text != null) {
        ButtonDefaults.buttonColors(backgroundColor = backgroundColor)
    } else {
        ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            disabledBackgroundColor = backgroundColor.copy(alpha = .12f)
                .compositeOver(MaterialTheme.colors.surface),
            contentColor = Color.Transparent,
            disabledContentColor = Color.Transparent,
        )
    }
    Button(
        shape = CircleShape,
        colors = buttonColors,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
        enabled = enabled,
        onClick = { onClick() }
    ) {
        // HACK we should be using tabular numbers on the text instead
        Box(contentAlignment = Alignment.Center) {
            Text(text = text ?: "00:00")
            Text(text = "00:00", color = Color.Transparent)
        }
    }
}
