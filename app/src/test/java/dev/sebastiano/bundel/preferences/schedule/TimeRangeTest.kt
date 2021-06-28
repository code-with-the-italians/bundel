package dev.sebastiano.bundel.preferences.schedule

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import assertk.assertions.prop
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalTime

private val anyTime = LocalTime.of(15, 0)
private val midnight = LocalTime.of(0, 0)
private val elevenPm = LocalTime.of(23, 0)
private val justBeforeMidnight = LocalTime.of(23, 59)

internal class TimeRangeTest {

    @Test
    internal fun `should create a valid instance of TimeRange correctly`() {
        val from = LocalTime.of(1, 59)
        val to = LocalTime.of(2, 0)

        assertThat(TimeRange(from, to)).all {
            prop(TimeRange::from).isEqualTo(from)
            prop(TimeRange::to).isEqualTo(to)
        }
    }

    @Test
    internal fun `should throw IAE when from is bigger than 'to'`() {
        val from = LocalTime.of(2, 0)
        val to = LocalTime.of(1, 59)

        assertThat { TimeRange(from, to) }.isFailure()
            .isInstanceOf(IllegalArgumentException::class)
    }

    @Test
    internal fun `should throw IAE when from is equal to to`() {
        val from = LocalTime.of(2, 0)
        val to = LocalTime.of(2, 0)

        assertThat { TimeRange(from, to) }.isFailure()
            .isInstanceOf(IllegalArgumentException::class)
    }

    @Nested
    inner class FromBoundariesCheck {

        @Test
        internal fun `should NOT allow decrementing 'from' HOURS when set to 00_00`() {
            assertThat(timeRange(from = midnight))
                .prop(TimeRange::canDecrementFromHours).isFalse()
        }

        @Test
        internal fun `should NOT allow decrementing 'from' MINUTES when set to 00_00`() {
            assertThat(timeRange(from = midnight))
                .prop(TimeRange::canDecrementFromMinutes).isFalse()
        }

        @Test
        internal fun `should NOT allow decrementing 'from' HOURS when set to 00_59`() {
            assertThat(timeRange(from = midnight.plusMinutes(59)))
                .prop(TimeRange::canDecrementFromHours).isFalse()
        }

        @Test
        internal fun `should allow decrementing 'from' MINUTES when set to 00_01`() {
            assertThat(timeRange(from = midnight.plusMinutes(1)))
                .prop(TimeRange::canDecrementFromMinutes).isTrue()
        }

        @Test
        internal fun `should allow decrementing 'from' HOURS when set to 01_00`() {
            assertThat(timeRange(from = midnight.plusHours(1)))
                .prop(TimeRange::canDecrementFromHours).isTrue()
        }

        @Test
        internal fun `should NOT allow incrementing 'from' HOURS when less than 1h earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(59)))
                .prop(TimeRange::canIncrementFromHours).isFalse()
        }

        @Test
        internal fun `should NOT allow incrementing 'from' HOURS when it's 1h earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusHours(1)))
                .prop(TimeRange::canIncrementFromHours).isFalse()
        }

