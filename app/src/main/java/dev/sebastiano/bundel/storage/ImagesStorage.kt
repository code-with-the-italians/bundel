package dev.sebastiano.bundel.storage

import android.graphics.drawable.Icon
import dev.sebastiano.bundel.notifications.ActiveNotification
import java.io.File

internal interface ImagesStorage {

    suspend fun saveIconsFrom(activeNotification: ActiveNotification)

    fun getIconFile(notificationUniqueId: String, iconSize: NotificationIconSize): File

    suspend fun deleteIconsFor(notificationUniqueId: String)

    suspend fun saveAppIcon(packageName: String, icon: Icon)

    fun getAppIconFile(packageName: String): File

    suspend fun deleteAppIcon(packageName: String)

    suspend fun clear()

    enum class NotificationIconSize(val cacheKey: String) {
        SMALL("small"),
        LARGE("large"),
        EXTRA_LARGE("xlarge")
    }
}
