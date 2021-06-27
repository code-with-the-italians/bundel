package dev.sebastiano.bundel.preferences.schedule

import assertk.Assert
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import assertk.assertions.prop
import assertk.assertions.support.expected
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalTime

internal class TimeRangesScheduleTest {

    private val schedule = TimeRangesSchedule()

    @Nested
    inner class Initialization {

        @Test
        internal fun `should throw IAE when creating an empty instance`() {
            assertThat { TimeRangesSchedule.of() }.isFailure()
                .isInstanceOf(IllegalArgumentException::class)
        }

        @Test
        internal fun `should throw IAE when creating an instance with overlapping ranges`() {
            val someRange = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val someOtherRange = TimeRange(from = LocalTime.of(11, 0), to = LocalTime.of(12, 0))
            assertThat { TimeRangesSchedule.of(someRange, someOtherRange) }.isFailure()
                .isInstanceOf(IllegalArgumentException::class)
        }

        @Test
        internal fun `should add 9 to 12_30 and 14 to 18 to a new TimeRangeSchedule`() {
            assertAll {
                assertThat(schedule[0])
                    .hasFrom(LocalTime.of(9, 0))
                    .hasTo(LocalTime.of(12, 30))
                assertThat(schedule[1])
                    .hasFrom(LocalTime.of(14, 0))
                    .hasTo(LocalTime.of(18, 0))
            }
        }

        @Test
        internal fun `should sort ranges when creating a new TimeRangeSchedule`() {
            val range1 = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val range2 = TimeRange(from = LocalTime.of(12, 0), to = LocalTime.of(13, 0))
            val range3 = TimeRange(from = LocalTime.of(14, 0), to = LocalTime.of(15, 0))

            val schedule = TimeRangesSchedule.of(range3, range1, range2)

            assertAll {
                assertThat(schedule).hasSize(3)
                assertThat(schedule[0]).isEqualTo(range1)
                assertThat(schedule[1]).isEqualTo(range2)
                assertThat(schedule[2]).isEqualTo(range3)
            }
        }
    }

    @Nested
    inner class Appending {

        @Test
        internal fun `should append a TimeRange that starts immediately after the last one, and lasts 59 minutes`() {
            schedule.appendTimeRange()
            assertThat(schedule.last())
                .hasFrom(LocalTime.of(18, 1))
                .hasTo(LocalTime.of(19, 0))
        }

        @Test
        internal fun `should throw ISE when appending a Range if canAppendAnotherRange is false`() {
            val scheduleEndingAtEndOfDay = TimeRangesSchedule.of(
                TimeRange(from = LocalTime.of(18, 1), to = LocalTime.of(23, 59))
            )

            check(!scheduleEndingAtEndOfDay.canAppendAnotherRange) { "Test precondition failed: canAppendAnotherRange is true" }

            assertThat { scheduleEndingAtEndOfDay.appendTimeRange() }.isFailure()
                .isInstanceOf(IllegalStateException::class)
        }
    }

    @Nested
    inner class Updating {

        @Test
        internal fun `should throw IAE when updating a range that isn't in the list`() {
            val someRange = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val someNewRange = TimeRange(from = LocalTime.of(10, 1), to = LocalTime.of(11, 0))
            assertThat { schedule.updateRange(someRange, someNewRange) }.isFailure()
                .isInstanceOf(IllegalArgumentException::class)
        }

        @Test
        internal fun `should replace the old value with the new one when there is only one range`() {
            val someRange = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val someNewRange = TimeRange(from = LocalTime.of(10, 1), to = LocalTime.of(11, 0))
            val scheduleWithOneRange = TimeRangesSchedule.of(someRange)

            scheduleWithOneRange.updateRange(someRange, someNewRange)

            assertAll {
                assertThat(scheduleWithOneRange).hasSize(1)
                assertThat(scheduleWithOneRange.last()).isEqualTo(someNewRange)
            }
        }

        @Test
        internal fun `should throw IAE if the new range overlaps the next one`() {
            val someRange = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val someOtherRange = TimeRange(from = LocalTime.of(12, 0), to = LocalTime.of(13, 0))
            val someNewRange = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(12, 0))

            val schedule = TimeRangesSchedule.of(someRange, someOtherRange)

            assertThat { schedule.updateRange(someRange, someNewRange) }.isFailure()
                .isInstanceOf(IllegalArgumentException::class)
        }

        @Test
        internal fun `should throw IAE if the new range overlaps the previous one`() {
            val someRange = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val someOtherRange = TimeRange(from = LocalTime.of(12, 0), to = LocalTime.of(13, 0))
            val someNewOtherRange = TimeRange(from = LocalTime.of(11, 0), to = LocalTime.of(13, 0))

            val schedule = TimeRangesSchedule.of(someRange, someOtherRange)

            assertThat { schedule.updateRange(someRange, someNewOtherRange) }.isFailure()
                .isInstanceOf(IllegalArgumentException::class)
        }

