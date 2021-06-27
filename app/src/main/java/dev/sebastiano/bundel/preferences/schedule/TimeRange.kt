package dev.sebastiano.bundel.preferences.schedule

import java.time.LocalTime

private val MINIMUM_TIME = LocalTime.of(0,0)
private val MAXIMUM_TIME = LocalTime.of(23,59)

internal data class TimeRange(
    val from: LocalTime,
    val to: LocalTime
) {

    init {
        require(from < to) { "'From' ($from) must be strictly smaller than 'to' ($to)" }
        require(from < MAXIMUM_TIME) { "'From' ($from) must be strictly smaller than $MAXIMUM_TIME" }
        require(to > MINIMUM_TIME) { "'To' ($from) must be strictly larger than $MINIMUM_TIME" }
    }

    val canIncreaseFromMinutes: Boolean
        get() {
            val newFrom = from.plusMinutes(1)
            return newFrom > from && newFrom < to
        }

    val canIncreaseFromHours: Boolean
        get() {
            val newFrom = from.plusHours(1)
            return newFrom > from && newFrom < to
        }

    val canDecreaseFromMinutes: Boolean
        get() {
            val newFrom = from.minusMinutes(1)
            return newFrom < from
        }

    val canDecreaseFromHours: Boolean
        get() {
            val newFrom = from.minusHours(1)
            return newFrom < from
        }

    val canIncreaseToMinutes: Boolean
        get() {
            val newTo = to.plusMinutes(1)
            return newTo > to
        }

    val canIncreaseToHours: Boolean
        get() {
            val newTo = to.plusHours(1)
            return newTo > to
        }

    val canDecreaseToMinutes: Boolean
        get() {
            val newTo = to.minusMinutes(1)
            return newTo > from
        }

    val canDecreaseToHours: Boolean
        get() {
            val newTo = to.minusHours(1)
            return newTo > from
        }
}
