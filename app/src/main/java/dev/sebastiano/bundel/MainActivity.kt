package dev.sebastiano.bundel

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
        val viewModel = hiltNavGraphViewModel<OnboardingViewModel>()
        RunOnActivityStart { viewModel.checkIfNeedsNotificationsPermission() }

        OnboardingScreen(viewModel, onSettingsIntentClick, onDismissClicked)
    }

    @Composable
    private fun NotificationsListScreen() {
        val viewModel = hiltNavGraphViewModel<NotificationsListViewModel>()
        RunOnActivityStart { viewModel.startObserving() }
        NotificationsListScreen(viewModel)
    }

    @Composable
    private fun RunOnActivityStart(onStart: () -> Unit) {
        val lifecycleObserver = remember {
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    onStart()
                }
            }
        }

        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            lifecycle.addObserver(lifecycleObserver)
            onDispose {
                // TODO check if this gets called when closing the activity
                lifecycle.removeObserver(lifecycleObserver)
            }
        }
    }

    private fun showNotificationsPreferences() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    private object NavigationRoutes {

        const val ONBOARDING = "onboarding"
        const val NOTIFICATIONS_LIST = "notifications_list"
    }
}
