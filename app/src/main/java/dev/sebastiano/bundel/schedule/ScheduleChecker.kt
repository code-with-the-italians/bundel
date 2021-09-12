package dev.sebastiano.bundel.schedule

import androidx.annotation.IntRange
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

internal object ScheduleChecker {

    private val FIFTEEN_MINUTES_IN_MILLIS = TimeUnit.MINUTES.toMillis(15).toInt()

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

    @IntRange(from = 0)
    fun calculateSnoozeDelay(
        now: LocalDateTime,
        daysSchedule: Map<WeekDay, Boolean>,
        timeRangesSchedule: TimeRangesSchedule
    ): Int {
        require(isSnoozeActive(now, daysSchedule, timeRangesSchedule)) { "Snoozing is not active now" }

        // TODO allow customising delivery frequency (default: 1h)
        val nowTime = now.toLocalTime()
        val range = timeRangesSchedule.first { it.contains(nowTime) }
        val millisDuration = Duration.between(range.from, nowTime).toMillis().toInt()
        return FIFTEEN_MINUTES_IN_MILLIS - millisDuration % FIFTEEN_MINUTES_IN_MILLIS
    }
}
