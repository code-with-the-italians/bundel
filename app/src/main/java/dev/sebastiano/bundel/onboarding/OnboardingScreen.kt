@file:OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)

package dev.sebastiano.bundel.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import dev.sebastiano.bundel.R
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

@Composable
internal fun OnboardingScreen(
    needsPermission: Boolean,
    onSettingsIntentClick: () -> Unit,
    onOnboardingDoneClicked: () -> Unit,
    crashReportingEnabled: Boolean,
    onSwitchChanged: (Boolean) -> Unit
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

            val pagerState = rememberPagerState(
                pageCount = if (needsPermission) 2 else 1
            )
            OnboardingPager(
                onSettingsIntentClick, crashReportingEnabled, onSwitchChanged, pagerState
            )

            Spacer(modifier = Modifier.height(32.dp))

            ActionsRow(pagerState, onOnboardingDoneClicked)
        }
    }
}

@Composable
private fun ColumnScope.OnboardingPager(
    onSettingsIntentClick: () -> Unit,
    crashReportingEnabled: Boolean,
    onSwitchChanged: (Boolean) -> Unit,
    pagerState: PagerState
) {
    HorizontalPager(pagerState, Modifier.weight(1F)) { pageIndex ->
        when (pageIndex) {
            0 -> IntroPage(crashReportingEnabled, onSwitchChanged)
            1 -> RequestNotificationsAccess(onSettingsIntentClick)
            else -> error("Too many pages") // TODO
        }
    }
}

@Composable
fun IntroPage(
    crashReportingEnabled: Boolean,
    onSwitchChanged: (Boolean) -> Unit
) {
    Column {
        Text(text = stringResource(id = R.string.onboarding_blurb))

        Spacer(modifier = Modifier.height(24.dp))

        CrashlyticsSwitch(
            crashReportingEnabled = crashReportingEnabled,
            onSwitchChanged = onSwitchChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = singlePadding(), horizontal = 16.dp)
        )
    }
}

@Composable
private fun CrashlyticsSwitch(
    crashReportingEnabled: Boolean,
    onSwitchChanged: (Boolean) -> Unit,
    modifier: Modifier
) {
    var switchChecked by remember { mutableStateOf(crashReportingEnabled) }
    Row(
        modifier = Modifier
            .clickable { switchChecked = !switchChecked }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.onboarding_enable_crashlytics),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = switchChecked,
            onCheckedChange = { onSwitchChanged(!crashReportingEnabled) },
            colors = SwitchDefaults.colors(
                // TODO
            )
        )
    }
}

@Composable
private fun RequestNotificationsAccess(onSettingsIntentClick: () -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.notifications_permission_explanation),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSettingsIntentClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.button_notifications_access_prompt))
        }
    }
}

@Composable
private fun ActionsRow(pagerState: PagerState, onOnboardingDoneClicked: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val scope = rememberCoroutineScope()
        AnimatedVisibility(
            visible = pagerState.currentPage > 0,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(text = stringResource(id = R.string.back).toUpperCase(Locale.getDefault()))
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.Center)
        )

        if (pagerState.currentPage < pagerState.pageCount - 1) {
            Button(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(text = stringResource(id = R.string.next).toUpperCase(Locale.getDefault()))
            }
        } else {
            Button(
                onClick = { onOnboardingDoneClicked() },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(text = stringResource(id = R.string.done).toUpperCase(Locale.getDefault()))
            }
        }
    }
}
