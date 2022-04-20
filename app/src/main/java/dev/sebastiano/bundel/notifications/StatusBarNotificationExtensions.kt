package dev.sebastiano.bundel.notifications

import android.app.Notification
import android.app.Notification.EXTRA_SHOW_WHEN
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification

internal fun StatusBarNotification.toActiveNotificationOrNull(context: Context) =
    toActiveNotification(context).takeIf { it.isNotEmpty }

internal fun StatusBarNotification.toActiveNotification(context: Context): ActiveNotification {
    val packageManager = context.packageManager
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)

    return ActiveNotification(
        persistableNotification = PersistableNotification(
            id = id,
            key = key,
            timestamp = notification.`when`,
            showTimestamp = notification.run { `when` != 0L && extras.getBoolean(EXTRA_SHOW_WHEN) },
            isGroup = notification.run { groupKey != null && flags and Notification.FLAG_GROUP_SUMMARY != 0 },
            text = text,
            title = title,
            subText = subText,
            titleBig = titleBig,
            appInfo = extractAppInfo(applicationInfo, packageManager)
        ),
        icons = extractIcons(applicationInfo),
        interactions = extractInteractions(),
        isSnoozed = false
    )
}

private fun StatusBarNotification.extractIcons(applicationInfo: ApplicationInfo) = ActiveNotification.Icons(
    appIcon = Icon.createWithResource(packageName, applicationInfo.icon),
    small = notification.smallIcon,
    large = notification.getLargeIcon(),
    extraLarge = notification.extras.getParcelable(Notification.EXTRA_LARGE_ICON_BIG) as Icon?
)

private fun StatusBarNotification.extractAppInfo(
    applicationInfo: ApplicationInfo,
    packageManager: PackageManager
): PersistableNotification.SenderAppInfo =
    PersistableNotification.SenderAppInfo(
        packageName = packageName,
        name = packageManager.getResourcesForApplication(applicationInfo).getString(applicationInfo.labelRes),
        iconPath = null
    )

private fun StatusBarNotification.extractInteractions() = ActiveNotification.Interactions(
    main = notification.contentIntent,
    dismiss = notification.deleteIntent,
    actions = notification.actions?.map { ActiveNotification.Interactions.ActionItem(it.title, it.getIcon(), it.actionIntent) }
        ?: emptyList()
)

internal val StatusBarNotification.text: String?
    get() = notification.extras.get(Notification.EXTRA_TEXT)?.toString()

internal val StatusBarNotification.title: String?
    get() = notification.extras.get(Notification.EXTRA_TITLE)?.toString()

internal val StatusBarNotification.titleBig: String?
    get() = notification.extras.get(Notification.EXTRA_TITLE_BIG)?.toString()

internal val StatusBarNotification.subText: String?
    get() = notification.extras.get(Notification.EXTRA_SUB_TEXT)?.toString()
