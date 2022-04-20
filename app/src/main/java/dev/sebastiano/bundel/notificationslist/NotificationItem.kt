package dev.sebastiano.bundel.notificationslist

import android.graphics.drawable.Icon
import android.text.format.DateUtils
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.swipeable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.notifications.ActiveNotification
import dev.sebastiano.bundel.notifications.PersistableNotification
import dev.sebastiano.bundel.notificationslist.DismissState.Closed
import dev.sebastiano.bundel.notificationslist.DismissState.Open
import dev.sebastiano.bundel.storage.ImagesStorage
import dev.sebastiano.bundel.ui.BundelYouTheme
import dev.sebastiano.bundel.ui.iconSize
import dev.sebastiano.bundel.ui.singlePadding
import dev.sebastiano.bundel.util.asImageBitmap
import dev.sebastiano.bundel.util.rememberIconPainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private fun previewNotification() = ActiveNotification(
    persistableNotification = PersistableNotification(
        id = 1234,
        key = "1234",
        timestamp = 12345678L,
        showTimestamp = true,
        text = "Hello I'm Ivan and I'm here to complain about things.",
        title = "Ivan Morgillo",
        appInfo = PersistableNotification.SenderAppInfo(
            "com.yeah",
            "Yeah! messenger"
        ),
    ),
    interactions = ActiveNotification.Interactions(
        actions = listOf(
            ActiveNotification.Interactions.ActionItem(text = "Mai una gioia"),
            ActiveNotification.Interactions.ActionItem(text = "Mai una gioia"),
            ActiveNotification.Interactions.ActionItem(text = "Mai una gioia"),
            ActiveNotification.Interactions.ActionItem(text = "Mai una gioia"),
        )
    ),
    isSnoozed = false
)

@Preview
@Composable
private fun NotificationItemLightPreview() {
    BundelYouTheme {
        SnoozeItem(
            activeNotification = previewNotification(),
            onNotificationClick = {},
            onNotificationDismiss = {}
        )
    }
}

@Preview
@Composable
private fun NotificationItemDarkPreview() {
    BundelYouTheme(darkTheme = true) {
        SnoozeItem(
            activeNotification = previewNotification(),
            onNotificationClick = {},
            onNotificationDismiss = {}
        )
    }
}

private enum class DismissState {
    Open, Closed
}

private const val SNOOZE_PREVIEW_TIMEOUT_MILLIS = 5_000L

@OptIn(ExperimentalMaterialApi::class)
@Suppress("LongMethod") // Kinda has to be :(
@Composable
internal fun SnoozeItem(
    activeNotification: ActiveNotification,
    onNotificationClick: (ActiveNotification) -> Unit,
    onNotificationDismiss: (ActiveNotification) -> Unit
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    var isInProgress by remember { mutableStateOf(false) }
    val isActuallySnoozed = activeNotification.isSnoozed
    val isEnabled = !isInProgress && !isActuallySnoozed

    LaunchedEffect(isInProgress) {
        if (isInProgress) {
            delay(SNOOZE_PREVIEW_TIMEOUT_MILLIS)
            isInProgress = false
        }
    }
    LaunchedEffect(isActuallySnoozed) {
        isInProgress = false
    }

    val coroutineScope = rememberCoroutineScope()

    val openPixels = with(LocalDensity.current) { 48.dp.toPx() }
    val anchors = mapOf(0f to Closed, openPixels to Open)
    val state = remember { SwipeableState(initialValue = Closed) }

    Box(
        Modifier
            .swipeable(
                orientation = Orientation.Horizontal,
                anchors = anchors,
                state = state,
                enabled = isEnabled,
                reverseDirection = isRtl
            )
    ) {
        // background
        SnoozeBackground(modifier = Modifier.matchParentSize()) {
            coroutineScope.launch {
                isInProgress = true
                state.animateTo(Closed)
            }

            onNotificationDismiss(activeNotification)
        }
        // item content
        Box(modifier = Modifier.offset(offset = { IntOffset(x = state.offset.value.roundToInt(), y = 0) })) {
            NotificationItem(
                activeNotification = activeNotification,
                onNotificationClick = onNotificationClick,
                isEnabled = isEnabled
            )
        }
    }
}

@Composable
private fun SnoozeBackground(
    modifier: Modifier = Modifier,
    cornerRadiusFactor: State<Float> = remember { mutableStateOf(1f) },
    onSnoozeClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(end = 10.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(4.dp * cornerRadiusFactor.value.absoluteValue.coerceAtMost(1f)),
            ),
    ) {
        IconButton(
            onClick = onSnoozeClicked,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                Icons.Default.Timer,
                contentDescription = "Snooze",
                tint = MaterialTheme.colorScheme.onTertiary,
            )
        }
    }
}

