@file:OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)

package dev.sebastiano.bundel.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoneOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import dev.sebastiano.bundel.BundelOnboardingTheme
import dev.sebastiano.bundel.OnboardingViewModel
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.composables.MaterialChip
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import dev.sebastiano.bundel.singlePadding
import kotlinx.coroutines.launch
import java.util.Locale

@Preview(name = "Onboarding screen (needs permission)", showSystemUi = true)
@Composable
internal fun OnboardingScreenNeedsPermissionPreview() {
    BundelOnboardingTheme {
        OnboardingScreen(
            true,
            onSettingsIntentClick = { },
            onOnboardingDoneClicked = { },
            false,
            {}
        )
    }
}

@Preview(name = "Onboarding screen (needs permission, dark theme)", showSystemUi = true)
@Composable
internal fun OnboardingDarkScreenNeedsPermissionPreview() {
    BundelOnboardingTheme(darkModeOverride = true) {
        OnboardingScreen(
            true,
            onSettingsIntentClick = { },
            onOnboardingDoneClicked = { },
            false,
            {}
        )
    }
}

@Preview(name = "Onboarding screen (dismiss only)", showSystemUi = true)
@Composable
internal fun OnboardingScreenDismissOnlyPreview() {
    BundelOnboardingTheme {
        OnboardingScreen(
            false,
            onSettingsIntentClick = { },
            onOnboardingDoneClicked = { },
            true,
            {}
        )
    }
}

@Preview(backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
private fun ScheduleDaysPagePreview() {
    BundelOnboardingTheme {
        ScheduleDaysPage(WeekDay.values().map { it to true }.toMap()) { _, _ -> }
    }
}

@Suppress("MagicNumber") // It's a preview
@Preview(backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
private fun ScheduleHoursPagePreview() {
    BundelOnboardingTheme {
        ScheduleHoursPage(
            schedule = TimeRangesSchedule(),
            onAddTimeRange = {},
            onRemoveTimeRange = {}
        ) { _, _ -> }
    }
}

@Composable
internal fun OnboardingScreen(
    needsPermission: Boolean,
    onSettingsIntentClick: () -> Unit,
    onOnboardingDoneClicked: () -> Unit,
    crashReportingEnabled: Boolean,
    onCrashlyticsEnabledChanged: (Boolean) -> Unit
) {
    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(R.drawable.ic_bundel_icon),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.h2)
            }

            Spacer(modifier = Modifier.height(32.dp))

            val pagerState = rememberPagerState(pageCount = 5)
            OnboardingPager(
                onSettingsIntentClick = onSettingsIntentClick,
                crashReportingEnabled = crashReportingEnabled,
                onCrashlyticsEnabledChanged = onCrashlyticsEnabledChanged,
                pagerState = pagerState,
                needsPermission = needsPermission
            )

            Spacer(modifier = Modifier.height(32.dp))

            ActionsRow(pagerState, needsPermission, onOnboardingDoneClicked)
        }
    }
}

@Composable
private fun ColumnScope.OnboardingPager(
    onSettingsIntentClick: () -> Unit,
    crashReportingEnabled: Boolean,
    onCrashlyticsEnabledChanged: (Boolean) -> Unit,
    pagerState: PagerState,
    needsPermission: Boolean
) {
    val viewModel = viewModel<OnboardingViewModel>()

    @Suppress("MagicNumber") // Yolo, page indices
    HorizontalPager(pagerState, dragEnabled = false, modifier = Modifier.weight(1F)) { pageIndex ->
        when (pageIndex) {
            0 -> IntroPage(crashReportingEnabled, onCrashlyticsEnabledChanged)
            1 -> NotificationsAccessPage(onSettingsIntentClick, needsPermission)
            2 -> {
                val activeDays by viewModel.daysScheduleFlow.collectAsState(emptyMap())
                ScheduleDaysPage(activeDays) { day, active -> viewModel.onDaysScheduleChangeWeekDay(day, active) }
            }
            3 -> {
                val hoursSchedule by viewModel.timeRangesScheduleFlow.collectAsState(TimeRangesSchedule())
                ScheduleHoursPage(
                    schedule = hoursSchedule,
                    onAddTimeRange = { viewModel.onTimeRangesScheduleAddTimeRange() },
                    onRemoveTimeRange = { viewModel.onTimeRangesScheduleRemoveTimeRange(it) },
                    onChangeTimeRange = { old, new -> viewModel.onTimeRangesScheduleChangeTimeRange(old, new) }
                )
            }
            4 -> AllSetPage()
            else -> error("Too many pages")
        }
    }
}

@Composable
fun IntroPage(
    crashReportingEnabled: Boolean,
    onCrashlyticsEnabledChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_welcome_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.onboarding_blurb),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        CrashlyticsSwitch(
            crashReportingEnabled = crashReportingEnabled,
            onCrashlyticsEnabledChanged = onCrashlyticsEnabledChanged,
            modifier = Modifier.padding(vertical = singlePadding(), horizontal = 16.dp)
        )
    }
}

