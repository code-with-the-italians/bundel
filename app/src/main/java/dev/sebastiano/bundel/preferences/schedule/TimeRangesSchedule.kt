package dev.sebastiano.bundel.preferences.schedule

import java.time.LocalTime

private val LAST_AVAILABLE_TIME_OF_DAY = LocalTime.of(23, 59)
private const val MINIMUM_RANGE_DURATION_IN_MINUTES = 1L
private val LAST_TIME_THAT_CAN_APPEND_TO = LAST_AVAILABLE_TIME_OF_DAY.minusMinutes(MINIMUM_RANGE_DURATION_IN_MINUTES)

internal class TimeRangesSchedule private constructor(
    private val ranges: List<TimeRange>
) : List<TimeRange> by ranges {

    val canAppendAnotherRange: Boolean
        get() = last().to < LAST_TIME_THAT_CAN_APPEND_TO

    val canRemoveRanges: Boolean
        get() = size > 1

    val timeRanges: List<TimeRange> = ranges

    fun appendTimeRange(): TimeRangesSchedule {
        check(canAppendAnotherRange) { "Trying to add a time range when canAppendAnotherRange is false" }

        val lastTo = last().to
        val newTo = lastTo.plusHours(1)
        val timeRange = TimeRange(
            from = lastTo.plusMinutes(1),
            to = if (newTo < lastTo) LAST_AVAILABLE_TIME_OF_DAY else newTo
        )
        return of(ranges + timeRange)
    }

    fun updateRange(old: TimeRange, new: TimeRange): TimeRangesSchedule {
        val oldIndex = ranges.indexOf(old)
        require(oldIndex >= 0) { "Range not found: $old" }

        val newRanges = ranges.toMutableList()
        newRanges.removeAt(oldIndex)
        newRanges.add(oldIndex, new)

        return of(newRanges)
    }

    fun removeRange(range: TimeRange): TimeRangesSchedule {
        check(canRemoveRanges) { "Trying to remove a range when there is only one range left in the schedule" }

        val oldIndex = ranges.indexOf(range)
        require(oldIndex >= 0) { "Range not found: $range" }

        val newRanges = ranges.toMutableList()
        newRanges.removeAt(oldIndex)

        return of(newRanges)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeRangesSchedule) return false

        if (ranges != other.ranges) return false

        return true
    }

    override fun hashCode(): Int = ranges.hashCode()

    companion object Factory {

        private val DEFAULT_RANGES = arrayOf(
            TimeRange(from = LocalTime.of(9, 0), to = LocalTime.of(12, 30)),
            TimeRange(from = LocalTime.of(14, 0), to = LocalTime.of(18, 0))
        )

        fun of(ranges: List<TimeRange>): TimeRangesSchedule = of(*ranges.toTypedArray())

        fun of(vararg ranges: TimeRange): TimeRangesSchedule {
            require(ranges.isNotEmpty()) { "There needs to be at least one range in the schedule" }

            // Sort by start time
            val sortedRanges = ranges.sortedBy { it.from }

            for (index in 1 until sortedRanges.size) {
                val current = sortedRanges[index]
                val previous = sortedRanges[index - 1]

                require(current.from > previous.to) { "The FROM of range at position $index ($current) overlaps previous range ($previous)" }
            }

            return TimeRangesSchedule(sortedRanges.toList())
        }

        operator fun invoke() = of(*DEFAULT_RANGES)
    }
}
