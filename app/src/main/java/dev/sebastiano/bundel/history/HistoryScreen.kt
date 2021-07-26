package dev.sebastiano.bundel.history

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.notifications.PersistableNotification
import dev.sebastiano.bundel.notificationslist.NotificationItem
import dev.sebastiano.bundel.notificationslist.NotificationsListEmptyState
import dev.sebastiano.bundel.storage.DiskImagesStorage
import dev.sebastiano.bundel.ui.BundelTheme
import dev.sebastiano.bundel.ui.singlePadding

@Preview
@Composable
fun NotificationsHistoryEmptyLightPreview() {
    BundelTheme {
        NotificationsHistoryScreen(persistableNotifications = emptyList())
    }
}

@Preview
@Composable
fun NotificationsHistoryEmptyDarkPreview() {
    BundelTheme(darkModeOverride = true) {
        NotificationsHistoryScreen(persistableNotifications = emptyList())
    }
}

private val persistableNotification = PersistableNotification(
    id = 123,
    key = "123",
    timestamp = 12345678L,
    text = "Hello Ivan",
    appInfo = PersistableNotification.SenderAppInfo("com.yeah", "Yeah!")
)

@Preview
@Composable
fun NotificationsHistoryLightPreview() {
    BundelTheme {
        NotificationsHistoryScreen(
            persistableNotifications = listOf(persistableNotification)
        )
    }
}

@Preview
@Composable
fun NotificationsHistoryDarkPreview() {
    BundelTheme(darkModeOverride = true) {
        NotificationsHistoryScreen(
            persistableNotifications = listOf(persistableNotification)
        )
    }
}

@Composable
internal fun NotificationsHistoryScreen(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    persistableNotifications: List<PersistableNotification>
) {
    if (persistableNotifications.isNotEmpty()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NotificationsLazyColumn(persistableNotifications)
        }
    } else {
        NotificationsListEmptyState(Modifier.padding(innerPadding))
    }
}

@Composable
private fun NotificationsLazyColumn(persistableNotifications: List<PersistableNotification>) {
    val imagesStorage = DiskImagesStorage(LocalContext.current.applicationContext as Application)
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(singlePadding())) {
        val items = persistableNotifications.filterNot { it.isGroup }
        itemsIndexed(items) { index, notification ->
            NotificationItem(notification, imagesStorage, isLastItem = index == items.lastIndex)
        }
    }
}
