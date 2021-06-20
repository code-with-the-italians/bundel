package dev.sebastiano.bundel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import dev.sebastiano.bundel.storage.PreferenceStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : ViewModel() {

    val hoursSchedule = MutableStateFlow(emptyList<TimeRange>())
    val daysSchedule = MutableStateFlow(emptyMap<WeekDay, Boolean>())
    val crashReportingEnabledFlow = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            crashReportingEnabledFlow.emit(preferenceStorage.isCrashlyticsEnabled())
            daysSchedule.emit(preferenceStorage.getScheduleActiveDays())
            hoursSchedule.emit(preferenceStorage.getScheduleActiveHours())
        }
    }

    fun setCrashReportingEnabled(enabled: Boolean) {
        Timber.d("Crashlytics is enabled: $enabled")
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)

        viewModelScope.launch {
            preferenceStorage.setIsCrashlyticsEnabled(enabled)
            crashReportingEnabledFlow.emit(enabled)
        }
    }

    fun onScheduleDayActiveChanged(day: WeekDay, active: Boolean) {
        Timber.d("Schedule day ${day.name} active changed: $active")

        val daysScheduleValue = daysSchedule.value.toMutableMap()
        daysScheduleValue[day] = active

        viewModelScope.launch {
            preferenceStorage.setScheduleActiveDays(daysScheduleValue)
            daysSchedule.emit(daysScheduleValue)
        }
    }

    fun onScheduleHoursAddTimeRange() {
        val timeRange = hoursSchedule.value.last().let { timeRange ->
            TimeRange(from = timeRange.to.plusMinutes(1), to = timeRange.to.plusMinutes(60))
        }
        Timber.d("Adding time range to schedule: $timeRange")

        // TODO switch from List<TimeRange> to a smarter container that knows day boundaries, sorting, etc
        val marksSchedule = hoursSchedule.value + timeRange

        viewModelScope.launch {
            preferenceStorage.setScheduleActiveHours(marksSchedule)
            hoursSchedule.emit(marksSchedule)
        }
    }

    fun onScheduleHoursRemoveTimeRange(timeRange: TimeRange) {
        Timber.d("Removing time range from schedule: $timeRange")

        // TODO switch from List<TimeRange> to a smarter container that knows day boundaries, sorting, etc
        val newSchedule = hoursSchedule.value - timeRange

        viewModelScope.launch {
            preferenceStorage.setScheduleActiveHours(newSchedule)
            hoursSchedule.emit(newSchedule)
        }
    }

    fun onScheduleHoursChangeTimeRange(old: TimeRange, new: TimeRange) {
        Timber.d("Changing time range in schedule from: $old, to: $new")

        // TODO switch from List<TimeRange> to a smarter container that knows day boundaries, sorting, etc
        val newSchedule = hoursSchedule.value.toMutableList()
        val oldIndex = newSchedule.indexOf(old)
        require(oldIndex >= 0) { "Trying to remove time range not in schedule: $old" }

        newSchedule.remove(old)
        newSchedule.add(oldIndex, new)

        viewModelScope.launch {
            preferenceStorage.setScheduleActiveHours(newSchedule)
            hoursSchedule.emit(newSchedule)
        }
    }
}
