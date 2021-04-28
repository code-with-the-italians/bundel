package dev.sebastiano.bundel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class NotificationsListViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(State(emptyList(), false))
    val state: Flow<State> = _state

    fun startObserving() {
        viewModelScope.launch {
            BundelNotificationListenerService.notificationsFlow.collect { notifications ->
                _state.value = State(notifications, isConnected = true)
            }
        }
    }

    data class State(
        val notifications: List<String>,
        val isConnected: Boolean
    ) {

        companion object {

            val EMPTY = State(emptyList(), isConnected = false)
        }
    }
}
