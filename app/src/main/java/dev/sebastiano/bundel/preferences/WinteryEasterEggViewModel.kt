package dev.sebastiano.bundel.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

@HiltViewModel
internal class WinteryEasterEggViewModel @Inject constructor(
    private val preferences: Preferences
) : ViewModel() {

    val easterEggEnabledFlow: Flow<Boolean> = preferences.isWinteryEasterEggEnabled()

    fun isWinteryEasterEggPeriod(): Boolean {
        val now = LocalDate.now()
        return now.month == Month.DECEMBER && now.dayOfMonth in 21..31
    }

    fun shouldShowWinteryEasterEgg(): Flow<Boolean> =
        preferences.isWinteryEasterEggEnabled()
            .map { enabled -> enabled /*&& isWinteryEasterEggPeriod()*/ } // STOPSHIP restore condition

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setWinteryEasterEggEnabled(enabled)
        }
    }
}
