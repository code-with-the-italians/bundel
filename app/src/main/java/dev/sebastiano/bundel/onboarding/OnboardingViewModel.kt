package dev.sebastiano.bundel.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.ui.composables.TimeRange
import dev.sebastiano.bundel.ui.composables.WeekDay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    val timeRangesScheduleFlow = preferences.getTimeRangesSchedule()
    val daysScheduleFlow = preferences.getDaysSchedule()
    val crashReportingEnabledFlowrina = preferences.isCrashlyticsEnabled()

    fun setCrashReportingEnabled(enabled: Boolean) {
        Timber.d("Crashlytics is enabled: $enabled")
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)

        viewModelScope.launch {
            preferences.setIsCrashlyticsEnabled(enabled)
        }
    }

    fun onDaysScheduleChangeWeekDay(weekDay: WeekDay, active: Boolean) {
        Timber.d("Schedule day ${weekDay.name} active changed: $active")

        viewModelScope.launch {
            val daysScheduleValue = daysScheduleFlow.first().toMutableMap()
            daysScheduleValue[weekDay] = active
            preferences.setDaysSchedule(daysScheduleValue)
        }
    }

    fun onTimeRangesScheduleAddTimeRange() {
        Timber.d("Adding time range to schedule")

        viewModelScope.launch {
            val newSchedule = timeRangesScheduleFlow.first().appendTimeRange()
            preferences.setTimeRangesSchedule(newSchedule)
        }
    }

    fun onTimeRangesScheduleRemoveTimeRange(timeRange: TimeRange) {
        Timber.d("Removing time range from schedule: $timeRange")

        viewModelScope.launch {
            val newSchedule = timeRangesScheduleFlow.first().removeRange(timeRange)
            preferences.setTimeRangesSchedule(newSchedule)
        }
    }

    fun onTimeRangesScheduleChangeTimeRange(old: TimeRange, new: TimeRange) {
        Timber.d("Changing time range in schedule from: $old, to: $new")

        viewModelScope.launch {
            val newSchedule = timeRangesScheduleFlow.first().updateRange(old, new)
            preferences.setTimeRangesSchedule(newSchedule)
        }
    }
}
