package dev.sebastiano.bundel

import android.graphics.drawable.Icon
import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import dev.sebastiano.bundel.notifications.Notification

@Preview
@Composable
fun NotificationsListEmptyLightPreview() {
    BundelTheme {
        NotificationsListScreen(notifications = listOf())
    }
}

@Preview
@Composable
fun NotificationsListLightPreview() {
    BundelTheme {
        NotificationsListScreen(
            notifications = listOf(
                Notification(timestamp = 12345678L, text = "Hello Ivan")
            )
        )
    }
}

@Composable
internal fun NotificationsListScreen(notifications: List<Notification>) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { NotificationsListTopAppBar() },
    ) {
        if (notifications.isNotEmpty()) {
            Column(Modifier.fillMaxSize()) {
                NotificationsList(notifications)
            }
        } else {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                Column(
                    Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.sad_face), fontSize = 72.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.notifications_empty_text))
                }
            }
        }
    }
}

@Composable
private fun NotificationsList(notifications: List<Notification>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notifications) { notification ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
            ) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        val icon = (notification.icons.small ?: notification.icons.large ?: notification.icons.extraLarge)
                            ?.asImageBitmap()
                        if (icon != null) {
                            Image(icon, stringResource(R.string.notification_icon_content_description), modifier = Modifier.size(48.dp))
                        } else {
                            Image(
                                Icons.Rounded.BrokenImage,
                                stringResource(R.string.notification_no_icon_content_description),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Text(notification.text ?: stringResource(R.string.notification_missing_value), Modifier.padding(8.dp))
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(
                                DateUtils.getRelativeTimeSpanString(
                                    notification.timestamp,
                                    System.currentTimeMillis(),
                                    DateUtils.SECOND_IN_MILLIS
                                ).toString(),
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Icon.asImageBitmap(): ImageBitmap? =
    loadDrawable(LocalContext.current)
        ?.toBitmap()
        ?.asImageBitmap()

@Composable
private fun NotificationsListTopAppBar() {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.app_name), style = MaterialTheme.typography.h4)
        }
    )
}
