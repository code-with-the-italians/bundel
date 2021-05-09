package dev.sebastiano.bundel.notificationslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sebastiano.bundel.BundelTheme
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.notifications.NotificationEntry

@Preview
@Composable
fun NotificationsListEmptyLightPreview() {
    BundelTheme {
        NotificationsListScreen(notificationEntries = emptyList())
    }
}

@Preview
@Composable
fun NotificationsListEmptyDarkPreview() {
    BundelTheme(darkModeOverride = true) {
        NotificationsListScreen(notificationEntries = emptyList())
    }
}

@Preview
@Composable
fun NotificationsListLightPreview() {
    BundelTheme {
        NotificationsListScreen(
            notificationEntries = listOf(
                NotificationEntry(
                    timestamp = 12345678L,
                    text = "Hello Ivan",
                    appInfo = NotificationEntry.SenderAppInfo("com.yeah", "Yeah!")
                )
            )
        )
    }
}

@Preview
@Composable
fun NotificationsListDarkPreview() {
    BundelTheme(darkModeOverride = true) {
        NotificationsListScreen(
            notificationEntries = listOf(
                NotificationEntry(
                    timestamp = 12345678L,
                    text = "Hello Ivan",
                    appInfo = NotificationEntry.SenderAppInfo("com.yeah", "Yeah!")
                )
            )
        )
    }
}

@Composable
internal fun NotificationsListScreen(notificationEntries: List<NotificationEntry>) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { NotificationsListTopAppBar() },
    ) {
        if (notificationEntries.isNotEmpty()) {
            Column(Modifier.fillMaxSize()) {
                NotificationsList(notificationEntries)
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
                    Text(text = stringResource(R.string.notifications_empty_text), style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}

@Composable
private fun NotificationsList(notificationEntries: List<NotificationEntry>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notificationEntries.filterNot { it.isGroup }) { notification ->
            NotificationItem(notification)
        }
    }
}

@Composable
private fun NotificationsListTopAppBar() {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.app_name), style = MaterialTheme.typography.h4)
        }
    )
}
