package dev.sebastiano.bundel

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import dev.sebastiano.bundel.navigation.NavigationRoute
import dev.sebastiano.bundel.navigation.mainScreenGraph
import dev.sebastiano.bundel.navigation.onboardingGraph
import dev.sebastiano.bundel.navigation.settingsGraph
import dev.sebastiano.bundel.navigation.splashScreenGraph
import dev.sebastiano.bundel.notifications.needsNotificationsPermission
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.storage.DataRepository
import dev.sebastiano.bundel.ui.BundelTheme
import dev.sebastiano.bundel.ui.defaultEnterTransition
import dev.sebastiano.bundel.ui.defaultExitTransition
import dev.sebastiano.bundel.ui.defaultPopEnterTransition
import dev.sebastiano.bundel.ui.defaultPopExitTransition
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // TODO Try to bring this into the OnboardingViewModel
    private val needsNotificationsPermission = lifecycle.eventsAsFlow()
        .filter { it == Lifecycle.Event.ON_START }
        .map { needsNotificationsPermission(this) }

    @Inject
    internal lateinit var repository: DataRepository

    @Inject
    internal lateinit var preferences: Preferences

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberAnimatedNavController()
            val systemUiController = rememberSystemUiController()

            BundelTheme {
                SetupSystemUi(systemUiController)

                AnimatedNavHost(
                    navController = navController,
                    startDestination = NavigationRoute.SplashScreen.route,
                    enterTransition = { initial, target -> defaultEnterTransition(initial, target) },
                    exitTransition = { initial, target -> defaultExitTransition(initial, target) },
                    popEnterTransition = { _, _ -> defaultPopEnterTransition() },
                    popExitTransition = { _, _ -> defaultPopExitTransition() }
                ) {
                    splashScreenGraph(preferences) { navigationRoute ->
                        navController.navigate(navigationRoute.route)
                    }
                    onboardingGraph(
                        navController = navController,
                        needsNotificationsPermissionFlow = needsNotificationsPermission,
                        preferences = preferences,
                        onOpenSettingsClick = { openNotificationsPreferences() }
                    )
                    mainScreenGraph(navController, lifecycle, repository, preferences)
                    settingsGraph(navController)
                }
            }
        }
    }

    @Composable
    private fun SetupSystemUi(systemUiController: SystemUiController) {
        val barsColor = MaterialTheme.colors.primaryVariant
        SideEffect {
            systemUiController.setStatusBarColor(color = barsColor)
            systemUiController.setNavigationBarColor(color = barsColor)
        }
    }

    private fun openNotificationsPreferences() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}

private fun Lifecycle.eventsAsFlow(): Flow<Lifecycle.Event> = callbackFlow {
    val observer = LifecycleEventObserver { _, event -> trySend(event) }
    addObserver(observer)

    awaitClose { removeObserver(observer) }
}
