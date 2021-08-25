package dev.sebastiano.bundel.storage

import dev.sebastiano.bundel.notifications.ActiveNotification
import dev.sebastiano.bundel.storage.model.DbNotification
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DataRepository @Inject constructor(
    private val database: RobertoDatabase,
    private val imagesStorage: ImagesStorage
) {

    suspend fun saveNotification(activeNotification: ActiveNotification) {
        database.dao().insertNotification(DbNotification.from(activeNotification.persistableNotification))
        imagesStorage.saveIconsFrom(activeNotification)
    }

    fun getNotifications() =
        database.dao()
            .getNotifications()
            .map { dbNotifications -> dbNotifications.map { it.toPersistableNotification() } }

    suspend fun deleteNotification(notificationUniqueId: String) {
        database.dao().deleteNotificationById(notificationUniqueId)
        cleanupIconsFor(notificationUniqueId)
    }

    suspend fun deleteNotifications(notificationUniqueIds: List<String>) {
        database.dao().deleteNotificationsById(notificationUniqueIds)
        for (notificationUniqueId in notificationUniqueIds) {
            cleanupIconsFor(notificationUniqueId)
        }
    }

    private suspend fun cleanupIconsFor(notificationUniqueId: String) {
        imagesStorage.deleteIconsFor(notificationUniqueId)
        // TODO clean up app icons when no more notifications use them
    }
}
