package dev.sebastiano.bundel.storage.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.sebastiano.bundel.notifications.PersistableNotification

@Entity(tableName = "apps")
internal data class DbAppInfo(
    @ColumnInfo("package_name") @PrimaryKey val packageName: String,
    val name: String?
) {

    companion object Factory {

        fun from(appInfo: PersistableNotification.SenderAppInfo) = DbAppInfo(
            packageName = appInfo.packageName,
            name = appInfo.name
        )
    }
}

@Entity(tableName = "notifications")
internal data class DbNotification(
    @ColumnInfo(name = "notification_id") @PrimaryKey val id: Int,
    @ColumnInfo(name = "uid") val uniqueId: String,
    @ColumnInfo(name = "notification_key") val key: String,
    val timestamp: Long,
    val showTimestamp: Boolean = false,
    val isGroup: Boolean = false,
    val text: String? = null,
    val title: String? = null,
    val subText: String? = null,
    val titleBig: String? = null,
    @ColumnInfo(name = "app_package") val appPackageName: String
) {

    fun toPersistableNotification() = PersistableNotification(
        id = id,
        key = key,
        timestamp = timestamp,
        showTimestamp = showTimestamp,
        isGroup = isGroup,
        text = text,
        title = title,
        subText = subText,
        titleBig = titleBig,
        appInfo = PersistableNotification.SenderAppInfo(
            packageName = appPackageName
        )
    )

    companion object Factory {

        fun from(notification: PersistableNotification) = DbNotification(
            id = notification.id,
            key = notification.key,
            uniqueId = notification.uniqueId,
            timestamp = notification.timestamp,
            showTimestamp = notification.showTimestamp,
            isGroup = notification.isGroup,
            text = notification.text,
            title = notification.title,
            subText = notification.subText,
            titleBig = notification.titleBig,
            appPackageName = notification.appInfo.packageName
        )
    }
}
