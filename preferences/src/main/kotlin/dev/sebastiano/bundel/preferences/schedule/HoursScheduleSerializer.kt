package dev.sebastiano.bundel.preferences.schedule

import dev.sebastiano.bundel.ui.composables.TimeRange
import java.time.LocalTime

internal object HoursScheduleSerializer {

    fun serializeToString(schedule: TimeRangesSchedule): String {
        require(schedule.timeRanges.isNotEmpty()) { "The schedule must not be empty" }

        return schedule.timeRanges.joinToString(separator = ",") { "${it.from.serializeToString()}–${it.to.serializeToString()}" }
    }

    private fun LocalTime.serializeToString() = "$hour:$minute"

    fun deserializeFromString(rawSchedule: String): TimeRangesSchedule {
        require(rawSchedule.isNotBlank()) { "The raw schedule must not be blank" }

        val timeRanges = rawSchedule.split(',')
            .map { entry ->
                val entryParts = entry.split('–')
                require(entryParts.size == 2) { "Entry with invalid number of parts: '$entry'" }

                entryParts
            }
            .map { entryParts ->
                entryParts.first().deserializeToHourOfDay() to entryParts.last().deserializeToHourOfDay()
            }
            .map { (from, to) -> TimeRange(from, to) }

        return TimeRangesSchedule.of(*timeRanges.toTypedArray())
    }

    private fun String.deserializeToHourOfDay(): LocalTime {
        val entryParts = split(':')
        require(entryParts.size == 2) { "HourOfDay with invalid number of parts: '$this'" }

        return LocalTime.of(entryParts.first().toInt(), entryParts.last().toInt())
    }
}
