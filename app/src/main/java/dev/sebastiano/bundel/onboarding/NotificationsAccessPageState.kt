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

internal class NotificationsAccessPageState(
    val needsPermission: Boolean,
    val onSettingsIntentClick: () -> Unit
) {

    constructor() : this(needsPermission = true, onSettingsIntentClick = {})
}

@Composable
internal fun NotificationsAccessPage(pageState: NotificationsAccessPageState) {
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
