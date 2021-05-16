package dev.sebastiano.bundel.storage

import dev.sebastiano.bundel.notifications.PersistableNotification
import dev.sebastiano.bundel.storage.model.DbNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RobertoRepository @Inject constructor(
    private val database: RobertoDatabase
) {

    suspend fun saveNotification(persistableNotification: PersistableNotification) {
        withContext(Dispatchers.IO) {
            database.robertooo().insertNotification(
                DbNotification.from(persistableNotification)
            )
        }
    }

    fun getNotifications() =
        database.robertooo()
            .getNotifications()
            .map { dbNotifications -> dbNotifications.map { it.toPersistableNotification() } }

    suspend fun deleteNotification(notificationId: String) {
        withContext(Dispatchers.IO) {
            database.robertooo().deleteNotificationById(notificationId)
        }
    }

    private suspend fun deleteNotifications(notificationIds: List<String>) {
        withContext(Dispatchers.IO) {
            database.robertooo().deleteNotificationsById(notificationIds)
        }
    }
}
