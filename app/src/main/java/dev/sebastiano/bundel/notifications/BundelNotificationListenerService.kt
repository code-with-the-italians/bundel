package dev.sebastiano.bundel.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import dev.sebastiano.bundel.glance.BundelAppWidgetReceiver.Companion.updateWidgets
import dev.sebastiano.bundel.storage.DataRepository
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class BundelNotificationListenerService : NotificationListenerService(), CoroutineScope {

    @Inject
    lateinit var repository: DataRepository

    override val coroutineContext = SupervisorJob() + CoroutineName("BundelNotificationListenerServiceScope")

    private var isConnected = false
    private var collectJob: Job? = null

    override fun getActiveNotifications(): Array<StatusBarNotification> {
        Timber.d("Fetching active notifications")
        return super.getActiveNotifications()
    }

    override fun onListenerConnected() {
        Timber.d("Notifications listener connected")
        isConnected = true
        val notifications = activeNotifications.mapNotNull { it.toActiveNotificationOrNull(this) }
        mutableNotificationsFlow.value = notifications
        runBlocking {
            for (notification in notifications) {
                repository.saveNotification(notification)
            }
        }

        launch {
            updateWidgets(notificationsCount = notifications.filterNot { it.persistableNotification.isGroup }.size)
        }

        collectJob = launch {
            snoozedNotificationsFlow.collect {
                snooze(it)
            }
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

        val activeNotification = sbn.toActiveNotificationOrNull(this)
        if (activeNotification != null) {
            val currentNotifications = mutableNotificationsFlow.value.toMutableList()
            val existingNotification = currentNotifications.find { it.persistableNotification.key == sbn.key }
            if (existingNotification != null) {
                // Exists already, likely snoozed, replace it
                currentNotifications -= existingNotification
                currentNotifications += activeNotification
            } else {
                currentNotifications += activeNotification
            }

            launch {
                updateWidgets(notificationsCount = currentNotifications.filterNot { it.persistableNotification.isGroup }.size)
            }

            mutableNotificationsFlow.value = currentNotifications
                .distinctBy { it.persistableNotification.uniqueId }
                .sortedByDescending { it.persistableNotification.timestamp }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Timber.i("Notification removed by ${sbn.packageName}")
        val existing = mutableNotificationsFlow.value.find { it.persistableNotification.key == sbn.key }
            ?: return

        if (existing.isSnoozed) {
            Timber.i("Notification is snoozed, not really removing it")
            return
        }
        mutableNotificationsFlow.value = mutableNotificationsFlow.value - existing

        launch {
            updateWidgets(notificationsCount = mutableNotificationsFlow.value.filterNot { it.persistableNotification.isGroup }.size)
        }
    }

    private fun snooze(snoozedNotification: SnoozedNotification) {
        Timber.i("Snoozing notification '${snoozedNotification.kettle}' by ${snoozedNotification.durationMillis} millis")
        mutableNotificationsFlow.value = mutableNotificationsFlow.value.map {
            if (it.persistableNotification.key == snoozedNotification.kettle) {
                it.copy(isSnoozed = true)
            } else {
                it
            }
        }
        snoozeNotification(snoozedNotification.kettle, snoozedNotification.durationMillis.toLong())
    }

    companion object {

        private val mutableNotificationsFlow = MutableStateFlow(emptyList<ActiveNotification>())
        private val snoozedNotificationsFlow = MutableSharedFlow<SnoozedNotification>()

        val activeNotificationsFlow: StateFlow<List<ActiveNotification>> = mutableNotificationsFlow

        suspend fun snooze(notification: ActiveNotification, durationMillis: Int = 5_000) {
            snoozedNotificationsFlow.emit(SnoozedNotification(notification.persistableNotification.key, durationMillis))
        }

        private data class SnoozedNotification(
            val kettle: String, // aka fuck you kettles, it's supposed to be the notification key
            val durationMillis: Int
        )
    }
}
