@file:OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)

package dev.sebastiano.bundel.onboarding

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import dev.sebastiano.bundel.BundelOnboardingTheme
import dev.sebastiano.bundel.OnboardingViewModel
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.launch
import java.util.Locale

@Preview(name = "Onboarding screen", showSystemUi = true)
@Composable
internal fun OnboardingScreenPreview() {
    BundelOnboardingTheme {
        OnboardingScreen()
    }
}

@Preview(name = "Onboarding screen (dark theme)", showSystemUi = true)
@Composable
internal fun OnboardingScreenDarkThemePreview() {
    BundelOnboardingTheme(darkModeOverride = true) {
        OnboardingScreen()
    }
}

internal enum class PembaaaOrientation {
    Landscape,
    Portrait
}

@Composable
internal fun currentOrientation(): PembaaaOrientation =
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> PembaaaOrientation.Landscape
        else -> PembaaaOrientation.Portrait
    }

@Composable
internal fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    needsPermission: Boolean,
    onSettingsIntentClick: () -> Unit,
    onOnboardingDoneClicked: () -> Unit
) {
    val crashReportingEnabled by viewModel.crashReportingEnabledFlowrina.collectAsState(initial = false)
    val daysSchedule by viewModel.daysScheduleFlow.collectAsState(initial = emptyMap())
    val timeRangesSchedule by viewModel.timeRangesScheduleFlow.collectAsState(initial = TimeRangesSchedule())

    OnboardingScreen(
        needsPermission = needsPermission,
        introPageState = IntroPageState(
            crashReportingEnabled = crashReportingEnabled,
            onCrashlyticsEnabledChanged = { viewModel.setCrashReportingEnabled(it) }
        ),
        notificationsAccessPageState = NotificationsAccessPageState(needsPermission, onSettingsIntentClick),
        daysSchedulePageState = DaysSchedulePageState(
            daysSchedule = daysSchedule,
            onDayCheckedChange = { weekDay: WeekDay, checked: Boolean -> viewModel.onDaysScheduleChangeWeekDay(weekDay, checked) }
        ),
        hoursSchedulePageState = HoursSchedulePageState(
            timeRangesSchedule = timeRangesSchedule,
            onAddTimeRange = { viewModel.onTimeRangesScheduleAddTimeRange() },
            onRemoveTimeRange = { viewModel.onTimeRangesScheduleRemoveTimeRange(it) },
            onChangeTimeRange = { old, new -> viewModel.onTimeRangesScheduleChangeTimeRange(old, new) }
        ),
        onOnboardingDoneClicked = onOnboardingDoneClicked,
    )
}

@Composable
internal fun OnboardingScreen(
    needsPermission: Boolean = false,
    introPageState: IntroPageState = IntroPageState(),
    notificationsAccessPageState: NotificationsAccessPageState = NotificationsAccessPageState(),
    daysSchedulePageState: DaysSchedulePageState = DaysSchedulePageState(),
    hoursSchedulePageState: HoursSchedulePageState = HoursSchedulePageState(),
    onOnboardingDoneClicked: () -> Unit = {}
) {
    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val orientation = currentOrientation()
            OnboardingHeader(orientation)

            val pagerState = rememberPagerState(pageCount = 5)
            val onboardingPagerState = OnboardingPagerState(
                pagerState,
                introPageState,
                notificationsAccessPageState,
                daysSchedulePageState,
                hoursSchedulePageState
            )               // Do we need to remember this? WHO KNOWS

            OnboardingPager(state = onboardingPagerState)

            Spacer(modifier = Modifier.height(32.dp))

            ActionsRow(pagerState, needsPermission, onOnboardingDoneClicked)
        }
    }
}

@Preview(name = "Onboarding header (landscape)", widthDp = 600, backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
fun OnboardingHeaderLandscapePreview() {
    BundelOnboardingTheme {
        Column(Modifier.fillMaxWidth()) {
            OnboardingHeader(orientation = PembaaaOrientation.Landscape)
        }
    }
}

@Suppress("unused") // We rely on being inside a Column
@Composable
private fun ColumnScope.OnboardingHeader(orientation: PembaaaOrientation) {
    if (orientation == PembaaaOrientation.Portrait) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.ic_bundel_icon),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.h2)
        }

        Spacer(modifier = Modifier.height(32.dp))
    } else {
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.ic_bundel_icon),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.h3)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

internal data class OnboardingPagerState(
    val pagerState: PagerState,
    val introPageState: IntroPageState,
    val notificationsAccessPageState: NotificationsAccessPageState,
    val daysSchedulePageState: DaysSchedulePageState,
    val hoursSchedulePageState: HoursSchedulePageState
)

@Composable
private fun ColumnScope.OnboardingPager(state: OnboardingPagerState) {
    @Suppress("MagicNumber") // Yolo, page indices
    HorizontalPager(state.pagerState, dragEnabled = false, modifier = Modifier.weight(1F)) { pageIndex ->
        when (pageIndex) {
            0 -> IntroPage(state.introPageState)
            1 -> NotificationsAccessPage(state.notificationsAccessPageState)
            2 -> DaysSchedulePage(state.daysSchedulePageState)
            3 -> ScheduleHoursPage(state.hoursSchedulePageState)
            4 -> AllSetPage()
            else -> error("Too many pages")
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
