package dev.sebastiano.bundel

import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

internal class FakePreferences : Preferences {

    val crashlyticsEnabled = MutableStateFlow(false)

    override fun isCrashlyticsEnabled() = crashlyticsEnabled

    override suspend fun setIsCrashlyticsEnabled(enabled: Boolean) {
        crashlyticsEnabled.emit(enabled)
    }

    override fun isWinteryEasterEggEnabled() = MutableStateFlow(false)

    override suspend fun setWinteryEasterEggEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getExcludedPackages(): Flow<Set<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun setExcludedPackages(excludedPackages: Set<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun isOnboardingSeen() = false

    override suspend fun setIsOnboardingSeen(onboardingSeen: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getDaysSchedule() = MutableStateFlow(emptyMap<WeekDay, Boolean>())

    override suspend fun setDaysSchedule(daysSchedule: Map<WeekDay, Boolean>) {
        TODO("Not yet implemented")
    }

    override fun getTimeRangesSchedule() = MutableStateFlow(TimeRangesSchedule())

    override suspend fun setTimeRangesSchedule(timeRangesSchedule: TimeRangesSchedule) {
        TODO("Not yet implemented")
    }

    override fun getSnoozeWindowDurationSeconds() = MutableStateFlow(21)

    override suspend fun setSnoozeWindowDurationSeconds(duration: Int) {
        TODO("Not yet implemented")
    }
}
