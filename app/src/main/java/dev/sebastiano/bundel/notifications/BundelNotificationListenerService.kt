package dev.sebastiano.bundel.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import dev.sebastiano.bundel.storage.RobertoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class BundelNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var repository: RobertoRepository

    private var isConnected = false

    override fun getActiveNotifications(): Array<StatusBarNotification> {
        Timber.d("Fetching active notifications")
        return super.getActiveNotifications()
    }

    override fun onListenerConnected() {
        isConnected = true
        val notifications = activeNotifications.mapNotNull { it.toNotificationOrNull(this) }
        _notificationsFlow.value = notifications
        runBlocking {
            for (notification in notifications) {
                repository.saveNotification(notification)
            }
        }
        Timber.i("Notifications listener connected")
    }

    override fun onListenerDisconnected() {
        isConnected = false
        Timber.i("Notifications listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Timber.i("Notification posted by ${sbn.packageName}")
        runBlocking {
            repository.saveNotification(sbn.toNotificationEntry(this@BundelNotificationListenerService))
        }
        _notificationsFlow.value = activeNotifications.mapNotNull { it.toNotificationOrNull(this) }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Timber.i("Notification removed by ${sbn.packageName}")
        runBlocking {
            val notification = sbn.toNotificationEntry(this@BundelNotificationListenerService)
            repository.deleteNotification(notification.uniqueId)
        }
        _notificationsFlow.value = activeNotifications.mapNotNull { it.toNotificationOrNull(this) }
    }

    companion object {

        private val _notificationsFlow = MutableStateFlow(emptyList<NotificationEntry>())
        val notificationsFlow: Flow<List<NotificationEntry>> = _notificationsFlow
    }
}
