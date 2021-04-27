package dev.sebastiano.bundel

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.sebastiano.bundel.notifications.needsNotificationsPermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(State(false))
    val state: Flow<State> = _state

    fun checkIfNeedsNotificationsPermission(context: Context) {
        _state.value = State(needsNotificationsPermission(context))
    }

    data class State(val needsPermission: Boolean)
}