private fun ActiveNotification.ifClickable(onClick: (ActiveNotification) -> Unit) =
    if (isClickable()) {
        { onClick(this) }
    } else {
        {} // No-op
    }

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun NotificationItem(
    activeNotification: ActiveNotification,
    isEnabled: Boolean = true,
    onNotificationClick: (ActiveNotification) -> Unit = {}
) {
    val itemAlpha by animateFloatAsState(if (isEnabled) 1f else ContentAlpha.disabled)
    Card(
        modifier = Modifier.fillMaxWidth(),
        enabled = !activeNotification.isSnoozed,
        onClick = activeNotification.ifClickable { onNotificationClick(activeNotification) }
    ) {
        Column(
            Modifier
                .padding(singlePadding())
                .alpha(itemAlpha)
        ) {
            NotificationMetadata(activeNotification.persistableNotification, activeNotification.icons.appIcon)

            NotificationContent(
                notification = activeNotification.persistableNotification,
                iconPainter = rememberIconPainter(activeNotification.icons.large ?: activeNotification.icons.small),
                interactions = activeNotification.interactions,
                enabled = !activeNotification.isSnoozed
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun NotificationItem(
    persistableNotification: PersistableNotification,
    imagesStorage: ImagesStorage,
    isLastItem: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .run { if (!isLastItem) padding(bottom = singlePadding()) else this }
    ) {
        Column(Modifier.padding(singlePadding())) {
            NotificationMetadata(persistableNotification, persistableNotification.appInfo.iconPath)

            val iconPainter = rememberAsyncImagePainter(
                model = getIconFile(imagesStorage, persistableNotification)
            )
            NotificationContent(persistableNotification, iconPainter, interactions = null)
        }
    }
}

@Composable
private fun getIconFile(
    imagesStorage: ImagesStorage,
    persistableNotification: PersistableNotification
) =
    File(imagesStorage.getIconPath(persistableNotification.uniqueId, ImagesStorage.NotificationIconSize.LARGE))
        .takeIf { it.exists() }
        ?: File(imagesStorage.getIconPath(persistableNotification.uniqueId, ImagesStorage.NotificationIconSize.SMALL))

@Composable
private fun NotificationMetadata(notification: PersistableNotification, appIcon: Any?) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = singlePadding()),
        verticalAlignment = Alignment.Bottom
    ) {
        val appName = notification.appInfo.name ?: notification.appInfo.packageName

        AppIcon(appIcon, appName)

        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall) {
            Text(appName, Modifier.weight(1F, fill = false), maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (notification.showTimestamp) Timestamp(notification)
        }
    }
}

@Composable
private fun AppIcon(appIcon: Any?, appName: String) {
    if (appIcon is String) {
        SubcomposeAsyncImage(
            model = appIcon,
            contentDescription = stringResource(id = R.string.app_icon_content_description, appName),
        ) {
            if (painter.state is AsyncImagePainter.State.Success) {
                SubcomposeAsyncImageContent(Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(singlePadding()))
            }
        }
    } else if (appIcon is Icon) {
        Image(appIcon.asImageBitmap(), stringResource(id = R.string.app_icon_content_description, appName), Modifier.size(16.dp))
        Spacer(modifier = Modifier.size(singlePadding()))
    }
}

@Composable
private fun Timestamp(notification: PersistableNotification) {
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
private fun NotificationContent(
    notification: PersistableNotification,
    iconPainter: Painter?,
    interactions: ActiveNotification.Interactions?,
    enabled: Boolean = true
) {
    Row(Modifier.fillMaxWidth()) {
        if (iconPainter != null) {
            Image(iconPainter, stringResource(R.string.notification_icon_content_description), modifier = Modifier.size(iconSize()))
        } else {
            Image(
                Icons.Rounded.BrokenImage,
                stringResource(R.string.notification_no_icon_content_description),
                modifier = Modifier.size(iconSize()),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        }
        Column {
            if (notification.title != null) {
                Text(
                    text = notification.title.trim(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = singlePadding())
                )
            }

            if (notification.text != null) {
                Text(
                    text = notification.text.trim(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = singlePadding())
                )
            }

            if (interactions != null) NotificationActions(interactions, enabled)
        }
    }
}

@Composable
private fun NotificationActions(
    interactions: ActiveNotification.Interactions,
    enabled: Boolean
) {
    if (interactions.actions.isEmpty()) return

    Spacer(modifier = Modifier.height(singlePadding()))
    val scrollState = rememberScrollState()
    Row(Modifier.horizontalScroll(scrollState)) {
        val items = interactions.actions.take(3)
        for ((index, action) in items.withIndex()) {
            TextButton(
                onClick = { action.pendingIntent?.send() },
                enabled = enabled
            ) {
                Text(action.text.trim().toString())
            }
            if (index < items.size - 1) {
                Spacer(modifier = Modifier.size(singlePadding()))
            }
        }
    }
}
