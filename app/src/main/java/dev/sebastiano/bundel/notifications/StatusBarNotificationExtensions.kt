package dev.sebastiano.bundel.notifications

import android.app.Notification
import android.app.Notification.EXTRA_SHOW_WHEN
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

internal fun StatusBarNotification.toActiveNotificationOrNull(context: Context) =
    toActiveNotification(context).takeIf { it.isNotEmpty }

@Suppress("DEPRECATION")
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
            appInfo = extractAppInfo(applicationInfo, packageManager),
        ),
        icons = extractIcons(applicationInfo),
        interactions = extractInteractions(),
        isSnoozed = false,
    )
}

@Suppress("DEPRECATION")
private fun StatusBarNotification.extractIcons(applicationInfo: ApplicationInfo) = ActiveNotification.Icons(
    appIcon = Icon.createWithResource(packageName, applicationInfo.icon),
    small = notification.smallIcon,
    large = notification.getLargeIcon(),
    extraLarge = notification.extras.getParcelable(Notification.EXTRA_LARGE_ICON_BIG),
)

private fun StatusBarNotification.extractAppInfo(
    applicationInfo: ApplicationInfo,
    packageManager: PackageManager,
): PersistableNotification.SenderAppInfo =
    PersistableNotification.SenderAppInfo(
        packageName = packageName,
        name = if (applicationInfo.labelRes != Resources.ID_NULL) {
            packageManager.getResourcesForApplication(applicationInfo).getString(applicationInfo.labelRes)
        } else {
            Firebase.crashlytics.log("Application ${applicationInfo.packageName} has no label")
            applicationInfo.packageName
        },
        iconPath = null,
    )

private fun StatusBarNotification.extractInteractions() = ActiveNotification.Interactions(
    main = notification.contentIntent,
    dismiss = notification.deleteIntent,
    actions = notification.actions
        ?.map { ActiveNotification.Interactions.ActionItem(it.title, it.getIcon(), it.actionIntent) }
        .orEmpty(),
)

internal val StatusBarNotification.text: String?
    get() = notification.extras.getString(Notification.EXTRA_TEXT)

internal val StatusBarNotification.title: String?
    get() = notification.extras.getString(Notification.EXTRA_TITLE)

internal val StatusBarNotification.titleBig: String?
    get() = notification.extras.getString(Notification.EXTRA_TITLE_BIG)

internal val StatusBarNotification.subText: String?
    get() = notification.extras.getString(Notification.EXTRA_SUB_TEXT)
