package dev.sebastiano.bundel.preferences.schedule

import androidx.annotation.IntRange

internal data class TimeRange(
    val from: HourOfDay,
    val to: HourOfDay
) {

    data class HourOfDay(
        @IntRange(from = 0, to = 23) val hour: Int,
        @IntRange(from = 0, to = 59) val minute: Int
    ) {

        // TODO wrap a LocalTime instead of doing maths ourselves

        @Suppress("MagicNumber") // We should not depend on this crap logic anyway
        fun plusMinutes(minutes: Int): HourOfDay {
            val newMinutes = minute + minutes
            return if (newMinutes <= 59) {
                HourOfDay(hour, newMinutes)
            } else {
                HourOfDay(hour + newMinutes / 60, newMinutes % 60)
            }
        }

        fun minusMinutes(minutes: Int): HourOfDay = copy(minute = (minute - minutes).coerceAtLeast(0))

        fun plusHours(hours: Int): HourOfDay = copy(hour = (hour + hours).coerceAtMost(23))

        fun minusHours(hours: Int): HourOfDay = copy(hour = (hour - hours).coerceAtLeast(0))
    }
}
