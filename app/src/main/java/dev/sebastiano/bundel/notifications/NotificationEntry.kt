package dev.sebastiano.bundel.notifications

import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification

data class NotificationEntry(
    val timestamp: Long,
    val text: String? = null,
    val title: String? = null,
    val subText: String? = null,
    val titleBig: String? = null,
    val icons: Icons = Icons(),
    val senderAppInfo: SenderAppInfo,
    val originalNotification: StatusBarNotification? = null
) {

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
}
