package dev.sebastiano.bundel.preferences.schedule

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import org.junit.jupiter.api.Test
import java.time.LocalTime

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
    internal fun `should throw IAE when from is bigger than to`() {
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
}
