package dev.sebastiano.bundel.onboarding

import dev.sebastiano.bundel.preferences.DataStorePreferences
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal object DefaultsFakePreferences : Preferences {

    override fun isCrashlyticsEnabled(): Flow<Boolean> = flow {
        emit(false)
    }

    override suspend fun setIsCrashlyticsEnabled(enabled: Boolean) {
        // No-op
    }

    override fun isWinteryEasterEggEnabled(): Flow<Boolean> = flow {
        emit(false)
    }

    override suspend fun setWinteryEasterEggEnabled(enabled: Boolean) {
        // No-op
    }

    override fun getExcludedPackages(): Flow<Set<String>> = flow {
        emit(emptySet())
    }

    override suspend fun setExcludedPackages(excludedPackages: Set<String>) {
        // No-op
    }

    override suspend fun isOnboardingSeen() = true

    override suspend fun setIsOnboardingSeen(onboardingSeen: Boolean) {
        // No-op
    }

    override fun getDaysSchedule(): Flow<Map<WeekDay, Boolean>> = flow {
        emit(DataStorePreferences.DEFAULT_DAYS_SCHEDULE)
    }

    override suspend fun setDaysSchedule(daysSchedule: Map<WeekDay, Boolean>) {
        // No-op
    }

    override fun getTimeRangesSchedule(): Flow<TimeRangesSchedule> = flow {
        emit(DataStorePreferences.DEFAULT_HOURS_SCHEDULE)
    }

    override suspend fun setTimeRangesSchedule(timeRangesSchedule: TimeRangesSchedule) {
        // No-op
    }

    override fun getSnoozeWindowDurationSeconds(): Flow<Int> = flow {
        emit(DataStorePreferences.DEFAULT_SNOOZE_WINDOW_DURATION_SECONDS)
    }

    override suspend fun setSnoozeWindowDurationSeconds(duration: Int) {
        // No-op
    }
}
