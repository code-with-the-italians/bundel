package dev.sebastiano.bundel.notificationslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import dev.sebastiano.bundel.notifications.ActiveNotification
import dev.sebastiano.bundel.notifications.BundelNotificationListenerService
import dev.sebastiano.bundel.notifications.PersistableNotification
import dev.sebastiano.bundel.ui.BundelYouTheme
import dev.sebastiano.bundel.ui.singlePadding

@Preview
@Composable
fun NotificationsListEmptyLightPreview() {
    BundelYouTheme {
        NotificationsListScreen(
            activeNotifications = emptyList(),
            onNotificationClick = {},
            onNotificationDismiss = {},
        )
    }
}

@Preview
@Composable
fun NotificationsListEmptyDarkPreview() {
    BundelYouTheme(darkTheme = true) {
        NotificationsListScreen(
            activeNotifications = emptyList(),
            onNotificationClick = {},
            onNotificationDismiss = {},
        )
    }
}

private val activeNotification = ActiveNotification(
    persistableNotification = PersistableNotification(
        id = 123,
        key = "123",
        timestamp = 12345678L,
        text = "Hello Ivan",
        appInfo = PersistableNotification.SenderAppInfo("com.yeah", "Yeah!"),
    ),
    isSnoozed = false,
)

@Preview
@Composable
fun NotificationsListLightPreview() {
    BundelYouTheme {
        NotificationsListScreen(
            activeNotifications = listOf(activeNotification),
            onNotificationClick = {},
            onNotificationDismiss = {},
        )
    }
}

@Preview
@Composable
fun NotificationsListDarkPreview() {
    BundelYouTheme(darkTheme = true) {
        NotificationsListScreen(
            activeNotifications = listOf(activeNotification),
            onNotificationClick = {},
            onNotificationDismiss = {},
        )
    }
}

@Composable
internal fun NotificationsListScreen(
    lifecycle: Lifecycle,
    innerPadding: PaddingValues,
    onNotificationClick: (notification: ActiveNotification) -> Unit,
    onNotificationDismiss: (notification: ActiveNotification) -> Unit,
) {
    val notifications by remember(lifecycle) { BundelNotificationListenerService.activeNotificationsFlow.flowWithLifecycle(lifecycle) }
        .collectAsState(emptyList())
    NotificationsListScreen(notifications, onNotificationClick, onNotificationDismiss, innerPadding)
}

@Composable
private fun NotificationsListScreen(
    activeNotifications: List<ActiveNotification>,
    onNotificationClick: (notification: ActiveNotification) -> Unit,
    onNotificationDismiss: (notification: ActiveNotification) -> Unit,
    innerPadding: PaddingValues = PaddingValues(0.dp),
) {
    if (activeNotifications.isNotEmpty()) {
        NotificationsLazyColumn(
            activeNotifications,
            Modifier.padding(innerPadding),
            onNotificationClick,
            onNotificationDismiss,
        )
    } else {
        NotificationsListEmptyState()
    }
}

@Composable
private fun NotificationsLazyColumn(
    activeNotifications: List<ActiveNotification>,
    modifier: Modifier = Modifier,
    onNotificationClick: (ActiveNotification) -> Unit,
    onNotificationDismiss: (ActiveNotification) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentPadding = PaddingValues(singlePadding()),
        verticalArrangement = Arrangement.spacedBy(singlePadding()),
    ) {
        val items = activeNotifications.filterNot { it.persistableNotification.isGroup }
        items(items = items, key = { item -> item.persistableNotification.uniqueId }) { notification ->
            SnoozeItem(notification, onNotificationClick, onNotificationDismiss)
        }
    }
}
