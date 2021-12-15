package dev.sebastiano.bundel.glance

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import dev.sebastiano.bundel.notifications.BundelNotificationListenerService
import timber.log.Timber

class BundelAppWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget =
        CannoliWidget(numberOfItems = BundelNotificationListenerService.activeNotificationsFlow.value.size)

    companion object {

        internal suspend fun Context.updateWidgets(notificationsCount: Int) {
            Timber.i("Updating widget. Count: $notificationsCount")
            CannoliWidget(notificationsCount).updateAll(this)
        }
    }
}
