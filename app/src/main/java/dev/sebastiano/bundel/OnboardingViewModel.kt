package dev.sebastiano.bundel

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sebastiano.bundel.notifications.needsNotificationsPermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(State(false))
    val state: Flow<State> = _state

    fun checkIfNeedsNotificationsPermission() {
        _state.value = State(needsNotificationsPermission(application))
    }

    data class State(val needsPermission: Boolean)
}
