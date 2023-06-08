package dev.sebastiano.bundel.preferences.schedule

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import dev.sebastiano.bundel.ui.composables.ExpandedRangeExtremity
import dev.sebastiano.bundel.ui.composables.PartOfHour
import dev.sebastiano.bundel.ui.composables.TimePickerModel
import dev.sebastiano.bundel.ui.composables.TimeRange
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import java.time.LocalTime

@RunWith(Enclosed::class)
internal class TimePickerModelTest {

    private val timeRange = TimeRange(
        from = LocalTime.of(10, 0),
        to = LocalTime.of(12, 0)
    )

    inner class FromBoundaryChecks {

        @Test
        internal fun `should allow incrementing FROM HOUR when doesn't overlap with TO`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.HOUR
            )
            assertThat(model.canIncrement).isTrue()
        }

        @Test
        internal fun `should allow incrementing FROM MINUTE when doesn't overlap with TO`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.MINUTE
            )
            assertThat(model.canIncrement).isTrue()
        }

        @Test
        internal fun `should NOT allow incrementing FROM HOUR when overlaps with TO`() {
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
        internal fun `should NOT allow incrementing FROM MINUTE when overlaps with TO`() {
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
        internal fun `should NOT allow decrementing FROM HOUR when overlaps with minimumAllowableFrom`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.HOUR,
                minimumAllowableFrom = timeRange.from.minusHours(1)
            )
            assertThat(model.canDecrement).isFalse()
        }

        @Test
        internal fun `should NOT allow decrementing FROM MINUTE when overlaps with minimumAllowableFrom`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.FROM,
                partOfHour = PartOfHour.MINUTE,
                minimumAllowableFrom = timeRange.from.minusMinutes(1)
            )
            assertThat(model.canDecrement).isFalse()
        }
    }

    inner class ToBoundaryChecks {

        @Test
        internal fun `should allow incrementing TO HOUR when doesn't overlap with FROM`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.HOUR
            )
            assertThat(model.canIncrement).isTrue()
        }

        @Test
        internal fun `should allow incrementing TO MINUTE when doesn't overlap with FROM`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.MINUTE
            )
            assertThat(model.canIncrement).isTrue()
        }

        @Test
        internal fun `should NOT allow decrementing TO HOUR when overlaps with FROM`() {
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
        internal fun `should NOT allow decrementing TO MINUTE when overlaps with FROM`() {
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
        internal fun `should NOT allow decrementing TO HOUR when overlaps with FROM that is set to midnight`() {
            val model = TimePickerModel(
                timeRange = TimeRange(
                    from = LocalTime.of(0, 0),
                    to = LocalTime.of(1, 0)
                ),
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.HOUR
            )
            assertThat(model.canDecrement).isFalse()
        }

        @Test
        internal fun `should NOT allow decrementing TO MINUTE when overlaps with FROM that is set to midnight`() {
            val model = TimePickerModel(
                timeRange = TimeRange(
                    from = LocalTime.of(0, 0),
                    to = LocalTime.of(0, 1)
                ),
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.MINUTE
            )
            assertThat(model.canDecrement).isFalse()
        }

        @Test
        internal fun `should NOT allow incrementing TO HOUR when overlaps with maximumAllowableTo`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.HOUR,
                maximumAllowableTo = timeRange.to.plusHours(1)
            )
            assertThat(model.canIncrement).isFalse()
        }

        @Test
        internal fun `should NOT allow incrementing TO MINUTE when overlaps with maximumAllowableTo`() {
            val model = TimePickerModel(
                timeRange = timeRange,
                rangeExtremity = ExpandedRangeExtremity.TO,
                partOfHour = PartOfHour.MINUTE,
                maximumAllowableTo = timeRange.to.plusMinutes(1)
            )
            assertThat(model.canIncrement).isFalse()
        }
    }

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
