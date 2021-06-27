package dev.sebastiano.bundel.preferences.schedule

import java.time.LocalTime

internal object HoursScheduleSerializer {

    fun serializeToString(schedule: List<TimeRange>): String {
        require(schedule.isNotEmpty()) { "The schedule must not be empty" }

        return schedule.joinToString(separator = ",") { "${it.from.serializeToString()}–${it.to.serializeToString()}" }
    }

    private fun LocalTime.serializeToString() = "$hour:$minute"

    fun deserializeFromString(rawSchedule: String): List<TimeRange> {
        require(rawSchedule.isNotBlank()) { "The raw schedule must not be blank" }

        return rawSchedule.split(',')
            .map { entry ->
                val entryParts = entry.split('–')
                require(entryParts.size == 2) { "Entry with invalid number of parts: '$entry'" }

                entryParts
            }
            .map { entryParts ->
                entryParts.first().deserializeToHourOfDay() to entryParts.last().deserializeToHourOfDay()
            }
            .map { (from, to) -> TimeRange(from, to) }
    }

    private fun String.deserializeToHourOfDay(): LocalTime {
        val entryParts = split(':')
        require(entryParts.size == 2) { "HourOfDay with invalid number of parts: '$this'" }

        return LocalTime.of(entryParts.first().toInt(), entryParts.last().toInt())
    }
}
