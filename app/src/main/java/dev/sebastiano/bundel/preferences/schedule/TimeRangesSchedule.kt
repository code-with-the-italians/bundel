package dev.sebastiano.bundel.preferences.schedule

import java.time.LocalTime

private val LAST_AVAILABLE_MINUTE_OF_DAY = LocalTime.of(23, 59)
private const val MINIMUM_RANGE_DURATION_IN_MINUTES = 1L
private val LAST_TIME_THAT_CAN_APPEND_TO = LAST_AVAILABLE_MINUTE_OF_DAY.minusMinutes(MINIMUM_RANGE_DURATION_IN_MINUTES)

internal class TimeRangesSchedule private constructor(
    private val ranges: MutableList<TimeRange> = mutableListOf()
) {

    val canAppendAnotherRange: Boolean
        get() = last().to < LAST_TIME_THAT_CAN_APPEND_TO

    val canRemoveRanges: Boolean
        get() = size > 1

    init {
        checkAndSortRanges()
    }

    fun appendTimeRange() {
        check(canAppendAnotherRange) { "Trying to add a time range when canAppendAnotherRange is false" }

        val lastTo = last().to
        val timeRange = TimeRange(
            from = lastTo.plusMinutes(1),
            to = lastTo.plusHours(1)
        )
        ranges.add(timeRange)
        checkAndSortRanges()
    }

    operator fun get(index: Int) = ranges[index]

    fun updateRange(old: TimeRange, new: TimeRange) {
        val oldIndex = ranges.indexOf(old)
        require(oldIndex >= 0) { "Range not found: $old" }

        ranges.removeAt(oldIndex)
        ranges.add(oldIndex, new)

        checkAndSortRanges()
    }

    private fun checkAndSortRanges() {
        require(ranges.isNotEmpty()) { "There needs to be at least one range in the schedule" }

        // Sort by start time
        ranges.sortBy { it.from }

        for (index in 1 until ranges.size) {
            val current = ranges[index]
            val previous = ranges[index - 1]

            require(current.from > previous.to) { "Range at position $index start overlaps previous range" }
        }
    }

    fun removeRange(range: TimeRange) {
        check(canRemoveRanges) { "Trying to remove a range when there is only one range left in the schedule" }

        val oldIndex = ranges.indexOf(range)
        require(oldIndex >= 0) { "Range not found: $range" }

        ranges.removeAt(oldIndex)
    }

    fun last() = ranges.last()

    val size: Int
        get() = ranges.size

    companion object Factory {

        private val DEFAULT_RANGES = arrayOf(
            TimeRange(from = LocalTime.of(9, 0), to = LocalTime.of(12, 30)),
            TimeRange(from = LocalTime.of(14, 0), to = LocalTime.of(18, 0))
        )

        fun of(vararg ranges: TimeRange) =
            TimeRangesSchedule(ranges.toMutableList())

        operator fun invoke() = of(*DEFAULT_RANGES)
    }
}
