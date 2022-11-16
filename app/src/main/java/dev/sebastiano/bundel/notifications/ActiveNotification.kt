package dev.sebastiano.bundel.notifications

import android.app.PendingIntent
import android.graphics.drawable.Icon

internal data class ActiveNotification(
    val persistableNotification: PersistableNotification,
    val icons: Icons = Icons(),
    val interactions: Interactions = Interactions(),
    val isSnoozed: Boolean
) {

    val isNotEmpty: Boolean =
        persistableNotification.isNotEmpty || icons.isNotEmpty

    fun isClickable() = interactions.main != null

    data class Icons(
        val appIcon: Icon? = null,
        val small: Icon? = null,
        val large: Icon? = null,
        val extraLarge: Icon? = null
    ) {

        val isNotEmpty: Boolean = appIcon != null || small != null || large != null || extraLarge != null
    }

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
