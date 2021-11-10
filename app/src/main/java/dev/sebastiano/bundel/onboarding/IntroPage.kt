package dev.sebastiano.bundel.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.ui.BundelOnboardingYouTheme
import dev.sebastiano.bundel.ui.singlePadding
import dev.sebastiano.bundel.util.Orientation
import dev.sebastiano.bundel.util.currentOrientation

@Preview(backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
fun IntroPagePreview() {
    BundelOnboardingYouTheme {
        Surface {
            val england = IntroPageState()
            IntroPage(pageState = england)
        }
    }
}

@Preview(backgroundColor = 0xFF4CE062, showBackground = true, widthDp = 822, heightDp = 392)
@Composable
fun IntroPageLandscapePreview() {
    BundelOnboardingYouTheme {
        Surface {
            val england = IntroPageState()
            IntroPage(pageState = england, orientation = Orientation.Landscape)
        }
    }
}

internal class IntroPageState(
    val crashReportingEnabled: Boolean,
    val onCrashlyticsEnabledChanged: (Boolean) -> Unit
) {

    constructor() : this(crashReportingEnabled = false, onCrashlyticsEnabledChanged = { })
}

@Composable
internal fun IntroPage(
    pageState: IntroPageState,
    orientation: Orientation = currentOrientation()
) {
    Column(
        modifier = Modifier.onboardingPageModifier(orientation),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (orientation == Orientation.Portrait) {
            PageTitle(text = stringResource(id = R.string.onboarding_welcome_title), textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.onboarding_blurb),
            textAlign = TextAlign.Center
        )

        val spacerHeight = if (orientation == Orientation.Portrait) 24.dp else singlePadding()

        Spacer(modifier = Modifier.height(spacerHeight))

        CrashlyticsSwitch(
            crashReportingEnabled = pageState.crashReportingEnabled,
            onCrashlyticsEnabledChanged = pageState.onCrashlyticsEnabledChanged,
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
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSecondary,
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = MaterialTheme.colorScheme.onSecondary,
            )
        )

        Spacer(modifier = Modifier.width(singlePadding()))

        Text(stringResource(R.string.onboarding_enable_crashlytics))
    }
}
