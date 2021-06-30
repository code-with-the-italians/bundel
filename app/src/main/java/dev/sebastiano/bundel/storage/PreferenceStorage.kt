package dev.sebastiano.bundel.storage

import android.content.Context
import dev.sebastiano.bundel.preferences.schedule.DaysScheduleSerializer
import dev.sebastiano.bundel.preferences.schedule.HoursScheduleSerializer
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal interface PreferenceStorage {

    suspend fun isCrashlyticsEnabled(): Boolean
    fun isCrashlyticsEnabledFlow(): Flow<Boolean>
    suspend fun setIsCrashlyticsEnabled(enabled: Boolean): Boolean

    suspend fun isOnboardingSeen(): Boolean
    suspend fun setIsOnboardingSeen(enabled: Boolean): Boolean

    suspend fun getScheduleActiveDays(): Map<WeekDay, Boolean>
    suspend fun setScheduleActiveDays(daysSchedule: Map<WeekDay, Boolean>): Boolean

    suspend fun getScheduleActiveHours(): TimeRangesSchedule
    suspend fun setScheduleActiveHours(hoursSchedule: TimeRangesSchedule): Boolean
}

internal class SharedPreferencesStorage @Inject constructor(context: Context) : PreferenceStorage {

    private val storage by lazy { context.getSharedPreferences("preferences", Context.MODE_PRIVATE) }

    override fun isCrashlyticsEnabledFlow(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

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

    override suspend fun getScheduleActiveHours(): TimeRangesSchedule = withContext(Dispatchers.IO) {
        val rawValue = storage.getString(Keys.HOURS_SCHEDULE, null)
            ?: HoursScheduleSerializer.serializeToString(DEFAULT_HOURS_SCHEDULE)

        HoursScheduleSerializer.deserializeFromString(rawValue)
    }

    override suspend fun setScheduleActiveHours(hoursSchedule: TimeRangesSchedule): Boolean = withContext(Dispatchers.IO) {
        storage.edit().putString(Keys.HOURS_SCHEDULE, HoursScheduleSerializer.serializeToString(hoursSchedule)).commit()
    }

    private object Keys {

        const val CRASHLYTICS_ENABLED = "crashlytics"
        const val ONBOARDING_SEEN = "onboarding_seen"
        const val DAYS_SCHEDULE = "days_schedule"
        const val HOURS_SCHEDULE = "hours_schedule"
    }

    companion object {

        private val DEFAULT_DAYS_SCHEDULE = WeekDay.values().map { it to true }.toMap()

        private val DEFAULT_HOURS_SCHEDULE = TimeRangesSchedule()
    }
}
