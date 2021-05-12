package dev.sebastiano.bundel.notifications

import android.app.PendingIntent
import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification

data class NotificationEntry(
    val timestamp: Long,
    val showTimestamp: Boolean = false,
    val isGroup: Boolean = false,
    val text: String? = null,
    val title: String? = null,
    val subText: String? = null,
    val titleBig: String? = null,
    val icons: Icons = Icons(),
    val appInfo: SenderAppInfo,
    val interactions: Interactions = Interactions(),
    val originalNotification: StatusBarNotification? = null
) {

    val uniqueId = "${appInfo.packageName}_${originalNotification?.id ?: "no-id"}_$timestamp"

    fun isClickable() = interactions.main != null

    val isNotEmpty: Boolean =
        timestamp >= 0 &&
            (
                text?.isNotBlank() == true ||
                    title?.isNotBlank() == true ||
                    subText?.isNotBlank() == true ||
                    titleBig?.isNotBlank() == true ||
                    icons.isNotEmpty
                )

    data class Icons(
        val small: Icon? = null,
        val large: Icon? = null,
        val extraLarge: Icon? = null,
    ) {

        val isNotEmpty: Boolean = small != null || large != null || extraLarge != null
    }

    data class SenderAppInfo(
        val packageName: String,
        val name: String,
        val icon: Icon? = null
    )

    data class Interactions(
        val main: PendingIntent? = null,
        val dismiss: PendingIntent? = null,
        val actions: List<ActionItem> = emptyList()
    ) {

        data class ActionItem(
            val text: CharSequence,
            val icon: Icon? = null,
            val pendingIntent: PendingIntent? = null
        )
    }
}
