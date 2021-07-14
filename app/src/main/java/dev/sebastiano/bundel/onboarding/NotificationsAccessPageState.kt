package dev.sebastiano.bundel.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoneOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.util.PembaaaOrientation
import dev.sebastiano.bundel.util.currentOrientation

internal class NotificationsAccessPageState(
    val needsPermission: Boolean,
    val onSettingsIntentClick: () -> Unit
) {

    constructor() : this(needsPermission = true, onSettingsIntentClick = {})
}

@Composable
internal fun NotificationsAccessPage(
    pageState: NotificationsAccessPageState,
    orientation: PembaaaOrientation = currentOrientation()
) {
    Column(
        modifier = Modifier.onboardingPageModifier(orientation),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (orientation == PembaaaOrientation.Portrait) {
            PageTitle(text = stringResource(id = R.string.onboarding_notifications_permission_title))
        }

        Spacer(Modifier.height(24.dp))

        if (pageState.needsPermission) {
            Text(
                text = stringResource(R.string.notifications_permission_explanation),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(onClick = pageState.onSettingsIntentClick) {
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
