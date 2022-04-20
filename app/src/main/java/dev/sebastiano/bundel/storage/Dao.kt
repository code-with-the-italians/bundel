package dev.sebastiano.bundel.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import dev.sebastiano.bundel.storage.model.DbAppInfo
import dev.sebastiano.bundel.storage.model.DbNotification
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class Dao {

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertNotification(notification: DbNotification)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    abstract fun getNotifications(): Flow<List<DbNotification>>

    @Query("DELETE FROM notifications")
    abstract suspend fun clearNotifications()

    @Query("DELETE FROM notifications WHERE timestamp < :olderThan")
    abstract suspend fun clearNotifications(olderThan: Long)

    @Query("DELETE FROM notifications WHERE notification_id = :notificationId")
    abstract suspend fun deleteNotificationById(notificationId: String)

    @Transaction
    open suspend fun deleteNotificationsById(ids: List<String>) {
        for (id in ids) {
            deleteNotificationById(id)
        }
    }

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAppInfo(appInfo: DbAppInfo)

    @Query("SELECT * FROM apps ORDER BY name")
    abstract fun getAppInfo(): Flow<List<DbAppInfo>>

    @Query("DELETE FROM apps")
    abstract suspend fun clearAppInfo()

    @Query("DELETE FROM apps WHERE package_name = :packageName")
    abstract suspend fun deleteAppInfoByPackageName(packageName: String)

    @Transaction
    open suspend fun deleteAppInfoByIdByPackageName(packageNames: List<String>) {
        for (packageName in packageNames) {
            deleteNotificationById(packageName)
        }
    }
}
