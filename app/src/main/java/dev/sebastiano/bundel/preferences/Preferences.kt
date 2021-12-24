package dev.sebastiano.bundel.preferences

import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.flow.Flow

internal interface Preferences {

    fun isCrashlyticsEnabled(): Flow<Boolean>
    suspend fun setIsCrashlyticsEnabled(enabled: Boolean)

    fun isWinteryEasterEggEnabled(): Flow<Boolean>
    suspend fun setWinteryEasterEggEnabled(enabled: Boolean)

    fun getExcludedPackages(): Flow<Set<String>>
    suspend fun setExcludedPackages(excludedPackages: Set<String>)

    suspend fun isOnboardingSeen(): Boolean
    suspend fun setIsOnboardingSeen(onboardingSeen: Boolean)

    fun getDaysSchedule(): Flow<Map<WeekDay, Boolean>>
    suspend fun setDaysSchedule(daysSchedule: Map<WeekDay, Boolean>)

    fun getTimeRangesSchedule(): Flow<TimeRangesSchedule>
    suspend fun setTimeRangesSchedule(timeRangesSchedule: TimeRangesSchedule)
}
