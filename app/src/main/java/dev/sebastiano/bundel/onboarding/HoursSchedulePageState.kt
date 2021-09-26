package dev.sebastiano.bundel.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.ui.BundelOnboardingTheme
import dev.sebastiano.bundel.ui.singlePadding
import dev.sebastiano.bundel.util.Orientation
import dev.sebastiano.bundel.util.currentOrientation

@Suppress("MagicNumber") // It's a preview
@Preview(backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
private fun HoursSchedulePagePreview() {
    BundelOnboardingTheme {
        Surface {
            ScheduleHoursPage(HoursSchedulePageState())
        }
    }
}

@Suppress("MagicNumber") // It's a preview
@Preview(backgroundColor = 0xFF4CE062, showBackground = true, widthDp = 822, heightDp = 392)
@Preview(backgroundColor = 0xFF4CE062, showBackground = true, widthDp = 622, heightDp = 422)
@Composable
private fun HoursSchedulePageLandscapePreview() {
    BundelOnboardingTheme {
        Surface {
            ScheduleHoursPage(HoursSchedulePageState(), orientation = Orientation.Landscape)
        }
    }
}

internal class HoursSchedulePageState(
    val timeRangesSchedule: TimeRangesSchedule,
    val onAddTimeRange: () -> Unit,
    val onRemoveTimeRange: (timeRange: TimeRange) -> Unit,
    val onChangeTimeRange: (old: TimeRange, new: TimeRange) -> Unit
) {

    constructor() : this(
        timeRangesSchedule = TimeRangesSchedule(),
        onAddTimeRange = {},
        onRemoveTimeRange = {},
        onChangeTimeRange = { _, _ -> }
    )
}

@Composable
internal fun ScheduleHoursPage(
    hoursSchedulePageState: HoursSchedulePageState,
    orientation: Orientation = currentOrientation()
) {
    Column(
        modifier = Modifier.onboardingPageModifier(orientation),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (orientation == Orientation.Portrait) {
            PageTitle(text = stringResource(id = R.string.onboarding_schedule_title))

            Spacer(Modifier.height(24.dp))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = stringResource(R.string.onboarding_schedule_blurb),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            val items = hoursSchedulePageState.timeRangesSchedule.timeRanges.withIndex().toList()

            items(items = items) { (index, timeRange) ->
                val minimumAllowedFrom = if (index > 0) items[index - 1].value.to else null
                val maximumAllowedTo = if (index < items.count() - 1) items[index + 1].value.from else null

                TimeRangeRow(
                    pickerBackgroundColor = MaterialTheme.colors.secondary,
                    timeRange = timeRange,
                    canBeRemoved = hoursSchedulePageState.timeRangesSchedule.canRemoveRanges,
                    minimumAllowableFrom = minimumAllowedFrom,
                    maximumAllowableTo = maximumAllowedTo,
                    onRemoved = if (hoursSchedulePageState.timeRangesSchedule.canRemoveRanges) {
                        { hoursSchedulePageState.onRemoveTimeRange(timeRange) }
                    } else {
                        { }
                    }
                ) { newTimeRange -> hoursSchedulePageState.onChangeTimeRange(timeRange, newTimeRange) }

                Spacer(modifier = Modifier.height(singlePadding()))
            }

            if (hoursSchedulePageState.timeRangesSchedule.canAppendAnotherRange) {
                item {
                    Box(modifier = Modifier.clickable { hoursSchedulePageState.onAddTimeRange() }) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            TimeRangeRow(pickerBackgroundColor = MaterialTheme.colors.secondary, timeRange = null, enabled = false)
                        }
                    }
                }
            }
        }
    }
}
