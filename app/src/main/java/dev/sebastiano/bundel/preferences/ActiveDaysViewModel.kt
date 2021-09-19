package dev.sebastiano.bundel.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ActiveDaysViewModel @Inject constructor(
    private val preferences: Preferences
) : ViewModel() {

    val daysScheduleFlow = preferences.getDaysSchedule()

    fun onDaysScheduleChangeWeekDay(weekDay: WeekDay, active: Boolean) {
        Timber.d("Schedule day ${weekDay.name} active changed: $active")

        viewModelScope.launch {
            val daysScheduleValue = daysScheduleFlow.first().toMutableMap()
            daysScheduleValue[weekDay] = active
            preferences.setDaysSchedule(daysScheduleValue)
        }
    }
}
