package dev.sebastiano.bundel.storage

import android.graphics.drawable.Icon
import dev.sebastiano.bundel.notifications.ActiveNotification

internal interface ImagesStorage {

    suspend fun saveIconsFrom(activeNotification: ActiveNotification)

    fun getIconPath(notificationUniqueId: String, iconSize: NotificationIconSize): String

    suspend fun deleteIconsFor(notificationUniqueId: String)

    suspend fun saveAppIcon(packageName: String, icon: Icon)

    fun getAppIconPath(packageName: String): String

    suspend fun deleteAppIcon(packageName: String)

    suspend fun clear()

    enum class NotificationIconSize(val cacheKey: String) {
        SMALL("small"),
        LARGE("large"),
        EXTRA_LARGE("xlarge")
    }
}
