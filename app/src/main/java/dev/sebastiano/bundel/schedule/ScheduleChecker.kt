package dev.sebastiano.bundel.schedule

import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import java.time.LocalDateTime

internal object ScheduleChecker {

    fun isSnoozeActive(
        now: LocalDateTime,
        daysSchedule: Map<WeekDay, Boolean>,
        timeRangesSchedule: TimeRangesSchedule
    ): Boolean {
        val dayInSchedule = daysSchedule.entries.find { (day, _) -> day.dayOfWeek == now.dayOfWeek }
            ?: return false

        if (!dayInSchedule.value) return false

        return timeRangesSchedule.any { it.contains(now.toLocalTime()) }
    }
}
