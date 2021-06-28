package dev.sebastiano.bundel.preferences.schedule

import java.time.LocalTime

private val MINIMUM_TIME = LocalTime.of(0, 0)
private val MAXIMUM_TIME = LocalTime.of(23, 59)

internal data class TimeRange(
    val from: LocalTime,
    val to: LocalTime
) {

    init {
        require(from < to) { "'From' ($from) must be strictly smaller than 'to' ($to)" }
        require(from < MAXIMUM_TIME) { "'From' ($from) must be strictly smaller than $MAXIMUM_TIME" }
        require(to > MINIMUM_TIME) { "'To' ($from) must be strictly larger than $MINIMUM_TIME" }
    }

    val canIncrementFromMinutes: Boolean
        get() {
            val newFrom = from.plusMinutes(1)
            return newFrom > from && newFrom < to
        }

    val canIncrementFromHours: Boolean
        get() {
            val newFrom = from.plusHours(1)
            return newFrom > from && newFrom < to
        }

    val canDecrementFromMinutes: Boolean
        get() {
            val newFrom = from.minusMinutes(1)
            return newFrom < from
        }

    val canDecrementFromHours: Boolean
        get() {
            val newFrom = from.minusHours(1)
            return newFrom < from
        }

    val canIncrementToMinutes: Boolean
        get() {
            val newTo = to.plusMinutes(1)
            return newTo > to
        }

    val canIncrementToHours: Boolean
        get() {
            val newTo = to.plusHours(1)
            return newTo > to
        }

    val canDecrementToMinutes: Boolean
        get() {
            val newTo = to.minusMinutes(1)
            return newTo > from && to.minute > 0
        }

    val canDecrementToHours: Boolean
        get() {
            val newTo = to.minusHours(1)
            return newTo > from && to.hour > 0
        }
}
