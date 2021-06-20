package dev.sebastiano.bundel.storage

import android.content.Context
import dev.sebastiano.bundel.preferences.schedule.DaysScheduleSerializer
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal interface PreferenceStorage {

    suspend fun isCrashlyticsEnabled(): Boolean
    suspend fun setIsCrashlyticsEnabled(enabled: Boolean): Boolean

    suspend fun isOnboardingSeen(): Boolean
    suspend fun setIsOnboardingSeen(enabled: Boolean): Boolean

    suspend fun getScheduleActiveDays(): Map<WeekDay, Boolean>
    suspend fun setScheduleActiveDays(daysSchedule: Map<WeekDay, Boolean>): Boolean
}

internal class SharedPreferencesStorage @Inject constructor(context: Context) : PreferenceStorage {

    private val storage by lazy { context.getSharedPreferences("preferences", Context.MODE_PRIVATE) }

    override suspend fun isCrashlyticsEnabled(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean(Keys.CRASHLYTICS_ENABLED, false)
    }

    override suspend fun setIsCrashlyticsEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        storage.edit().putBoolean(Keys.CRASHLYTICS_ENABLED, enabled).commit()
    }

    override suspend fun isOnboardingSeen(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean(Keys.ONBOARDING_SEEN, false)
    }

    override suspend fun setIsOnboardingSeen(enabled: Boolean): Boolean = withContext(Dispatchers.IO) {
        storage.edit().putBoolean(Keys.ONBOARDING_SEEN, enabled).commit()
    }

    override suspend fun getScheduleActiveDays(): Map<WeekDay, Boolean> = withContext(Dispatchers.IO) {
        val rawValue = storage.getString(Keys.DAYS_SCHEDULE, null)
            ?: DaysScheduleSerializer.serializeToString(DEFAULT_DAYS_SCHEDULE)

        DaysScheduleSerializer.deserializeFromString(rawValue)
    }

    override suspend fun setScheduleActiveDays(daysSchedule: Map<WeekDay, Boolean>): Boolean = withContext(Dispatchers.IO) {
        storage.edit().putString(Keys.DAYS_SCHEDULE, DaysScheduleSerializer.serializeToString(daysSchedule)).commit()
    }

    private object Keys {

        const val CRASHLYTICS_ENABLED = "crashlytics"
        const val ONBOARDING_SEEN = "onboarding_seen"
        const val DAYS_SCHEDULE = "days_schedule"
    }

    companion object {

        private val DEFAULT_DAYS_SCHEDULE = WeekDay.values().map { it to true }.toMap()
    }
}
