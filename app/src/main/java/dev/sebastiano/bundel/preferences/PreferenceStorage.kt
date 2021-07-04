package dev.sebastiano.bundel.preferences

import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.flow.Flow

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
