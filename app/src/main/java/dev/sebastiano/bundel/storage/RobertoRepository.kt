package dev.sebastiano.bundel.storage

import android.app.Application
import dev.sebastiano.bundel.notifications.NotificationEntry
import dev.sebastiano.bundel.notifications.toNotificationEntry
import dev.sebastiano.bundel.storage.model.DbNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RobertoRepository @Inject constructor(
    private val application: Application,
    private val database: RobertoDatabase,
    private val cache: NotificationsCache
) {

    suspend fun saveNotification(notification: NotificationEntry) {
        val statusBarNotification = requireNotNull(notification.originalNotification) {
            "Only notification entries with a StatusBarNotification can be stored."
        }
        withContext(Dispatchers.IO) {
            cache.storeStatusBarNotification(statusBarNotification, notification.uniqueId)
            database.robertooo().insertNotification(
                DbNotification(
                    notificationId = notification.uniqueId,
                    timestamp = notification.timestamp,
                    appPackageName = notification.appInfo.packageName
                )
            )
        }
    }

    suspend fun getNotifications() = coroutineScope {
        withContext(Dispatchers.IO) {
            database.robertooo()
                .getNotifications()
        }
            // .conflate() TODO?
            .map { dbNotifications ->
                val idsToPrune = mutableListOf<String>()
                val notifications = withContext(Dispatchers.IO) {
                    dbNotifications.mapNotNull { dbNotification ->
                        try {
                            cache.getStatusBarNotification(dbNotification.notificationId)
                        } catch (ignored: IllegalArgumentException) {
                            idsToPrune += dbNotification.notificationId
                            null
                        }
                    }.map { it.toNotificationEntry(application) }
                }

                if (idsToPrune.isNotEmpty()) {
                    launch { deleteNotifications(idsToPrune) }
                }

                notifications
            }
    }

    suspend fun deleteNotification(notificationId: String) {
        withContext(Dispatchers.IO) {
            database.robertooo().deleteNotificationById(notificationId)
            cache.deleteNotification(notificationId)
        }
    }

    private suspend fun deleteNotifications(notificationIds: List<String>) {
        withContext(Dispatchers.IO) {
            database.robertooo().deleteNotificationsById(notificationIds)
            for (id in notificationIds) {
                cache.deleteNotification(id)
            }
        }
    }
}
