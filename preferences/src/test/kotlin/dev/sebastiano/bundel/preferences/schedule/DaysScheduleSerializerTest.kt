package dev.sebastiano.bundel.preferences.schedule

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import dev.sebastiano.bundel.ui.composables.WeekDay
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
internal class DaysScheduleSerializerTest {

    inner class Serialize {

        @Test
        fun `should throw an error when the map is empty`() {
            assertThat { DaysScheduleSerializer.serializeToString(emptyMap()) }.isFailure()
                .isInstanceOf(IllegalArgumentException::class)
        }

        @Test
        fun `should create a valid string from a non-empty map`() {
            val schedule = mapOf(WeekDay.MONDAY to true, WeekDay.FRIDAY to false)
            assertThat(DaysScheduleSerializer.serializeToString(schedule))
                .isEqualTo("MONDAY=true,FRIDAY=false")
        }
    }

    inner class Deserialize {

        @Test
        fun `should throw an error when the string is blank`() {
            assertThat { DaysScheduleSerializer.deserializeFromString(" ") }.isFailure()
                .isInstanceOf(IllegalArgumentException::class)
        }

        @Test
        fun `should create a valid map from a non-empty string`() {
            val rawSchedule = "MONDAY=true,FRIDAY=false"
            assertThat(DaysScheduleSerializer.deserializeFromString(rawSchedule))
                .isEqualTo(mapOf(WeekDay.MONDAY to true, WeekDay.FRIDAY to false))
        }
    }
}
