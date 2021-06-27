package dev.sebastiano.bundel.preferences.schedule

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalTime

internal class TimePickerModelTest {

    private val timeRange = TimeRange(
        from = LocalTime.of(10, 0),
        to = LocalTime.of(12, 0)
    )

    @Nested
    inner class FromBoundaryChecks {

        @Test
        internal fun `should allow increasing FROM HOUR when doesn't overlap with TO`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.HOUR
            )
            assertThat(model.canIncrement).isTrue()
        }

        @Test
        internal fun `should allow increasing FROM MINUTE when doesn't overlap with TO`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.MINUTE
            )
            assertThat(model.canIncrement).isTrue()
        }

        @Test
        internal fun `should NOT allow increasing FROM HOUR when overlaps with TO`() {
            val model = TimePickerModel(
                timeRange = TimeRange(
                    from = LocalTime.of(10, 0),
                    to = LocalTime.of(11, 0)
                ),
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.HOUR
            )
            assertThat(model.canIncrement).isFalse()
        }

        @Test
        internal fun `should NOT allow increasing FROM MINUTE when overlaps with TO`() {
            val model = TimePickerModel(
                timeRange = TimeRange(
                    from = LocalTime.of(10, 0),
                    to = LocalTime.of(10, 1)
                ),
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.MINUTE
            )
            assertThat(model.canIncrement).isFalse()
        }

        @Test
        internal fun `should NOT allow decreasing FROM HOUR when overlaps with minimumAllowableFrom`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.HOUR,
                minimumAllowableFrom = timeRange.from.minusHours(1)
            )
            assertThat(model.canDecrement).isFalse()
        }

        @Test
        internal fun `should NOT allow decreasing FROM MINUTE when overlaps with minimumAllowableFrom`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.MINUTE,
                minimumAllowableFrom = timeRange.from.minusMinutes(1)
            )
            assertThat(model.canDecrement).isFalse()
        }
    }

    @Nested
    inner class ToBoundaryChecks {

        @Test
        internal fun `should allow increasing TO HOUR when doesn't overlap with FROM`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.HOUR
            )
            assertThat(model.canIncrement).isTrue()
        }

        @Test
        internal fun `should allow increasing TO MINUTE when doesn't overlap with FROM`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.MINUTE
            )
            assertThat(model.canIncrement).isTrue()
        }

        @Test
        internal fun `should NOT allow decreasing TO HOUR when overlaps with FROM`() {
            val model = TimePickerModel(
                timeRange = TimeRange(
                    from = LocalTime.of(10, 0),
                    to = LocalTime.of(11, 0)
                ),
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.HOUR
            )
            assertThat(model.canDecrement).isFalse()
        }

        @Test
        internal fun `should NOT allow decreasing TO MINUTE when overlaps with FROM`() {
            val model = TimePickerModel(
                timeRange = TimeRange(
                    from = LocalTime.of(10, 0),
                    to = LocalTime.of(10, 1)
                ),
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.MINUTE
            )
            assertThat(model.canDecrement).isFalse()
        }

        @Test
        internal fun `should NOT allow increasing TO HOUR when overlaps with maximumAllowableTo`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.HOUR,
                maximumAllowableTo = timeRange.to.plusHours(1)
            )
            assertThat(model.canIncrement).isFalse()
        }

        @Test
        internal fun `should NOT allow increasing TO MINUTE when overlaps with maximumAllowableTo`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.MINUTE,
                maximumAllowableTo = timeRange.to.plusMinutes(1)
            )
            assertThat(model.canIncrement).isFalse()
        }
    }

    @Nested
    inner class Incrementing {

        @Test
        internal fun `should increment FROM HOUR when selected`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.HOUR
            )

            assertAll {
                val newTimeRange = model.incrementTimeRangePart()
                assertThat(newTimeRange.from).isEqualTo(timeRange.from.plusHours(1))
                assertThat(newTimeRange.to).isEqualTo(timeRange.to)
            }
        }

        @Test
        internal fun `should increment FROM MINUTE when selected`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.MINUTE
            )

            assertAll {
                val newTimeRange = model.incrementTimeRangePart()
                assertThat(newTimeRange.from).isEqualTo(timeRange.from.plusMinutes(1))
                assertThat(newTimeRange.to).isEqualTo(timeRange.to)
            }
        }

        @Test
        internal fun `should increment TO HOUR when selected`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.HOUR
            )

            assertAll {
                val newTimeRange = model.incrementTimeRangePart()
                assertThat(newTimeRange.from).isEqualTo(timeRange.from)
                assertThat(newTimeRange.to).isEqualTo(timeRange.to.plusHours(1))
            }
        }

        @Test
        internal fun `should increment TO MINUTE when selected`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.MINUTE
            )

            assertAll {
                val newTimeRange = model.incrementTimeRangePart()
                assertThat(newTimeRange.from).isEqualTo(timeRange.from)
                assertThat(newTimeRange.to).isEqualTo(timeRange.to.plusMinutes(1))
            }
        }
    }

    @Nested
    inner class Decrementing {

        @Test
        internal fun `should decrement FROM HOUR when selected`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.HOUR
            )

            assertAll {
                val newTimeRange = model.decrementTimeRangePart()
                assertThat(newTimeRange.from).isEqualTo(timeRange.from.minusHours(1))
                assertThat(newTimeRange.to).isEqualTo(timeRange.to)
            }
        }

        @Test
        internal fun `should decrement FROM MINUTE when selected`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.MINUTE
            )

            assertAll {
                val newTimeRange = model.decrementTimeRangePart()
                assertThat(newTimeRange.from).isEqualTo(timeRange.from.minusMinutes(1))
                assertThat(newTimeRange.to).isEqualTo(timeRange.to)
            }
        }

        @Test
        internal fun `should decrement TO HOUR when selected`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.HOUR
            )

            assertAll {
                val newTimeRange = model.decrementTimeRangePart()
                assertThat(newTimeRange.from).isEqualTo(timeRange.from)
                assertThat(newTimeRange.to).isEqualTo(timeRange.to.minusHours(1))
            }
        }

        @Test
        internal fun `should decrement TO MINUTE when selected`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.MINUTE
            )

            assertAll {
                val newTimeRange = model.decrementTimeRangePart()
                assertThat(newTimeRange.from).isEqualTo(timeRange.from)
                assertThat(newTimeRange.to).isEqualTo(timeRange.to.minusMinutes(1))
            }
        }
    }
}
