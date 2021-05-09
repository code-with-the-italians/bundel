package dev.sebastiano.bundel.notificationslist

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.BundelTheme
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.notifications.NotificationEntry
import dev.sebastiano.bundel.util.asImageBitmap
import android.graphics.drawable.Icon as GraphicsIcon

@Composable
private fun iconSize() = 48.dp

@Composable
private fun singlePadding() = 8.dp

private fun previewNotification(context: Context) = NotificationEntry(
    timestamp = 12345678L,
    showTimestamp = true,
    interactions = NotificationEntry.Interactions(
        actions = listOf(
            NotificationEntry.Interactions.ActionItem(text = "Mai una gioia"),
            NotificationEntry.Interactions.ActionItem(text = "Mai una gioia"),
            NotificationEntry.Interactions.ActionItem(text = "Mai una gioia"),
            NotificationEntry.Interactions.ActionItem(text = "Mai una gioia"),
        )
    ),
    title = "Ivan Morgillo",
    text = "Hello I'm Ivan and I'm not here to complain about things.",
    appInfo = NotificationEntry.SenderAppInfo("com.yeah", "Yeah! messenger", GraphicsIcon.createWithResource(context, R.drawable.ic_whatever_24dp))
)

@Preview
@Composable
private fun NotificationItemLightPreview() {
    BundelTheme {
        NotificationItem(previewNotification(LocalContext.current))
    }
}

@Preview
@Composable
private fun NotificationItemDarkPreview() {
    BundelTheme(darkModeOverride = true) {
        NotificationItem(previewNotification(LocalContext.current))
    }
}

@Composable
internal fun NotificationItem(notification: NotificationEntry) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = singlePadding())
            .padding(top = singlePadding())
            .clickable(notification) { notification.interactions.main!!.send() }
    ) {
        Column(Modifier.padding(singlePadding())) {
            NotificationMetadata(notification)
            NotificationContent(notification)
        }
    }
}

private fun Modifier.clickable(notification: NotificationEntry, onClick: (NotificationEntry) -> Unit) =
    if (notification.isClickable()) clickable { onClick(notification) } else this

@Composable
fun NotificationMetadata(notification: NotificationEntry) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = singlePadding()),
        verticalAlignment = Alignment.Bottom
    ) {
        val appIcon = notification.appInfo.icon?.asImageBitmap()
        if (appIcon != null) {
            Image(appIcon, stringResource(id = R.string.app_icon_content_description, notification.appInfo.name), Modifier.size(16.dp))
            Spacer(modifier = Modifier.size(singlePadding()))
        }
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.caption) {
            Text(notification.appInfo.name, Modifier.weight(1F, fill = false), maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (notification.showTimestamp) Timestamp(notification)
        }
    }
}

@Composable
private fun Timestamp(notification: NotificationEntry) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(text = " · ")
        Text(
            DateUtils.getRelativeTimeSpanString(
                notification.timestamp,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS
            ).toString()
        )
    }
}

@Composable
private fun NotificationContent(notification: NotificationEntry) {
    Row(Modifier.fillMaxWidth()) {
        val icon = (notification.icons.large ?: notification.icons.small)
            ?.asImageBitmap()
        if (icon != null) {
            Image(icon, stringResource(R.string.notification_icon_content_description), modifier = Modifier.size(iconSize()))
        } else {
            Image(
                Icons.Rounded.BrokenImage,
                stringResource(R.string.notification_no_icon_content_description),
                modifier = Modifier.size(iconSize()),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
            )
        }
        Column {
            notification.title?.let {
                Text(
                    text = notification.title.trim(),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = singlePadding())
                )
            }

            notification.text?.let {
                Text(
                    text = notification.text.trim(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = singlePadding())
                )
            }

            NotificationActions(notification)
        }
    }
}

@Composable
fun NotificationActions(notification: NotificationEntry) {
    if (notification.interactions.actions.isEmpty()) return

    Spacer(modifier = Modifier.height(singlePadding()))
    val scrollState = rememberScrollState()
    Row(Modifier.horizontalScroll(scrollState)) {
        val items = notification.interactions.actions.take(3)
        for ((index, action) in items.withIndex()) {
            TextButton(onClick = { action.pendingIntent?.send() }) {
                Text(action.text.trim().toString())
            }
            if (index < items.size - 1) {
                Spacer(modifier = Modifier.size(singlePadding()))
            }
        }
    }
}
