@file:OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)

package dev.sebastiano.bundel.onboarding

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import dev.sebastiano.bundel.ui.BundelOnboardingYouTheme
import dev.sebastiano.bundel.ui.singlePadding
import dev.sebastiano.bundel.util.Orientation
import dev.sebastiano.bundel.util.currentOrientation
import kotlinx.coroutines.launch
import java.util.Locale

@Preview(name = "Onboarding screen", showSystemUi = true)
@Composable
private fun OnboardingScreenPreview() {
    BundelOnboardingYouTheme {
        Surface {
            OnboardingScreen()
        }
    }
}

@Preview(name = "Onboarding screen (landscape)", widthDp = 822, heightDp = 392)
@Composable
private fun OnboardingScreenLandscapePreview() {
    BundelOnboardingYouTheme {
        Surface {
            OnboardingScreen(orientation = Orientation.Landscape)
        }
    }
}

@Preview(name = "Onboarding screen (dark theme)", showSystemUi = true)
@Composable
private fun OnboardingScreenDarkThemePreview() {
    BundelOnboardingYouTheme(darkTheme = true) {
        Surface {
            OnboardingScreen()
        }
    }
}

@Composable
internal fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    needsPermission: Boolean,
    onSettingsIntentClick: () -> Unit,
    onOnboardingDoneClicked: () -> Unit
) {
    BundelOnboardingYouTheme {
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
}

@Composable
private fun OnboardingScreen(
    needsPermission: Boolean = false,
    introPageState: IntroPageState = IntroPageState(),
    notificationsAccessPageState: NotificationsAccessPageState = NotificationsAccessPageState(),
    daysSchedulePageState: DaysSchedulePageState = DaysSchedulePageState(),
    hoursSchedulePageState: HoursSchedulePageState = HoursSchedulePageState(),
    orientation: Orientation = currentOrientation(),
    onOnboardingDoneClicked: () -> Unit = {}
) {
    val verticalScreenPadding = if (orientation == Orientation.Portrait) 16.dp else singlePadding()
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = verticalScreenPadding)
        ) {
            val pagerState = rememberPagerState()
            val onboardingPagerState = OnboardingPagerState(
                pagerState = pagerState,
                introPageState = introPageState,
                notificationsAccessPageState = notificationsAccessPageState,
                daysSchedulePageState = daysSchedulePageState,
                hoursSchedulePageState = hoursSchedulePageState
            )

            OnboardingHeader(orientation, pagerState.targetPage)

            OnboardingPager(state = onboardingPagerState)

            val actionsRowTopSpace = if (orientation == Orientation.Portrait) 32.dp else singlePadding()
            Spacer(modifier = Modifier.height(actionsRowTopSpace))

            ActionsRow(pagerState, needsPermission, onOnboardingDoneClicked)
        }
    }
}

internal enum class OnboardingPage(
    @StringRes val pageTitle: Int
) {

    Intro(R.string.onboarding_welcome_title),
    NotificationsPermission(R.string.onboarding_notifications_permission_title),
    DaysSchedule(R.string.onboarding_schedule_title),
    HoursSchedule(R.string.onboarding_schedule_title),
    AllSet(R.string.onboarding_all_set)
}

@Preview(name = "Onboarding header (landscape)", widthDp = 600)
@Composable
private fun OnboardingHeaderLandscapePreview() {
    BundelOnboardingYouTheme {
        Surface {
            Column(Modifier.fillMaxWidth()) {
                OnboardingHeader(
                    orientation = Orientation.Landscape,
                    pageIndex = OnboardingPage.Intro.ordinal
                )
            }
        }
    }
}

@Suppress("unused") // We rely on being inside a Column
@Composable
private fun ColumnScope.OnboardingHeader(
    orientation: Orientation,
    pageIndex: Int
) {
    if (orientation == Orientation.Portrait) {
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
            Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
        }

        Spacer(modifier = Modifier.height(32.dp))
    } else {
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.ic_bundel_icon),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)

            AnimatedContent(
                targetState = pageIndex,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1

                    val enterTransition = fadeIn(animationSpec = spring()) +
                        slideIn(initialOffset = { IntOffset(direction * it.width / 5, 0) }, animationSpec = spring())
                    val exitTransition = fadeOut(animationSpec = spring()) +
                        slideOut(targetOffset = { IntOffset(-direction * it.width / 5, 0) }, animationSpec = spring())
                    enterTransition with exitTransition
                }
            ) { pageIndex ->
                PageTitle(
                    text = stringResource(id = OnboardingPage.values()[pageIndex].pageTitle),
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
internal fun PageTitle(text: String, textAlign: TextAlign = TextAlign.Center) {
    Text(
        text = text,
        textAlign = textAlign,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth()
    )
}

private data class OnboardingPagerState(
    val pagerState: PagerState,
    val introPageState: IntroPageState,
    val notificationsAccessPageState: NotificationsAccessPageState,
    val daysSchedulePageState: DaysSchedulePageState,
    val hoursSchedulePageState: HoursSchedulePageState
)

@Composable
private fun ColumnScope.OnboardingPager(state: OnboardingPagerState) {
    HorizontalPager(count = 5, state = state.pagerState, modifier = Modifier.weight(1F)) {
        when (OnboardingPage.values()[it]) {
            OnboardingPage.Intro -> IntroPage(state.introPageState)
            OnboardingPage.NotificationsPermission -> NotificationsAccessPage(state.notificationsAccessPageState)
            OnboardingPage.DaysSchedule -> DaysSchedulePage(state.daysSchedulePageState)
            OnboardingPage.HoursSchedule -> ScheduleHoursPage(state.hoursSchedulePageState)
            OnboardingPage.AllSet -> AllSetPage()
        }
    }
}

internal fun Modifier.onboardingPageModifier(orientation: Orientation) =
    if (orientation == Orientation.Portrait) {
        fillMaxSize()
    } else {
        fillMaxSize()
            .padding(horizontal = 96.dp)
    }

@Preview
@Composable
private fun AllSetPagePreview() {
    BundelOnboardingYouTheme {
        Surface {
            AllSetPage()
        }
    }
}

@Preview(widthDp = 822, heightDp = 392)
@Composable
private fun AllSetPageLandscapePreview() {
    BundelOnboardingYouTheme {
        Surface {
            AllSetPage(orientation = Orientation.Landscape)
        }
    }
}

@Composable
private fun AllSetPage(orientation: Orientation = currentOrientation()) {
    Column(
        modifier = Modifier.onboardingPageModifier(orientation),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (orientation == Orientation.Portrait) {
            PageTitle(text = stringResource(id = R.string.onboarding_all_set))

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(R.drawable.misaligned_floor),
                contentDescription = stringResource(R.string.onboarding_all_done_image_description)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.onboarding_all_set_blurb),
                textAlign = TextAlign.Center
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.misaligned_floor),
                    contentDescription = stringResource(R.string.onboarding_all_done_image_description)
                )

                Spacer(modifier = Modifier.width(24.dp))

                Text(
                    text = stringResource(id = R.string.onboarding_all_set_blurb),
                    textAlign = TextAlign.Start
                )
            }
        }
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
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        val buttonElevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)

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
