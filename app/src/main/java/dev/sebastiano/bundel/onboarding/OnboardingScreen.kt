package dev.sebastiano.bundel.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dev.sebastiano.bundel.BundelOnboardingTheme
import dev.sebastiano.bundel.R
import java.time.temporal.TemporalAdjusters.next

@Preview(name = "Onboarding screen (needs permission)", showSystemUi = true)
@Composable
internal fun OnboardingScreenNeedsPermissionPreview() {
    BundelOnboardingTheme {
        OnboardingScreen(
            true,
            onSettingsIntentClick = { },
            onDismissClicked = { },
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
            onDismissClicked = { },
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
            onDismissClicked = { },
            true,
            {}
        )
    }
}

@Composable
internal fun OnboardingScreen(
    needsPermission: Boolean,
    onSettingsIntentClick: () -> Unit,
    onDismissClicked: () -> Unit,
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

            OnboardingPager(needsPermission, onSettingsIntentClick, onDismissClicked, crashReportingEnabled, onSwitchChanged)

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { error("YOLO") }) {
                    Text(text = stringResource(id = R.string.next))
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ColumnScope.OnboardingPager(
    needsPermission: Boolean,
    onSettingsIntentClick: () -> Unit,
    onDismissClicked: () -> Unit,
    crashReportingEnabled: Boolean,
    onSwitchChanged: (Boolean) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = if (needsPermission) 2 else 1
    )

    HorizontalPager(pagerState, Modifier.weight(1F)) { pageIndex ->
        when (pageIndex) {
            0 -> IntroPage(crashReportingEnabled, onSwitchChanged)
            1 -> RequestNotificationsAccess(onSettingsIntentClick)
            else -> error("Too many pages")
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

        CrashlyticsSwitch(crashReportingEnabled, onSwitchChanged)
    }
}

@Composable
private fun CrashlyticsSwitch(
    crashReportingEnabled: Boolean,
    onSwitchChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Enable Crash reporting",
                style = MaterialTheme.typography.h5
            )
        }
        Column {
            Switch(
                checked = crashReportingEnabled,
                onCheckedChange = { onSwitchChanged(!crashReportingEnabled) },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.End),
            )
        }
    }
}

@Composable
private fun RequestNotificationsAccess(onSettingsIntentClick: () -> Unit) {
    Text(
        text = stringResource(R.string.notifications_permission_explanation),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    Button(
        onClick = onSettingsIntentClick,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(stringResource(R.string.button_notifications_access_prompt))
    }
}
