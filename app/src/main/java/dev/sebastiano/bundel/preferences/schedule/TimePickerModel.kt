package dev.sebastiano.bundel.preferences.schedule

import java.time.LocalTime

internal enum class ExpandedRangeExtremity {
    NONE,
    FROM,
    TO
}

internal enum class PartOfHour {
    HOUR,
    MINUTE
}

internal class TimePickerModel(
    val timeRange: TimeRange,
    private val rangeExtremity: ExpandedRangeExtremity,
    private val partOfHour: PartOfHour,
    minimumAllowableFrom: LocalTime? = null,
    maximumAllowableTo: LocalTime? = null
) {

    val canIncrement = canIncrementBasedOnRange(rangeExtremity, partOfHour) &&
        canIncrementBasedOnMaximumTo(rangeExtremity, partOfHour, maximumAllowableTo)

    private fun canIncrementBasedOnRange(
        rangeExtremity: ExpandedRangeExtremity,
        partOfHour: PartOfHour
    ) = when {
        rangeExtremity == ExpandedRangeExtremity.FROM && partOfHour == PartOfHour.HOUR -> timeRange.canIncrementFromHours
        rangeExtremity == ExpandedRangeExtremity.FROM && partOfHour == PartOfHour.MINUTE -> timeRange.canIncrementFromMinutes
        rangeExtremity == ExpandedRangeExtremity.TO && partOfHour == PartOfHour.HOUR -> timeRange.canIncrementToHours
        rangeExtremity == ExpandedRangeExtremity.TO && partOfHour == PartOfHour.MINUTE -> timeRange.canIncrementToMinutes
        else -> false
    }

    private fun canIncrementBasedOnMaximumTo(
        rangeExtremity: ExpandedRangeExtremity,
        partOfHour: PartOfHour,
        maximumAllowableTo: LocalTime?
    ): Boolean {
        if (maximumAllowableTo == null) return true
        if (rangeExtremity != ExpandedRangeExtremity.TO) return true
        return when (partOfHour) {
            PartOfHour.MINUTE -> timeRange.to.plusMinutes(1) < maximumAllowableTo
            PartOfHour.HOUR -> timeRange.to.plusHours(1) < maximumAllowableTo
        }
    }

    val canDecrement = canDecrementBasedOnRange(rangeExtremity, partOfHour) &&
        canDecrementBasedOnMinimumFrom(rangeExtremity, partOfHour, minimumAllowableFrom)

    private fun canDecrementBasedOnRange(rangeExtremity: ExpandedRangeExtremity, partOfHour: PartOfHour) = when {
        rangeExtremity == ExpandedRangeExtremity.FROM && partOfHour == PartOfHour.HOUR -> timeRange.canDecrementFromHours
        rangeExtremity == ExpandedRangeExtremity.FROM && partOfHour == PartOfHour.MINUTE -> timeRange.canDecrementFromMinutes
        rangeExtremity == ExpandedRangeExtremity.TO && partOfHour == PartOfHour.HOUR -> timeRange.canDecrementToHours
        rangeExtremity == ExpandedRangeExtremity.TO && partOfHour == PartOfHour.MINUTE -> timeRange.canDecrementToMinutes
        else -> false
    }

    private fun canDecrementBasedOnMinimumFrom(
        rangeExtremity: ExpandedRangeExtremity,
        partOfHour: PartOfHour,
        minimumAllowableFrom: LocalTime?
    ): Boolean {
        if (minimumAllowableFrom == null) return true
        if (rangeExtremity != ExpandedRangeExtremity.FROM) return true
        return when (partOfHour) {
            PartOfHour.MINUTE -> timeRange.from.minusMinutes(1) > minimumAllowableFrom
            PartOfHour.HOUR -> timeRange.from.minusHours(1) > minimumAllowableFrom
        }
    }

    fun incrementTimeRangePart(): TimeRange =
        timeRange.copy(
            from = if (rangeExtremity == ExpandedRangeExtremity.FROM) {
                if (partOfHour == PartOfHour.HOUR) {
                    timeRange.from.plusHours(1)
                } else {
                    timeRange.from.plusMinutes(1)
                }
            } else {
                timeRange.from
            },
            to = if (rangeExtremity == ExpandedRangeExtremity.TO) {
                if (partOfHour == PartOfHour.HOUR) {
                    timeRange.to.plusHours(1)
                } else {
                    timeRange.to.plusMinutes(1)
                }
            } else {
                timeRange.to
            }
        )

    fun decrementTimeRangePart(): TimeRange =
        timeRange.copy(
            from = if (rangeExtremity == ExpandedRangeExtremity.FROM) {
                if (partOfHour == PartOfHour.HOUR) {
                    timeRange.from.minusHours(1)
                } else {
                    timeRange.from.minusMinutes(1)
                }
            } else {
                timeRange.from
            },
            to = if (rangeExtremity == ExpandedRangeExtremity.TO) {
                if (partOfHour == PartOfHour.HOUR) {
                    timeRange.to.minusHours(1)
                } else {
                    timeRange.to.minusMinutes(1)
                }
            } else {
                timeRange.to
            }
        )
}
