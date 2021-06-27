@file:OptIn(ExperimentalAnimationApi::class)

package dev.sebastiano.bundel.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.BundelOnboardingTheme
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.singlePadding
import java.time.LocalTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

private enum class ExpandedTime {
    NONE,
    FROM,
    TO
}

private enum class SelectedPartOfHour {
    HOUR,
    MINUTE
}

@Preview(name = "Inactive", backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
fun TimeRangeRowInactivePreview() {
    BundelOnboardingTheme {
        TimeRangeRow(
            enabled = false
        )
    }
}

@Preview(name = "Active", backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
fun TimeRangeRowActivePreview() {
    BundelOnboardingTheme {
        TimeRangeRow(
            timeRange = TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 30)),
            enabled = true,
            canBeRemoved = true
        )
    }
}

@Composable
internal fun TimeRangeRow(
    timeRange: TimeRange? = null,
    enabled: Boolean = true,
    canBeRemoved: Boolean = false,
    onRemoved: ((TimeRange) -> Unit)? = {},
    onTimeRangeChanged: (TimeRange) -> Unit = {}
) {
    val timeFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .toFormatter(LocalConfiguration.current.locales[0])

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var expanded by remember { mutableStateOf(ExpandedTime.NONE) }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canBeRemoved && timeRange != null) {
                checkNotNull(onRemoved) { "Time range with canBeRemoved true requires a onRemove callback" }
                IconButton(onClick = { onRemoved(timeRange) }) {
                    Icon(Icons.Rounded.Clear, contentDescription = "Remove")
                }

                Spacer(modifier = Modifier.width(singlePadding()))
            } else {
                Box(Modifier.size(48.dp))
                Spacer(modifier = Modifier.width(singlePadding()))
            }

            Text(text = "From")

            Spacer(modifier = Modifier.width(singlePadding()))

            val expandedPillColor = MaterialTheme.colors.primary
            val normalPillColor = MaterialTheme.colors.onSurface

            TimePillButton(
                text = timeRange?.let { timeFormatter.format(timeRange.from) } ?: "",
                pillBackgroundColor = if (expanded == ExpandedTime.FROM) expandedPillColor else normalPillColor,
                enabled = enabled
            ) { expanded = if (expanded != ExpandedTime.FROM) ExpandedTime.FROM else ExpandedTime.NONE }

            Spacer(modifier = Modifier.width(singlePadding()))

            Text(text = "to")

            Spacer(modifier = Modifier.width(singlePadding()))

            TimePillButton(
                text = timeRange?.let { timeFormatter.format(timeRange.to) } ?: "",
                pillBackgroundColor = if (expanded == ExpandedTime.TO) expandedPillColor else normalPillColor,
                enabled = enabled
            ) { expanded = if (expanded != ExpandedTime.TO) ExpandedTime.TO else ExpandedTime.NONE }
        }

        AnimatedVisibility(visible = expanded != ExpandedTime.NONE) {
            val backgroundColor = MaterialTheme.colors.secondary
            checkNotNull(timeRange) { "The time picker is only available when the timeRange is not null" }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp + singlePadding(), top = 4.dp, end = 8.dp, bottom = 4.dp),
                backgroundColor = backgroundColor
            ) {
                TimePicker(
                    expanded = expanded,
                    timeRange = timeRange,
                    contentColor = contentColorFor(backgroundColor),
                    onTimeRangeChanged = onTimeRangeChanged
                )
            }
        }
    }
}

@Composable
private fun TimePicker(
    expanded: ExpandedTime,
    timeRange: TimeRange,
    contentColor: Color,
    onTimeRangeChanged: (TimeRange) -> Unit
) {
    val hourOfDay = if (expanded == ExpandedTime.FROM) timeRange.from else timeRange.to

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val textStyle = MaterialTheme.typography.h2
        var selectedPart by remember { mutableStateOf(SelectedPartOfHour.HOUR) }

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
            color = if (selectedPart == SelectedPartOfHour.HOUR) selectedPartColor else contentColor,
            onClick = { selectedPart = SelectedPartOfHour.HOUR }
        )

        Text(":", style = textStyle)

        SelectableAnimatedHourPart(
            numbersSlidingAnimation = numbersSlidingAnimation,
            value = hourOfDay.minute,
            textStyle = textStyle,
            color = if (selectedPart == SelectedPartOfHour.MINUTE) selectedPartColor else contentColor,
            onClick = { selectedPart = SelectedPartOfHour.MINUTE }
        )

        Spacer(modifier = Modifier.padding(start = singlePadding()))

        UpDownButtons(timeRange, expanded, selectedPart, onTimeRangeChanged)
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
    timeRange: TimeRange,
    expanded: ExpandedTime,
    selectedPart: SelectedPartOfHour,
    onTimeRangeChanged: (TimeRange) -> Unit
) {
    Column {
        IconButton(
            onClick = {
                val newTimeRange = timeRange.copy(
                    from = if (expanded == ExpandedTime.FROM) {
                        if (selectedPart == SelectedPartOfHour.HOUR) {
                            timeRange.from.plusHours(1)
                        } else {
                            timeRange.from.plusMinutes(1)
                        }
                    } else timeRange.from,
                    to = if (expanded == ExpandedTime.TO) {
                        if (selectedPart == SelectedPartOfHour.HOUR) {
                            timeRange.to.plusHours(1)
                        } else {
                            timeRange.to.plusMinutes(1)
                        }
                    } else timeRange.to
                )
                onTimeRangeChanged(newTimeRange)
            }
        ) {
            Icon(Icons.Rounded.ArrowDropUp, contentDescription = "One more!")
        }

        IconButton(
            onClick = {
                val newTimeRange = timeRange.copy(
                    from = if (expanded == ExpandedTime.FROM) {
                        if (selectedPart == SelectedPartOfHour.HOUR) {
                            timeRange.from.minusHours(1)
                        } else {
                            timeRange.from.minusMinutes(1)
                        }
                    } else timeRange.from,
                    to = if (expanded == ExpandedTime.TO) {
                        if (selectedPart == SelectedPartOfHour.HOUR) {
                            timeRange.to.minusHours(1)
                        } else {
                            timeRange.to.minusMinutes(1)
                        }
                    } else timeRange.to
                )
                onTimeRangeChanged(newTimeRange)
            }
        ) {
            Icon(Icons.Rounded.ArrowDropDown, contentDescription = "One less!")
        }
    }
}

@Composable
private fun TimePillButton(
    text: String,
    pillBackgroundColor: Color = MaterialTheme.colors.onSurface,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(targetValue = pillBackgroundColor)

    Button(
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
        enabled = enabled,
        onClick = { onClick() }
    ) {
        Text(text = text)
    }
}
