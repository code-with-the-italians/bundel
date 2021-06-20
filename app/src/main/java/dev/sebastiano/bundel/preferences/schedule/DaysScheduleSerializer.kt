package dev.sebastiano.bundel.preferences.schedule

internal object DaysScheduleSerializer {

    fun serializeToString(schedule: Map<WeekDay, Boolean>): String {
        require(schedule.isNotEmpty()) { "The schedule must not be empty" }

        return schedule.entries.joinToString(separator = ",") { "${it.key.name}=${it.value}" }
    }

    fun deserializeFromString(rawSchedule: String): Map<WeekDay, Boolean> {
        require(rawSchedule.isNotBlank()) { "The raw schedule must not be blank" }

        return rawSchedule.split(',')
            .map { entry ->
                val entryParts = entry.split('=')
                require(entryParts.size == 2) { "Entry with invalid number of parts: '$entry'" }

                entryParts
            }
            .map { entryParts ->
                val dayName = entryParts.first()
                val dayActive = entryParts.last()

                WeekDay.valueOf(dayName) to dayActive.toBooleanStrict()
            }
            .toMap()
    }
}
