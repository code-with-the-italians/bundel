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
        internal fun `should NOT allow decreasing 'from' HOURS when set to 00_00`() {
            assertThat(timeRange(from = midnight))
                .prop(TimeRange::canDecreaseFromHours).isFalse()
        }

        @Test
        internal fun `should NOT allow decreasing 'from' MINUTES when set to 00_00`() {
            assertThat(timeRange(from = midnight))
                .prop(TimeRange::canDecreaseFromMinutes).isFalse()
        }

        @Test
        internal fun `should NOT allow decreasing 'from' HOURS when set to 00_59`() {
            assertThat(timeRange(from = midnight.plusMinutes(59)))
                .prop(TimeRange::canDecreaseFromHours).isFalse()
        }

        @Test
        internal fun `should allow decreasing 'from' MINUTES when set to 00_01`() {
            assertThat(timeRange(from = midnight.plusMinutes(1)))
                .prop(TimeRange::canDecreaseFromMinutes).isTrue()
        }

        @Test
        internal fun `should allow decreasing 'from' HOURS when set to 01_00`() {
            assertThat(timeRange(from = midnight.plusHours(1)))
                .prop(TimeRange::canDecreaseFromHours).isTrue()
        }

        @Test
        internal fun `should NOT allow increasing 'from' HOURS when less than 1h earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(59)))
                .prop(TimeRange::canIncreaseFromHours).isFalse()
        }

        @Test
        internal fun `should NOT allow increasing 'from' HOURS when it's 1h earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusHours(1)))
                .prop(TimeRange::canIncreaseFromHours).isFalse()
        }

        @Test
        internal fun `should NOT allow increasing 'from' MINUTES when it's only 1m earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(1)))
                .prop(TimeRange::canIncreaseFromMinutes).isFalse()
        }

        @Test
        internal fun `should allow increasing 'from' HOURS when it's 1h1m earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusHours(1).plusMinutes(1)))
                .prop(TimeRange::canIncreaseFromHours).isTrue()
        }

        @Test
        internal fun `should allow increasing 'from' MINUTES when it's 2m earlier than 'to'`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(2)))
                .prop(TimeRange::canIncreaseFromMinutes).isTrue()
        }

        @Test
        internal fun `should NOT allow increasing 'from' HOURS when set to 23_58`() {
            assertThat(timeRange(from = justBeforeMidnight.minusMinutes(1)))
                .prop(TimeRange::canIncreaseFromHours).isFalse()
        }

        @Test
        internal fun `should NOT allow increasing 'from' MINUTES when set to 23_58`() {
            assertThat(timeRange(from = justBeforeMidnight.minusMinutes(1)))
                .prop(TimeRange::canIncreaseFromMinutes).isFalse()
        }

        @Test
        internal fun `should NOT allow increasing 'from' HOURS when set to 23_00`() {
            assertThat(timeRange(from = elevenPm))
                .prop(TimeRange::canIncreaseFromHours).isFalse()
        }

        @Test
        internal fun `should allow increasing 'from' MINUTES when set to 23_57 and to is 23_59`() {
            assertThat(timeRange(from = justBeforeMidnight.minusMinutes(2), to = justBeforeMidnight))
                .prop(TimeRange::canIncreaseFromMinutes).isTrue()
        }

        @Test
        internal fun `should allow increasing 'from' HOURS when set to 22_58 and to is 23_59`() {
            assertThat(timeRange(from = elevenPm.minusMinutes(2), to = justBeforeMidnight))
                .prop(TimeRange::canIncreaseFromMinutes).isTrue()
        }
    }

    @Nested
    inner class ToBoundariesCheck {

        @Test
        internal fun `should NOT allow decreasing 'to' HOURS when set to from + 1h or earlier`() {
            assertThat(timeRange(from = anyTime,to = anyTime.plusHours(1)))
                .prop(TimeRange::canDecreaseToHours).isFalse()
        }

        @Test
        internal fun `should NOT allow decreasing 'to' MINUTES when set to from + 1m`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(1)))
                .prop(TimeRange::canDecreaseToMinutes).isFalse()
        }

        @Test
        internal fun `should allow decreasing 'to' HOURS when set to from + 1h1m or later`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusHours(1).plusMinutes(1)))
                .prop(TimeRange::canDecreaseToHours).isTrue()
        }

        @Test
        internal fun `should allow decreasing 'to' MINUTES when set to from + 2m or later`() {
            assertThat(timeRange(from = anyTime, to = anyTime.plusMinutes(2)))
                .prop(TimeRange::canDecreaseToMinutes).isTrue()
        }

        @Test
        internal fun `should NOT allow increasing 'to' HOURS when it's 23_00 or later`() {
            assertThat(timeRange(from = anyTime, to = elevenPm))
                .prop(TimeRange::canIncreaseToHours).isFalse()
        }

        @Test
        internal fun `should NOT allow increasing 'to' MINUTES when it's 23_59`() {
            assertThat(timeRange(from = anyTime, to = justBeforeMidnight))
                .prop(TimeRange::canIncreaseToMinutes).isFalse()
        }

        @Test
        internal fun `should allow increasing 'to' HOURS when it's it's 22_59 or earlier`() {
            assertThat(timeRange(from = anyTime, to = elevenPm.minusMinutes(1)))
                .prop(TimeRange::canIncreaseToHours).isTrue()
        }

        @Test
        internal fun `should allow increasing 'to' MINUTES when it's it's 23_58 or earlier`() {
            assertThat(timeRange(from = anyTime, to = justBeforeMidnight.minusMinutes(1)))
                .prop(TimeRange::canIncreaseToMinutes).isTrue()
        }
    }

    private fun timeRange(
        from: LocalTime = LocalTime.of(1, 0),
        to: LocalTime = from.plusMinutes(1)
    ) = TimeRange(from, to)
}
