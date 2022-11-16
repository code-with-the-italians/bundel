package dev.sebastiano.bundel.schedule

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

internal class ScheduleCheckerTest {

    private val weekendDateTime = LocalDateTime.of(2021, 9, 12, 17, 45, 0, 0)
    private val weekdayDateTime = LocalDateTime.of(2021, 9, 8, 16, 45, 0, 0)

    private val daysSchedule = mapOf(
        WeekDay.MONDAY to true,
        WeekDay.TUESDAY to true,
        WeekDay.WEDNESDAY to true,
        WeekDay.THURSDAY to true,
        WeekDay.FRIDAY to true,
        WeekDay.SATURDAY to false,
        WeekDay.SUNDAY to false
    )

    private val timeRangesSchedule = TimeRangesSchedule.of(
        TimeRange(from = LocalTime.of(14, 0), to = LocalTime.of(18, 0))
    )

    @Nested
    inner class IsSnoozeActive {

        @Test
        internal fun `should return false when the days schedule is empty`() {
            val emptyDaysSchedule = emptyMap<WeekDay, Boolean>()
            assertThat(ScheduleChecker.isSnoozeActive(weekendDateTime, emptyDaysSchedule, timeRangesSchedule)).isFalse()
        }

        @Test
        internal fun `should return false when now is on a day that's not active`() {
            assertThat(ScheduleChecker.isSnoozeActive(weekendDateTime, daysSchedule, timeRangesSchedule)).isFalse()
        }

        @Test
        internal fun `should return false when now is on a good day but it's before the start of the time range`() {
            val from = timeRangesSchedule.first().from
            val dateTime = weekdayDateTime.withHour(from.hour)
                .withMinute(from.minute)
                .minusMinutes(1)
            assertThat(ScheduleChecker.isSnoozeActive(dateTime, daysSchedule, timeRangesSchedule)).isFalse()
        }

        @Test
        internal fun `should return false when now is on a good day but it's after the end of the time range`() {
            val to = timeRangesSchedule.last().to
            val dateTime = weekdayDateTime.withHour(to.hour)
                .withMinute(to.minute)
                .plusMinutes(1)
            assertThat(ScheduleChecker.isSnoozeActive(dateTime, daysSchedule, timeRangesSchedule)).isFalse()
        }

        @Test
        internal fun `should return true when now is on a good day at the beginning of the time range`() {
            val from = timeRangesSchedule.first().from
            val dateTime = weekdayDateTime.withHour(from.hour)
                .withMinute(from.minute)
            assertThat(ScheduleChecker.isSnoozeActive(dateTime, daysSchedule, timeRangesSchedule)).isTrue()
        }

        @Test
        internal fun `should return true when now is on a good day at the end of the time range`() {
            val to = timeRangesSchedule.last().to
            val dateTime = weekdayDateTime.withHour(to.hour)
                .withMinute(to.minute)
            assertThat(ScheduleChecker.isSnoozeActive(dateTime, daysSchedule, timeRangesSchedule)).isTrue()
        }

        @Test
        internal fun `should return true when now is on a good day within the time range`() {
            val from = timeRangesSchedule.first().from
            val to = timeRangesSchedule.last().to
            val dateTime = weekdayDateTime.withHour(from.hour + (to.hour - from.hour) / 3)
                .withMinute(from.minute + (to.minute - from.minute) / 3)
            assertThat(ScheduleChecker.isSnoozeActive(dateTime, daysSchedule, timeRangesSchedule)).isTrue()
        }

        @Test
        internal fun `should return false when now is on a good day between two time ranges`() {
            val timeRangesSchedule = TimeRangesSchedule.of(
                TimeRange(from = LocalTime.of(9, 0), to = LocalTime.of(12, 0)),
                TimeRange(from = LocalTime.of(14, 0), to = LocalTime.of(18, 0))
            )
            val dateTime = weekdayDateTime.withHour(13)
                .withMinute(31)
            assertThat(ScheduleChecker.isSnoozeActive(dateTime, daysSchedule, timeRangesSchedule)).isFalse()
        }
    }

    @Nested
    inner class CalculateSnoozeDelay {

        // - snooze by 15' if we are at the beginning of the range
        // - snooze by 10' if we are 5' into the range
        // - snooze by 15' if we are 15' into the range

        @Test
        internal fun `should throw IAE if now is not during snooze time`() {
            assertThat { ScheduleChecker.calculateSnoozeDelay(weekendDateTime, daysSchedule, timeRangesSchedule) }.isFailure()
                .isInstanceOf(IllegalArgumentException::class)
        }

        @Test
        internal fun `should snooze by 15 minutes if now is at the beginning of the range`() {
            val dateTime = weekdayDateTime.withHour(14)
                .withMinute(0)
            val expectedDateTime = dateTime.plusMinutes(15)
            val expectedMillis = Duration.between(dateTime, expectedDateTime).toMillis().toInt()

            val actual = ScheduleChecker.calculateSnoozeDelay(dateTime, daysSchedule, timeRangesSchedule)
            assertThat(actual).isEqualTo(expectedMillis)
        }

        @Test
        internal fun `should snooze by 10 minutes if now is 5 minutes into the range`() {
            val dateTime = weekdayDateTime.withHour(14)
                .withMinute(5)
            val expectedDateTime = dateTime.plusMinutes(10)
            val expectedMillis = Duration.between(dateTime, expectedDateTime).toMillis().toInt()

            val actual = ScheduleChecker.calculateSnoozeDelay(dateTime, daysSchedule, timeRangesSchedule)
            assertThat(actual).isEqualTo(expectedMillis)
        }

        @Test
        internal fun `should snooze by 15 minutes if now is 15 minutes into the range`() {
            val dateTime = weekdayDateTime.withHour(14)
                .withMinute(15)
            val expectedDateTime = dateTime.plusMinutes(15)
            val expectedMillis = Duration.between(dateTime, expectedDateTime).toMillis().toInt()

            val actual = ScheduleChecker.calculateSnoozeDelay(dateTime, daysSchedule, timeRangesSchedule)
            assertThat(actual).isEqualTo(expectedMillis)
        }
    }
}
