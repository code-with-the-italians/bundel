package dev.sebastiano.bundel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    private val preferences: Preferences
) : ViewModel() {

    val hoursSchedule = preferences.getScheduleActiveHours()
    val daysSchedule = preferences.getScheduleActiveDays()
    val crashReportingEnabledFlowrina = preferences.isCrashlyticsEnabled()

    fun setCrashReportingEnabled(enabled: Boolean) {
        Timber.d("Crashlytics is enabled: $enabled")
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)

        viewModelScope.launch {
            preferences.setIsCrashlyticsEnabled(enabled)
        }
    }

    fun onScheduleDayActiveChanged(day: WeekDay, active: Boolean) {
        Timber.d("Schedule day ${day.name} active changed: $active")

        viewModelScope.launch {
            val daysScheduleValue = daysSchedule.first().toMutableMap()
            daysScheduleValue[day] = active
            preferences.setScheduleActiveDays(daysScheduleValue)
        }
    }

    fun onScheduleHoursAddTimeRange() {
        Timber.d("Adding time range to schedule")

        viewModelScope.launch {
            val newSchedule = hoursSchedule.first().appendTimeRange()
            preferences.setScheduleActiveHours(newSchedule)
        }
    }

    fun onScheduleHoursRemoveTimeRange(timeRange: TimeRange) {
        Timber.d("Removing time range from schedule: $timeRange")

        viewModelScope.launch {
            val newSchedule = hoursSchedule.first().removeRange(timeRange)
            preferences.setScheduleActiveHours(newSchedule)
        }
    }

    fun onScheduleHoursChangeTimeRange(old: TimeRange, new: TimeRange) {
        Timber.d("Changing time range in schedule from: $old, to: $new")

        viewModelScope.launch {
            val newSchedule = hoursSchedule.first().updateRange(old, new)
            preferences.setScheduleActiveHours(newSchedule)
        }
    }
}
