package dev.sebastiano.bundel.preferences

import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.flow.Flow

internal interface Preferences {

    fun isCrashlyticsEnabled(): Flow<Boolean>
    suspend fun setIsCrashlyticsEnabled(enabled: Boolean)

    suspend fun isOnboardingSeen(): Boolean
    suspend fun setIsOnboardingSeen(onboardingSeen: Boolean)

    fun getDaysSchedule(): Flow<Map<WeekDay, Boolean>>
    suspend fun setDaysSchedule(daysSchedule: Map<WeekDay, Boolean>)

    fun getTimeRangesSchedule(): Flow<TimeRangesSchedule>
    suspend fun setTimeRangesSchedule(timeRangesSchedule: TimeRangesSchedule)
}
