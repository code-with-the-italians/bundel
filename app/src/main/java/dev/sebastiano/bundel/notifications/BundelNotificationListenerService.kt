package dev.sebastiano.bundel.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class BundelNotificationListenerService : NotificationListenerService() {

    private var isConnected = false

    override fun getActiveNotifications(): Array<StatusBarNotification> {
        Timber.d("Fetching active notifications")
        return super.getActiveNotifications()
    }

    override fun onListenerConnected() {
        isConnected = true
        _notificationsFlow.value = activeNotifications.mapNotNull { it.toNotificationOrNull() }
        Timber.i("Notifications listener connected")
    }

    override fun onListenerDisconnected() {
        isConnected = false
        Timber.i("Notifications listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Timber.i("Notification posted by ${sbn.packageName}")
        _notificationsFlow.value = activeNotifications.mapNotNull { it.toNotificationOrNull() }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Timber.i("Notification removed by ${sbn.packageName}")
        _notificationsFlow.value = activeNotifications.mapNotNull { it.toNotificationOrNull() }
    }

    companion object {

        private val _notificationsFlow = MutableStateFlow(emptyList<Notification>())
        val notificationsFlow: Flow<List<Notification>> = _notificationsFlow
    }
}
