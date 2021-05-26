package dev.sebastiano.bundel.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import dev.sebastiano.bundel.storage.RobertoRepository
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class BundelNotificationListenerService : NotificationListenerService(), CoroutineScope {

    @Inject
    lateinit var repository: RobertoRepository

    override val coroutineContext = SupervisorJob() + CoroutineName("BundelNotificationListenerServiceScope")

    private var isConnected = false
    private var collectJob: Job? = null

    override fun getActiveNotifications(): Array<StatusBarNotification> {
        Timber.d("Fetching active notifications")
        return super.getActiveNotifications()
    }

    override fun onListenerConnected() {
        isConnected = true
        val notifications = activeNotifications.mapNotNull { it.toActiveNotificationOrNull(this) }
        mutableNotificationsFlow.value = notifications
        runBlocking {
            for (notification in notifications) {
                repository.saveNotification(notification)
            }
        }
        Timber.i("Notifications listener connected")

        collectJob = launch {
            snoozeFlow.collect { snooze(it) }
        }
    }

    override fun onListenerDisconnected() {
        isConnected = false
        collectJob?.cancel()
        Timber.i("Notifications listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Timber.i("Notification posted by ${sbn.packageName}")
        runBlocking {
            repository.saveNotification(sbn.toActiveNotification(this@BundelNotificationListenerService))
        }
        mutableNotificationsFlow.value = activeNotifications.mapNotNull { it.toActiveNotificationOrNull(this) }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Timber.i("Notification removed by ${sbn.packageName}")
        mutableNotificationsFlow.value = activeNotifications.mapNotNull { it.toActiveNotificationOrNull(this) }
    }

    private fun snooze(key: String) {
        Timber.i("Snoozing notification '$key'")
        snoozeNotification(key, 5_000L)
    }

    companion object {

        private val mutableNotificationsFlow = MutableStateFlow(emptyList<ActiveNotification>())
        val NOTIFICATIONS_FLOW: Flow<List<ActiveNotification>> = mutableNotificationsFlow

        val snoozeFlow = MutableSharedFlow<String>()
    }
}
