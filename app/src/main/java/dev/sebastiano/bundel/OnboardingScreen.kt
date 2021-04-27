package dev.sebastiano.bundel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(name = "Onboarding screen (needs permission)", showSystemUi = true)
@Composable
private fun OnboardingScreenNeedsPermissionPreview() {
    OnboardingScreen(onSettingsIntentClick = { }, onDismissClicked = { })
}

@Preview(name = "Onboarding screen (dismiss only)", showSystemUi = true)
@Composable
private fun OnboardingScreenDismissOnlyPreview() {
    OnboardingScreen(onSettingsIntentClick = { }, onDismissClicked = { })
}

@Composable
internal fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onSettingsIntentClick: () -> Unit,
    onDismissClicked: () -> Unit
) {
    val state by viewModel.state.collectAsState(initial = OnboardingViewModel.State(false))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.h2)

        Spacer(modifier = Modifier.height(32.dp))

        if (state.needsPermission) {
            RequestNotificationsAccess(onSettingsIntentClick)
        } else {
            Button(onClick = onDismissClicked) {
                Text(text = "Let's a-go!")
            }
        }
    }
}

@Composable
private fun RequestNotificationsAccess(onSettingsIntentClick: () -> Unit) {
    Text(
        text = stringResource(R.string.notifications_permission_explanation),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .padding(bottom = 16.dp)
    )
    Button(
        onClick = onSettingsIntentClick,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(stringResource(R.string.button_notifications_access_prompt))
    }
}
