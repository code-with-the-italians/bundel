package dev.sebastiano.bundel.notificationslist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.sebastiano.bundel.BundelTheme
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.notifications.ActiveNotification
import dev.sebastiano.bundel.notifications.PersistableNotification

@Preview
@Composable
fun NotificationsListEmptyLightPreview() {
    BundelTheme {
        NotificationsListScreen(activeNotifications = emptyList())
    }
}

@Preview
@Composable
fun NotificationsListEmptyDarkPreview() {
    BundelTheme(darkModeOverride = true) {
        NotificationsListScreen(activeNotifications = emptyList())
    }
}

private val activeNotification = ActiveNotification(
    persistableNotification = PersistableNotification(
        id = 123,
        key = "123",
        timestamp = 12345678L,
        text = "Hello Ivan",
        appInfo = PersistableNotification.SenderAppInfo("com.yeah", "Yeah!")
    )
)

@Preview
@Composable
fun NotificationsListLightPreview() {
    BundelTheme {
        NotificationsListScreen(
            activeNotifications = listOf(activeNotification)
        )
    }
}

@Preview
@Composable
fun NotificationsListDarkPreview() {
    BundelTheme(darkModeOverride = true) {
        NotificationsListScreen(
            activeNotifications = listOf(activeNotification)
        )
    }
}

@Composable
internal fun NotificationsListScreen(
    activeNotifications: List<ActiveNotification>,
    onHistoryClicked: () -> Unit = {}
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { NotificationsListTopAppBar(onHistoryClicked) },
        scaffoldState = scaffoldState
    ) {
        if (activeNotifications.isNotEmpty()) {
            NotificationsLazyColumn(activeNotifications) {
                it.persistableNotification.key
//                it.interactions.main?.send()
            }
        } else {
            NotificationsListEmptyState()
        }
    }
}

@Composable
private fun NotificationsLazyColumn(
    activeNotifications: List<ActiveNotification>,
    onNotificationContentClick: (ActiveNotification) -> Unit = { }
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(activeNotifications.filterNot { it.persistableNotification.isGroup }) { notification ->
            NotificationItem(notification, onNotificationContentClick)
        }
    }
}

@Composable
private fun NotificationsListTopAppBar(onHistoryClicked: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name), style = MaterialTheme.typography.h4) },
        actions = {
            Button(onClick = onHistoryClicked) {
                Icon(Icons.Rounded.History, stringResource(id = R.string.menu_history))
            }
        }
    )
}
