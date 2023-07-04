package dev.sebastiano.bundel.preferences

import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sebastiano.bundel.ui.R.drawable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DebugPreferencesViewModel @Inject constructor(
    private val preferences: Preferences
) : ViewModel() {

    val useShortSnoozeWindow: Flow<Boolean> = preferences.getSnoozeWindowDurationSeconds()
        .map { it != DataStorePreferences.DEFAULT_SNOOZE_WINDOW_DURATION_SECONDS }

    fun setUseShortSnoozeWindow(enabled: Boolean) {
        viewModelScope.launch {
            val duration = if (enabled) shortSnoozeWindowDurationSeconds else DataStorePreferences.DEFAULT_SNOOZE_WINDOW_DURATION_SECONDS
            preferences.setSnoozeWindowDurationSeconds(duration)
        }
    }

    fun postTestNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        val random = Random.nextInt(1, 1000)
        val channel = NotificationChannelCompat.Builder("test", NotificationManager.IMPORTANCE_DEFAULT)
            .setName(context.getString(R.string.channel_test_notifications_name))
            .setDescription(context.getString(R.string.channel_test_notifications_description))
            .build()
        notificationManager.createNotificationChannel(channel)

        val id = Random.nextInt()
        val largeIcon = (ResourcesCompat.getDrawable(context.resources, drawable.outline_interests_black_48dp, null) as BitmapDrawable).bitmap
        val notification = NotificationCompat.Builder(context, channel.id)
            .setContentTitle(context.getString(R.string.debug_notification_title, random))
            .setContentText(context.getString(R.string.debug_notification_text))
            .setSmallIcon(IconCompat.createWithResource(context, drawable.ic_bundel_icon))
            .setLargeIcon(largeIcon)
            .build()
        notificationManager.notify(id, notification)
    }

    companion object {

        private const val shortSnoozeWindowDurationSeconds = 15
    }
}
