package dev.sebastiano.bundel.notificationslist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import dev.sebastiano.bundel.notifications.ActiveNotification
import dev.sebastiano.bundel.notifications.BundelNotificationListenerService
import dev.sebastiano.bundel.notifications.PersistableNotification
import dev.sebastiano.bundel.ui.BundelTheme
import dev.sebastiano.bundel.ui.singlePadding
import kotlinx.coroutines.launch

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
    lifecycle: Lifecycle,
    innerPadding: PaddingValues,
    onItemClicked: suspend (notification: ActiveNotification) -> Unit
) {
    val notifications by remember(lifecycle) { BundelNotificationListenerService.NOTIFICATIONS_FLOW.flowWithLifecycle(lifecycle) }
        .collectAsState(emptyList())
    NotificationsListScreen(innerPadding, notifications, onItemClicked)
}

@Composable
private fun NotificationsListScreen(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    activeNotifications: List<ActiveNotification>,
    onItemClicked: suspend (notification: ActiveNotification) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    if (activeNotifications.isNotEmpty()) {
        NotificationsLazyColumn(activeNotifications, Modifier.padding(innerPadding)) { notification ->
            scope.launch { onItemClicked(notification) }
        }
    } else {
        NotificationsListEmptyState()
    }
}

@Composable
private fun NotificationsLazyColumn(
    activeNotifications: List<ActiveNotification>,
    modifier: Modifier = Modifier,
    onNotificationContentClick: (ActiveNotification) -> Unit = { }
) {
    LazyColumn(modifier = Modifier.fillMaxSize().then(modifier), contentPadding = PaddingValues(singlePadding())) {
        val items = activeNotifications.filterNot { it.persistableNotification.isGroup }
        itemsIndexed(
            items = items,
            key = { _, item -> item.persistableNotification.uniqueId }
        ) { index, notification ->
            NotificationItem(notification, isLastItem = index == items.lastIndex, onNotificationContentClick)
        }
    }
}
