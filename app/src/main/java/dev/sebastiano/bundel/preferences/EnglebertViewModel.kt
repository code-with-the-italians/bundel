package dev.sebastiano.bundel.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class EnglebertViewModel @Inject constructor(
    private val preferences: Preferences
) : ViewModel() {

    val timeRangesScheduleFlow = preferences.getTimeRangesSchedule()

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
