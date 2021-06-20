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

        @Suppress("MagicNumber") // We should not depende on this crap logic anyway
        fun plusMinutes(minutes: Int): HourOfDay {
            val newMinutes = minute + minutes
            return if (newMinutes <= 59) {
                HourOfDay(hour, newMinutes)
            } else {
                HourOfDay(hour + newMinutes / 60, newMinutes % 60)
            }
        }
    }
}
