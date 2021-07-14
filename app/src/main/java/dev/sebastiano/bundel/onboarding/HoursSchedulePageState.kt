package dev.sebastiano.bundel.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.BundelOnboardingTheme
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.singlePadding

@Suppress("MagicNumber") // It's a preview
@Preview(backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
private fun HoursSchedulePagePreview() {
    BundelOnboardingTheme {
        ScheduleHoursPage(HoursSchedulePageState())
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
internal fun ScheduleHoursPage(hoursSchedulePageState: HoursSchedulePageState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_schedule_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.onboarding_schedule_blurb),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val items = hoursSchedulePageState.timeRangesSchedule.timeRanges.withIndex().toList()

            items(items = items) { (index, timeRange) ->
                val minimumAllowedFrom = if (index > 0) items[index - 1].value.to else null
                val maximumAllowedTo = if (index < items.count() - 1) items[index + 1].value.from else null

                TimeRangeRow(
                    timeRange = timeRange,
                    onRemoved = if (hoursSchedulePageState.timeRangesSchedule.canRemoveRanges) {
                        { hoursSchedulePageState.onRemoveTimeRange(timeRange) }
                    } else {
                        { }
                    },
                    canBeRemoved = hoursSchedulePageState.timeRangesSchedule.canRemoveRanges,
                    onTimeRangeChanged = { newTimeRange -> hoursSchedulePageState.onChangeTimeRange(timeRange, newTimeRange) },
                    minimumAllowableFrom = minimumAllowedFrom,
                    maximumAllowableTo = maximumAllowedTo
                )

                Spacer(modifier = Modifier.height(singlePadding()))
            }

            if (hoursSchedulePageState.timeRangesSchedule.canAppendAnotherRange) {
                item {
                    Box(modifier = Modifier.clickable { hoursSchedulePageState.onAddTimeRange() }) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            TimeRangeRow(timeRange = null, enabled = false)
                        }
                    }
                }
            }
        }
    }
}
