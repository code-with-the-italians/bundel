package dev.sebastiano.bundel.notifications

import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification
import android.app.Notification as AndroidAppNotification

internal fun StatusBarNotification.toNotificationOrNull() =
    toNotification().takeIf { it.isNotEmpty }

internal fun StatusBarNotification.toNotification() = Notification(
    timestamp = notification.`when`,
    text = text,
    title = title,
    subText = subText,
    titleBig = titleBig,
    icons = extractIcons()
)

private fun StatusBarNotification.extractIcons() = Notification.Icons(
    small = notification.smallIcon,
    large = notification.getLargeIcon(),
    extraLarge = notification.extras.getParcelable(AndroidAppNotification.EXTRA_LARGE_ICON_BIG) as Icon?
)

internal val StatusBarNotification.text: String?
    get() = notification.extras.get(AndroidAppNotification.EXTRA_TEXT)?.toString()

internal val StatusBarNotification.title: String?
    get() = notification.extras.get(AndroidAppNotification.EXTRA_TITLE)?.toString()

internal val StatusBarNotification.titleBig: String?
    get() = notification.extras.get(AndroidAppNotification.EXTRA_TITLE_BIG)?.toString()

internal val StatusBarNotification.subText: String?
    get() = notification.extras.get(AndroidAppNotification.EXTRA_SUB_TEXT)?.toString()
