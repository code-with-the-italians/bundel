package dev.sebastiano.bundel

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController

class MainActivity : AppCompatActivity() {

    private val onboardingViewModel = OnboardingViewModel()
    private val notificationsListViewModel = NotificationsListViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = NavigationRoutes.ONBOARDING) {
                composable(NavigationRoutes.ONBOARDING) {
                    OnboardingScreen(
                        viewModel = onboardingViewModel,
                        onSettingsIntentClick = { showNotificationsPreferences() },
                        onDismissClicked = { navController.navigate(NavigationRoutes.NOTIFICATIONS_LIST) }
                    )
                }
                composable(NavigationRoutes.NOTIFICATIONS_LIST) { NotificationsListScreen(notificationsListViewModel) }
            }
        }
    }

    private fun showNotificationsPreferences() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    override fun onStart() {
        super.onStart()
        onboardingViewModel.checkIfNeedsNotificationsPermission(this)
        notificationsListViewModel.startObserving()
    }

    private object NavigationRoutes {

        const val ONBOARDING = "onboarding"
        const val NOTIFICATIONS_LIST = "notifications_list"
    }
}