        @Test
        internal fun `should sort ranges when updating if order changed`() {
            val range1 = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val range2 = TimeRange(from = LocalTime.of(12, 0), to = LocalTime.of(13, 0))
            val range3 = TimeRange(from = LocalTime.of(14, 0), to = LocalTime.of(15, 0))
            val newRange = TimeRange(from = LocalTime.of(16, 0), to = LocalTime.of(17, 0))

            val schedule = TimeRangesSchedule.of(range1, range2, range3)
            schedule.updateRange(range2, newRange)

            assertAll {
                assertThat(schedule).hasSize(3)
                assertThat(schedule[0]).isEqualTo(range1)
                assertThat(schedule[1]).isEqualTo(range3)
                assertThat(schedule.last()).isEqualTo(newRange)
            }
        }
    }

    @Nested
    inner class Removing {

        @Test
        internal fun `should throw ISE when trying to remove items if canRemoveRanges is false`() {
            val someRange = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val scheduleWithOneRange = TimeRangesSchedule.of(someRange)

            check(!scheduleWithOneRange.canRemoveRanges) { "Test precondition failed: canRemoveRange is true" }

            assertThat { scheduleWithOneRange.removeRange(someRange) }.isFailure()
                .isInstanceOf(IllegalStateException::class)
        }

        @Test
        internal fun `should throw IAE when trying to remove an item that doesn't exist in the list`() {
            val someRange = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val someOtherRange = TimeRange(from = LocalTime.of(13, 0), to = LocalTime.of(14, 0))

            val schedule = TimeRangesSchedule.of(someRange)

            assertThat { schedule.removeRange(someOtherRange) }.isFailure()
                .isInstanceOf(IllegalStateException::class)
        }

        @Test
        internal fun `should remove a range when it exists and canRemoveRanges is true`() {
            val someRange = TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0))
            val someOtherRange = TimeRange(from = LocalTime.of(12, 0), to = LocalTime.of(13, 0))

            val schedule = TimeRangesSchedule.of(someRange, someOtherRange)
            check(schedule.canRemoveRanges) { "Test precondition failed: canRemoveRange is false" }

            schedule.removeRange(someRange)

            assertAll {
                assertThat(schedule).hasSize(1)
                assertThat(schedule.last()).isEqualTo(someOtherRange)
            }
        }
    }

    @Nested
    inner class CanAppendAnotherRange {

        @Test
        internal fun `should have canAppendAnotherRange true on a new instance`() {
            assertThat(schedule).prop(TimeRangesSchedule::canAppendAnotherRange).isTrue()
        }

        @Test
        internal fun `should have canAppendAnotherRange true when an appended range would be at least 2 minutes`() {
            val scheduleWithMoreRoom = TimeRangesSchedule.of(
                TimeRange(from = LocalTime.of(18, 1), to = LocalTime.of(23, 57))
            )

            assertThat(scheduleWithMoreRoom).prop(TimeRangesSchedule::canAppendAnotherRange).isTrue()
        }

        @Test
        internal fun `should have canAppendAnotherRange false when an appended range would be lass than 2 minutes`() {
            val scheduleWithMoreRoom = TimeRangesSchedule.of(
                TimeRange(from = LocalTime.of(18, 1), to = LocalTime.of(23, 58))
            )

            assertThat(scheduleWithMoreRoom).prop(TimeRangesSchedule::canAppendAnotherRange).isFalse()
        }
    }

    @Nested
    inner class CanRemoveRanges {

        @Test
        internal fun `should have canRemoveRanges true on a new instance`() {
            assertThat(schedule).prop(TimeRangesSchedule::canRemoveRanges).isTrue()
        }

        @Test
        internal fun `should have canRemoveRanges true when there are two or more ranges in the schedule`() {
            val scheduleWithTwoRanges = TimeRangesSchedule.of(
                TimeRange(from = LocalTime.of(10, 0), to = LocalTime.of(11, 0)),
                TimeRange(from = LocalTime.of(12, 0), to = LocalTime.of(13, 0))
            )

            assertThat(scheduleWithTwoRanges).prop(TimeRangesSchedule::canRemoveRanges).isTrue()
        }

        @Test
        internal fun `should have canRemoveRanges false when there is only one range in the schedule`() {
            val scheduleWithOnlyOneRange = TimeRangesSchedule.of(
                TimeRange(from = LocalTime.of(18, 1), to = LocalTime.of(23, 58))
            )

            assertThat(scheduleWithOnlyOneRange).prop(TimeRangesSchedule::canRemoveRanges).isFalse()
        }
    }
}

private fun Assert<TimeRange>.hasFrom(expected: LocalTime): Assert<TimeRange> {
    given { timeRange ->
        if (timeRange.from != expected) {
            expected(" from value to be '$expected', but was ${timeRange.from}", expected, timeRange.from)
        }
    }
    return this
}

private fun Assert<TimeRange>.hasTo(expected: LocalTime): Assert<TimeRange> {
    given { timeRange ->
        if (timeRange.to != expected) {
            expected(" to value to be '$expected', but was ${timeRange.to}", expected, timeRange.to)
        }
    }
    return this
}

private fun Assert<TimeRangesSchedule>.hasSize(expected: Int): Assert<TimeRangesSchedule> {
    given { schedule ->
        if (schedule.size != expected) {
            expected(" size to be '$expected', but was ${schedule.size}", expected, schedule.size)
        }
    }
    return this
}
