package dev.sebastiano.bundel.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.singlePadding

internal class IntroPageState(
    val crashReportingEnabled: Boolean,
    val onCrashlyticsEnabledChanged: (Boolean) -> Unit
) {

    constructor() : this(crashReportingEnabled = false, onCrashlyticsEnabledChanged = { })
}

@Composable
internal fun IntroPage(pageState: IntroPageState) {
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
