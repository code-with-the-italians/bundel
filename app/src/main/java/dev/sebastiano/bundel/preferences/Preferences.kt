package dev.sebastiano.bundel.preferences

import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.flow.Flow

internal interface Preferences {

    fun isCrashlyticsEnabled(): Flow<Boolean>
    suspend fun setIsCrashlyticsEnabled(enabled: Boolean): Boolean

    suspend fun isOnboardingSeen(): Boolean
    suspend fun setIsOnboardingSeen(enabled: Boolean): Boolean

    fun getScheduleActiveDays(): Flow<Map<WeekDay, Boolean>>
    suspend fun setScheduleActiveDays(daysSchedule: Map<WeekDay, Boolean>): Boolean

    fun getScheduleActiveHours(): Flow<TimeRangesSchedule>
    suspend fun setScheduleActiveHours(hoursSchedule: TimeRangesSchedule): Boolean
}
