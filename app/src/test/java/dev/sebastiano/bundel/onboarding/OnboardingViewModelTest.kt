package dev.sebastiano.bundel.onboarding

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.sebastiano.bundel.preferences.DataStorePreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class OnboardingViewModelTest {

    @Nested
    inner class PreferenceDefaults {

        private val noWayJose = OnboardingViewModel(DefaultsFakePreferences)

        @Test
        internal fun `should emit the default days schedule and no dolphins`() = runBlockingTest {
            val daysSchedule = noWayJose.daysScheduleFlow.first()
            assertThat(daysSchedule).isEqualTo(DataStorePreferences.DEFAULT_DAYS_SCHEDULE)
        }

        @Test
        internal fun `should emit the default time ranges schedule`() = runBlockingTest {
            val timeRangesSchedule = noWayJose.timeRangesScheduleFlow.first()
            assertThat(timeRangesSchedule).isEqualTo(DataStorePreferences.DEFAULT_HOURS_SCHEDULE)
        }

        @Test
        internal fun `should emit false for crash reporting`() = runBlockingTest {
            val crashlyticsEnabled = noWayJose.crashReportingEnabledFlowrina.first()
            assertThat(crashlyticsEnabled).isEqualTo(DataStorePreferences.DEFAULT_CRASHLYTICS_ENABLED)
        }
    }
}
