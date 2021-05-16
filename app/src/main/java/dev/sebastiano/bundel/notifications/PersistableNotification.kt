package dev.sebastiano.bundel.notifications

import android.graphics.drawable.Icon

internal data class PersistableNotification(
    val id: Int,
    val timestamp: Long,
    val showTimestamp: Boolean = false,
    val isGroup: Boolean = false,
    val text: String? = null,
    val title: String? = null,
    val subText: String? = null,
    val titleBig: String? = null,
    val appInfo: SenderAppInfo
) {

    val uniqueId = "${appInfo.packageName}_${id}_$timestamp"

    val isNotEmpty: Boolean =
        timestamp >= 0 &&
            (
                text?.isNotBlank() == true ||
                    title?.isNotBlank() == true ||
                    subText?.isNotBlank() == true ||
                    titleBig?.isNotBlank() == true
                )

    data class SenderAppInfo(
        val packageName: String,
        val name: String? = null,
        val icon: Icon? = null
    )
}