@Composable
private fun CrashlyticsSwitch(
    crashReportingEnabled: Boolean,
    onCrashlyticsEnabledChanged: (Boolean) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = Modifier
            .clickable { onCrashlyticsEnabledChanged(!crashReportingEnabled) }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = crashReportingEnabled,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = MaterialTheme.colors.secondary,
                uncheckedTrackColor = MaterialTheme.colors.onSecondary,
                checkedThumbColor = MaterialTheme.colors.secondary,
                checkedTrackColor = MaterialTheme.colors.onSecondary
            )
        )

        Spacer(modifier = Modifier.width(singlePadding()))

        Text(stringResource(R.string.onboarding_enable_crashlytics))
    }
}

@Composable
private fun NotificationsAccessPage(onSettingsIntentClick: () -> Unit, needsPermission: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.notifications_permission_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )

        Spacer(Modifier.height(24.dp))

        if (needsPermission) {
            Text(
                text = stringResource(R.string.notifications_permission_explanation),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(onClick = onSettingsIntentClick) {
                Text(stringResource(R.string.button_notifications_access_prompt))
            }
        } else {
            Icon(
                imageVector = Icons.Rounded.DoneOutline,
                contentDescription = stringResource(R.string.notifications_permission_done_image_content_description),
                tint = LocalContentColor.current,
                modifier = Modifier
                    .size(72.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.notifications_permission_all_done),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun ScheduleDaysPage(
    activeDays: Map<WeekDay, Boolean>,
    onDayCheckedChange: (day: WeekDay, checked: Boolean) -> Unit
) {
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

        FlowRow(
            modifier = Modifier.padding(horizontal = 32.dp),
            mainAxisAlignment = MainAxisAlignment.Center,
            mainAxisSpacing = singlePadding(),
            crossAxisSpacing = singlePadding()
        ) {
            for (weekDay in activeDays.keys) {
                MaterialChip(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    checkedBackgroundColor = MaterialTheme.colors.onSurface,
                    checked = checkNotNull(activeDays[weekDay]) { "Checked state missing for day $weekDay" },
                    onCheckedChanged = { checked -> onDayCheckedChange(weekDay, checked) }
                ) {
                    Text(
                        text = stringResource(id = weekDay.displayResId).uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.body1.plus(TextStyle(fontWeight = FontWeight.Medium))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.onboarding_schedule_blurb_2),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScheduleHoursPage(
    schedule: TimeRangesSchedule,
    onAddTimeRange: () -> Unit,
    onRemoveTimeRange: (timeRange: TimeRange) -> Unit,
    onChangeTimeRange: (old: TimeRange, new: TimeRange) -> Unit
) {
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
            val items = schedule.timeRanges.withIndex().toList()

            items(items = items) { (index, timeRange) ->
                val minimumAllowedFrom = if (index > 0) items[index - 1].value.to else null
                val maximumAllowedTo = if (index < items.count() - 1) items[index + 1].value.from else null

                TimeRangeRow(
                    timeRange = timeRange,
                    onRemoved = if (schedule.canRemoveRanges) {
                        { onRemoveTimeRange(timeRange) }
                    } else {
                        { }
                    },
                    canBeRemoved = schedule.canRemoveRanges,
                    onTimeRangeChanged = { newTimeRange -> onChangeTimeRange(timeRange, newTimeRange) },
                    minimumAllowableFrom = minimumAllowedFrom,
                    maximumAllowableTo = maximumAllowedTo
                )

                Spacer(modifier = Modifier.height(singlePadding()))
            }

            if (schedule.canAppendAnotherRange) {
                item {
                    Box(modifier = Modifier.clickable { onAddTimeRange() }) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            TimeRangeRow(timeRange = null, enabled = false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllSetPage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_all_set),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.onboarding_all_set_blurb),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ActionsRow(
    pagerState: PagerState,
    needsPermission: Boolean,
    onOnboardingDoneClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val scope = rememberCoroutineScope()
        val buttonColors = buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onSurface
        )
        val buttonElevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp)

        AnimatedVisibility(
            visible = pagerState.currentPage > 0,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                modifier = Modifier.align(Alignment.CenterStart),
                colors = buttonColors,
                elevation = buttonElevation
            ) {
                Text(text = stringResource(id = R.string.back).uppercase(Locale.getDefault()))
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.Center)
        )

        when {
            pagerState.currentPage < pagerState.pageCount - 1 -> {
                Button(
                    enabled = if (needsPermission) pagerState.currentPage != 1 else true,
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    colors = buttonColors,
                    elevation = buttonElevation
                ) {
                    Text(text = stringResource(id = R.string.next).uppercase(Locale.getDefault()))
                }
            }
            else -> {
                Button(
                    onClick = { onOnboardingDoneClicked() },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    colors = buttonColors,
                    elevation = buttonElevation
                ) {
                    Text(text = stringResource(id = R.string.done).uppercase(Locale.getDefault()))
                }
            }
        }
    }
}