        @Test
        internal fun `should NOT allow incrementing 'from' MINUTES when it's only 1m earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(1)))
                .prop(TimeRange::canIncrementFromMinutes).isFalse()
        }

        @Test
        internal fun `should allow incrementing 'from' HOURS when it's 1h1m earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusHours(1).plusMinutes(1)))
                .prop(TimeRange::canIncrementFromHours).isTrue()
        }

        @Test
        internal fun `should allow incrementing 'from' MINUTES when it's 2m earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(2)))
                .prop(TimeRange::canIncrementFromMinutes).isTrue()
        }

        @Test
        internal fun `should NOT allow incrementing 'from' HOURS when set to 23_58`() {
            assertThat(timeRange(from = justBeforeMidnight.minusMinutes(1)))
                .prop(TimeRange::canIncrementFromHours).isFalse()
        }

        @Test
        internal fun `should NOT allow incrementing 'from' MINUTES when set to 23_58`() {
            assertThat(timeRange(from = justBeforeMidnight.minusMinutes(1)))
                .prop(TimeRange::canIncrementFromMinutes).isFalse()
        }

        @Test
        internal fun `should NOT allow incrementing 'from' HOURS when set to 23_00`() {
            assertThat(timeRange(from = elevenPm))
                .prop(TimeRange::canIncrementFromHours).isFalse()
        }

        @Test
        internal fun `should allow incrementing 'from' MINUTES when set to 23_57 and to is 23_59`() {
            assertThat(timeRange(from = justBeforeMidnight.minusMinutes(2), to = justBeforeMidnight))
                .prop(TimeRange::canIncrementFromMinutes).isTrue()
        }

        @Test
        internal fun `should allow incrementing 'from' HOURS when set to 22_58 and to is 23_59`() {
            assertThat(timeRange(from = elevenPm.minusMinutes(2), to = justBeforeMidnight))
                .prop(TimeRange::canIncrementFromMinutes).isTrue()
        }
    }

    @Nested
    inner class ToBoundariesCheck {

        @Test
        internal fun `should NOT allow decrementing 'to' HOURS when from is midnight and to is set to 00_01`() {
            assertThat(timeRange(from = midnight, to = midnight.plusMinutes(1)))
                .prop(TimeRange::canDecrementToHours).isFalse()
        }

        @Test
        internal fun `should NOT allow decrementing 'to' HOURS when from is midnight and to is set to 01_00`() {
            assertThat(timeRange(from = midnight, to = midnight.plusHours(1)))
                .prop(TimeRange::canDecrementToHours).isFalse()
        }

        @Test
        internal fun `should NOT allow decrementing 'to' MINUTES when from is midnight and to is set to 00_01`() {
            assertThat(timeRange(from = midnight, to = midnight.plusMinutes(1)))
                .prop(TimeRange::canDecrementToMinutes).isFalse()
        }

        @Test
        internal fun `should NOT allow decrementing 'to' HOURS when set to from + 1h`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusHours(1)))
                .prop(TimeRange::canDecrementToHours).isFalse()
        }

        @Test
        internal fun `should NOT allow decrementing 'to' MINUTES when set to from + 1m`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(1)))
                .prop(TimeRange::canDecrementToMinutes).isFalse()
        }

        @Test
        internal fun `should allow decrementing 'to' HOURS when set to from + 1h1m or later`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusHours(1).plusMinutes(1)))
                .prop(TimeRange::canDecrementToHours).isTrue()
        }

        @Test
        internal fun `should allow decrementing 'to' MINUTES when set to from + 2m or later`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(2)))
                .prop(TimeRange::canDecrementToMinutes).isTrue()
        }

        @Test
        internal fun `should NOT allow incrementing 'to' HOURS when it's 23_00 or later`() {
            assertThat(timeRange(from = anyTime, to = elevenPm))
                .prop(TimeRange::canIncrementToHours).isFalse()
        }

        @Test
        internal fun `should NOT allow incrementing 'to' MINUTES when it's 23_59`() {
            assertThat(timeRange(from = anyTime, to = justBeforeMidnight))
                .prop(TimeRange::canIncrementToMinutes).isFalse()
        }

        @Test
        internal fun `should allow incrementing 'to' HOURS when it's it's 22_59`() {
            assertThat(timeRange(from = anyTime, to = elevenPm.minusMinutes(1)))
                .prop(TimeRange::canIncrementToHours).isTrue()
        }

        @Test
        internal fun `should allow incrementing 'to' MINUTES when it's it's 23_58`() {
            assertThat(timeRange(from = anyTime, to = justBeforeMidnight.minusMinutes(1)))
                .prop(TimeRange::canIncrementToMinutes).isTrue()
        }
    }

    private fun timeRange(
        from: LocalTime = LocalTime.of(1, 0),
        to: LocalTime = from.plusMinutes(1)
    ) = TimeRange(from, to)
}
