package dev.sebastiano.bundel.preferences.schedule

import java.time.LocalTime

internal data class TimeRange(
    val from: LocalTime,
    val to: LocalTime
) {

    init {
        require(from < to) { "'From' must be strictly lower than 'to'" }
    }
}
