package dev.sebastiano.bundel

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.sebastiano.bundel.notifications.needsNotificationsPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val needsNotificationsPermission = lifecycle.eventsAsFlow()
        .filter { it == Lifecycle.Event.ON_START }
        .map { needsNotificationsPermission(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = NavigationRoutes.ONBOARDING) {
                composable(NavigationRoutes.ONBOARDING) {
                    OnboardingScreen(
                        onSettingsIntentClick = { showNotificationsPreferences() },
                        onDismissClicked = { navController.navigate(NavigationRoutes.NOTIFICATIONS_LIST) }
                    )
                }
                composable(NavigationRoutes.NOTIFICATIONS_LIST) { NotificationsListScreen() }
            }
        }
    }

    @Composable
    private fun OnboardingScreen(
        onSettingsIntentClick: () -> Unit,
        onDismissClicked: () -> Unit
    ) {
        val needsNotificationsPermission by needsNotificationsPermission.collectAsState(true)
        OnboardingScreen(needsNotificationsPermission, onSettingsIntentClick, onDismissClicked)
    }

    @Composable
    private fun NotificationsListScreen() {
        val notifications by remember(lifecycle) { BundelNotificationListenerService.notificationsFlow.flowWithLifecycle(lifecycle) }
            .collectAsState(emptyList())
        NotificationsListScreen(notifications)
    }

    private fun showNotificationsPreferences() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    private object NavigationRoutes {

        const val ONBOARDING = "onboarding"
        const val NOTIFICATIONS_LIST = "notifications_list"
    }
}

private fun Lifecycle.eventsAsFlow(): Flow<Lifecycle.Event> = callbackFlow {
    val observer = LifecycleEventObserver { _, event -> offer(event) }
    addObserver(observer)

    awaitClose { removeObserver(observer) }
}
