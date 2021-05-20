package dev.sebastiano.bundel

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.sebastiano.bundel.history.NotificationsHistoryScreen
import dev.sebastiano.bundel.notifications.BundelNotificationListenerService.Companion.NOTIFICATIONS_FLOW
import dev.sebastiano.bundel.notifications.needsNotificationsPermission
import dev.sebastiano.bundel.notificationslist.NotificationsListScreen
import dev.sebastiano.bundel.onboarding.OnboardingScreen
import dev.sebastiano.bundel.storage.RobertoRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val needsNotificationsPermission = lifecycle.eventsAsFlow()
        .filter { it == Lifecycle.Event.ON_START }
        .map { needsNotificationsPermission(this) }

    @Inject
    internal lateinit var repository: RobertoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            BundelTheme {
                NavHost(navController = navController, startDestination = NavigationRoutes.ONBOARDING) {
                    composable(NavigationRoutes.ONBOARDING) {
                        OnboardingScreen(
                            onSettingsIntentClick = { showNotificationsPreferences() },
                            onDismissClicked = { navController.navigate(NavigationRoutes.NOTIFICATIONS_LIST) },
                        )
                    }
                    composable(NavigationRoutes.NOTIFICATIONS_LIST) {
                        NotificationsListScreen(
                            onHistoryClicked = { navController.navigate(NavigationRoutes.HISTORY) }
                        )
                    }
                    composable(NavigationRoutes.HISTORY) {
                        val items by repository.getNotifications().collectAsState(initial = emptyList())
                        NotificationsHistoryScreen(items)
                    }
                }
            }
        }
    }

    @Composable
    private fun OnboardingScreen(
        viewModel: OnboardingViewModel = viewModel(),
        onSettingsIntentClick: () -> Unit,
        onDismissClicked: () -> Unit,
    ) {
        val needsNotificationsPermission by needsNotificationsPermission.collectAsState(true)

        val checkedState by viewModel.crashlyticsState.collectAsState()

        OnboardingScreen(
            needsPermission = needsNotificationsPermission,
            onSettingsIntentClick = onSettingsIntentClick,
            onDismissClicked = onDismissClicked,
            crashReportingEnabled = checkedState,
            onSwitchChanged = { viewModel.onCrashlyticsChanged(it) }
        )
    }

    @Composable
    private fun NotificationsListScreen(
        onHistoryClicked: () -> Unit
    ) {
        val notifications by remember(lifecycle) { NOTIFICATIONS_FLOW.flowWithLifecycle(lifecycle) }
            .collectAsState(emptyList())
        NotificationsListScreen(notifications, onHistoryClicked)
    }

    private fun showNotificationsPreferences() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    private object NavigationRoutes {

        const val ONBOARDING = "onboarding"
        const val NOTIFICATIONS_LIST = "notifications_list"
        const val HISTORY = "history"
    }
}

private fun Lifecycle.eventsAsFlow(): Flow<Lifecycle.Event> = callbackFlow {
    val observer = LifecycleEventObserver { _, event -> offer(event) }
    addObserver(observer)

    awaitClose { removeObserver(observer) }
}
